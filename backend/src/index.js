import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import crypto from 'node:crypto';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { openDb } from './db.js';
import { isMailDeliveryConfigured, sendPasswordResetEmail } from './mail.js';
import {
  initFirebaseAdmin,
  sendAdminEscalationPush,
  sendSupportReplyPush,
  verifyFirebaseIdToken,
} from './firebaseAdmin.js';
import { registerDoctorRoutes } from './doctorApi.js';

const PORT = Number.parseInt(process.env.PORT ?? '8080', 10);
const DB_FILE = process.env.DB_FILE ?? './data.sqlite';
const JWT_SECRET = process.env.JWT_SECRET ?? 'dev-secret-change-me';
const ADMIN_KEY = process.env.ADMIN_KEY ?? 'dev-admin-key-change-me';
const UPLOADS_DIR = process.env.UPLOADS_DIR ?? path.join(path.dirname(path.resolve(DB_FILE)), 'uploads');

function ensureDbParentDir(filePath) {
  const dir = path.dirname(path.resolve(filePath));
  fs.mkdirSync(dir, { recursive: true });
}

ensureDbParentDir(DB_FILE);
fs.mkdirSync(path.join(UPLOADS_DIR, 'support'), { recursive: true });
const db = openDb({ filename: DB_FILE });
initFirebaseAdmin();

const ADMIN_USERNAME = process.env.ADMIN_USERNAME ?? 'Admin';
const ADMIN_EMAIL = process.env.ADMIN_EMAIL ?? 'admin@medtest1.local';
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD ?? 'Admin123!';

function ensureAdminUser() {
  const hash = bcrypt.hashSync(ADMIN_PASSWORD, 10);
  const byUsername = db
    .prepare('SELECT id, username, email FROM users WHERE lower(username) = lower(?) LIMIT 1')
    .get(ADMIN_USERNAME);

  if (byUsername?.id) {
    db.prepare('UPDATE users SET password_hash = ?, role = ? WHERE id = ?').run(hash, 'admin', byUsername.id);
    const emailTaken = db
      .prepare('SELECT id FROM users WHERE lower(email) = lower(?) AND id != ? LIMIT 1')
      .get(ADMIN_EMAIL, byUsername.id);
    if (!emailTaken?.id) {
      db.prepare('UPDATE users SET email = ? WHERE id = ?').run(ADMIN_EMAIL, byUsername.id);
    }
    return Number(byUsername.id);
  }

  const byEmail = db
    .prepare('SELECT id, username FROM users WHERE lower(email) = lower(?) LIMIT 1')
    .get(ADMIN_EMAIL);
  if (byEmail?.id) {
    db.prepare('UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?').run(
      ADMIN_USERNAME,
      hash,
      'admin',
      byEmail.id
    );
    return Number(byEmail.id);
  }

  const ts = nowMs();
  const info = db
    .prepare(
      `INSERT INTO users (username, email, password_hash, created_at, last_login_at, role)
       VALUES (?, ?, ?, ?, ?, 'admin')`
    )
    .run(ADMIN_USERNAME, ADMIN_EMAIL, hash, ts, ts);
  return Number(info.lastInsertRowid);
}

ensureAdminUser();

function isEscalationUserMessage(text) {
  const t = String(text ?? '').trim();
  return (
    t === 'Другое — нужен специалист Умник' ||
    t === 'Позвать Умника — нужен специалист' ||
    t.includes('нужен специалист Умник') ||
    t.includes('Позвать Умника')
  );
}

const app = express();
app.use(helmet({ crossOriginResourcePolicy: { policy: 'cross-origin' } }));
app.use(cors());
app.use(express.json({ limit: '6mb' }));
app.use('/uploads', express.static(UPLOADS_DIR));
app.use(morgan('dev'));

function nowMs() {
  return Date.now();
}

function normalizeEmail(email) {
  return String(email ?? '').trim().toLowerCase();
}

function normalizeUsername(username) {
  return String(username ?? '').trim();
}

function requireAdmin(req, res, next) {
  const key = req.header('x-admin-key') ?? '';
  if (key !== ADMIN_KEY) return res.status(401).json({ error: 'unauthorized' });
  next();
}

function authFromReq(req) {
  const hdr = req.header('authorization') ?? '';
  const m = hdr.match(/^Bearer\s+(.+)$/i);
  return m?.[1] ?? null;
}

function requireAuth(req, res, next) {
  const token = authFromReq(req);
  if (!token) return res.status(401).json({ error: 'unauthorized' });
  try {
    req.user = jwt.verify(token, JWT_SECRET);
    next();
  } catch {
    return res.status(401).json({ error: 'unauthorized' });
  }
}

function getUserRoleById(userId) {
  const row = db.prepare('SELECT role FROM users WHERE id = ? LIMIT 1').get(Number(userId));
  return row?.role ? String(row.role) : 'patient';
}

function publicUser(user) {
  return {
    id: Number(user.id),
    username: user.username,
    email: user.email,
    role: user.role ?? getUserRoleById(user.id),
  };
}

function issueToken(user) {
  const role = user.role ?? getUserRoleById(user.id);
  return jwt.sign(
    { sub: String(user.id), username: user.username, email: user.email, role },
    JWT_SECRET,
    { expiresIn: '30d' }
  );
}

function upsertInstall({ deviceId, appVersion, platform }) {
  const device_id = String(deviceId ?? '').trim();
  if (!device_id) return { ok: false, error: 'deviceId_required' };

  const ts = nowMs();
  const row = db
    .prepare('SELECT id, first_seen_at FROM installs WHERE device_id = ?')
    .get(device_id);

  if (!row) {
    const info = db
      .prepare(
        `INSERT INTO installs (device_id, first_seen_at, last_seen_at, app_version, platform)
         VALUES (?, ?, ?, ?, ?)`
      )
      .run(device_id, ts, ts, appVersion ?? null, platform ?? null);
    return { ok: true, installId: info.lastInsertRowid, firstSeenAt: ts, lastSeenAt: ts };
  }

  db.prepare(
    `UPDATE installs SET last_seen_at = ?, app_version = COALESCE(?, app_version), platform = COALESCE(?, platform)
     WHERE id = ?`
  ).run(ts, appVersion ?? null, platform ?? null, row.id);

  return { ok: true, installId: row.id, firstSeenAt: row.first_seen_at, lastSeenAt: ts };
}

function linkInstallToUser({ installId, userId }) {
  db.prepare(
    `INSERT OR IGNORE INTO install_user_links (install_id, user_id, linked_at) VALUES (?, ?, ?)`
  ).run(Number(installId), Number(userId), nowMs());
}

app.get('/health', (req, res) => {
  const adminRow = db
    .prepare('SELECT id, username, email FROM users WHERE lower(username) = lower(?) LIMIT 1')
    .get(ADMIN_USERNAME);
  res.json({
    ok: true,
    version: 6,
    features: {
      supportImages: true,
      supportReplies: true,
      doctorApi: true,
      prescriptionRespond: true,
      assignmentReject: true,
      completedAssignments: true,
      doctorPeerChat: true,
      doctorPeerUnread: true,
    },
    adminLogin: {
      username: ADMIN_USERNAME,
      email: ADMIN_EMAIL,
      exists: Boolean(adminRow?.id),
      passwordSource: 'ADMIN_PASSWORD env (not ADMIN_KEY)',
    },
  });
});

/** После смены ADMIN_PASSWORD на Render: вызовите с заголовком x-admin-key, затем войдите Admin + новый пароль. */
app.post('/api/admin/sync-admin-login', requireAdmin, (req, res) => {
  const id = ensureAdminUser();
  res.json({
    ok: true,
    userId: id,
    username: ADMIN_USERNAME,
    email: ADMIN_EMAIL,
    message: 'Пароль Admin обновлён из ADMIN_PASSWORD. Войдите в приложение этим логином и паролем.',
  });
});

app.post('/api/events/install', (req, res) => {
  const { deviceId, appVersion, platform, username } = req.body ?? {};
  const install = upsertInstall({ deviceId, appVersion, platform });
  if (!install.ok) return res.status(400).json({ error: install.error });

  if (username) {
    const u = db
      .prepare('SELECT id FROM users WHERE username = ? LIMIT 1')
      .get(normalizeUsername(username));
    if (u) linkInstallToUser({ installId: install.installId, userId: u.id });
  }

  return res.json({ ok: true, ...install });
});

app.post('/api/auth/register', (req, res) => {
  const { username, email, password, deviceId, appVersion, platform } = req.body ?? {};
  const uname = normalizeUsername(username);
  const em = normalizeEmail(email);
  const pw = String(password ?? '');

  if (!uname || uname.length < 3 || uname.length > 30) {
    return res.status(400).json({ error: 'invalid_username' });
  }
  if (!em || !em.includes('@')) return res.status(400).json({ error: 'invalid_email' });
  if (!pw || pw.length < 6) return res.status(400).json({ error: 'invalid_password' });

  const existing = db
    .prepare('SELECT id FROM users WHERE username = ? OR email = ? LIMIT 1')
    .get(uname, em);
  if (existing) return res.status(409).json({ error: 'user_exists' });

  const hash = bcrypt.hashSync(pw, 10);
  const createdAt = nowMs();
  const info = db
    .prepare(
      `INSERT INTO users (username, email, password_hash, created_at, last_login_at)
       VALUES (?, ?, ?, ?, ?)`
    )
    .run(uname, em, hash, createdAt, createdAt);

  const user = { id: info.lastInsertRowid, username: uname, email: em, role: 'patient' };
  const token = issueToken(user);

  const install = upsertInstall({ deviceId, appVersion, platform });
  if (install.ok) linkInstallToUser({ installId: install.installId, userId: user.id });

  return res.status(201).json({ ok: true, user: publicUser(user), token });
});

app.post('/api/auth/login', (req, res) => {
  const { usernameOrEmail, password, deviceId, appVersion, platform } = req.body ?? {};
  const id = String(usernameOrEmail ?? '').trim();
  const pw = String(password ?? '');
  if (!id || !pw) return res.status(400).json({ error: 'invalid_credentials' });

  const user = db
    .prepare(
      `SELECT id, username, email, password_hash, role FROM users
       WHERE lower(username) = lower(?) OR lower(email) = lower(?) LIMIT 1`
    )
    .get(id, id);

  if (!user) return res.status(401).json({ error: 'invalid_credentials' });
  if (!bcrypt.compareSync(pw, user.password_hash)) {
    return res.status(401).json({ error: 'invalid_credentials' });
  }

  db.prepare('UPDATE users SET last_login_at = ? WHERE id = ?').run(nowMs(), user.id);
  const token = issueToken(user);

  const install = upsertInstall({ deviceId, appVersion, platform });
  if (install.ok) linkInstallToUser({ installId: install.installId, userId: user.id });

  return res.json({ ok: true, user: publicUser(user), token });
});

/**
 * После смены пароля по ссылке Firebase клиент входит в Firebase с новым паролем,
 * передаёт idToken и тот же пароль — сервер проверяет токен и обновляет password_hash в SQLite.
 */
app.post('/api/auth/sync-password-from-firebase', async (req, res) => {
  const { idToken, password, deviceId, appVersion, platform } = req.body ?? {};
  const pw = String(password ?? '');
  if (!idToken || pw.length < 6) {
    return res.status(400).json({ ok: false, error: 'invalid_body' });
  }
  if (!initFirebaseAdmin()) {
    return res.status(503).json({ ok: false, error: 'firebase_admin_not_configured' });
  }
  let decoded;
  try {
    decoded = await verifyFirebaseIdToken(idToken);
  } catch {
    return res.status(401).json({ ok: false, error: 'invalid_token' });
  }
  const email = normalizeEmail(decoded.email);
  if (!email) return res.status(400).json({ ok: false, error: 'no_email_in_token' });

  const user = db
    .prepare('SELECT id, username, email, role FROM users WHERE lower(email) = lower(?) LIMIT 1')
    .get(email);
  if (!user) return res.status(404).json({ ok: false, error: 'user_not_found' });

  const hash = bcrypt.hashSync(pw, 10);
  db.prepare('UPDATE users SET password_hash = ? WHERE id = ?').run(hash, user.id);
  db.prepare('UPDATE users SET last_login_at = ? WHERE id = ?').run(nowMs(), user.id);

  const token = issueToken(user);
  const install = upsertInstall({ deviceId, appVersion, platform });
  if (install.ok) linkInstallToUser({ installId: install.installId, userId: user.id });

  return res.json({
    ok: true,
    user: publicUser(user),
    token
  });
});

app.post('/api/auth/forgot-password', async (req, res) => {
  const uname = normalizeUsername(req.body?.username ?? req.body?.login);
  const em = normalizeEmail(req.body?.email);
  if (!uname || uname.length < 3) return res.status(400).json({ ok: false, error: 'invalid_username' });
  if (!em || !em.includes('@')) return res.status(400).json({ ok: false, error: 'invalid_email' });

  const allowNoMail = process.env.ALLOW_RESET_WITHOUT_MAIL === '1';
  if (!isMailDeliveryConfigured() && !allowNoMail) {
    return res.status(503).json({ ok: false, error: 'mail_not_configured' });
  }

  const user = db
    .prepare('SELECT id FROM users WHERE username = ? AND lower(email) = lower(?) LIMIT 1')
    .get(uname, em);

  if (!user) {
    return res.status(400).json({ ok: false, error: 'login_email_mismatch' });
  }

  const code = String(crypto.randomInt(100000, 1000000));
  const ts = nowMs();
  const exp = ts + 15 * 60 * 1000;
  db.prepare('DELETE FROM password_reset_codes WHERE user_id = ?').run(user.id);
  db.prepare(
    'INSERT INTO password_reset_codes (user_id, code, expires_at, created_at) VALUES (?, ?, ?, ?)'
  ).run(user.id, code, exp, ts);

  // eslint-disable-next-line no-console
  console.log(`[password-reset] логин "${uname}" / ${em} → код ${code} (15 мин)`);

  if (isMailDeliveryConfigured()) {
    try {
      await sendPasswordResetEmail({ to: em, username: uname, code });
      // eslint-disable-next-line no-console
      console.log(`[password-reset] письмо отправлено на ${em}`);
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error('[password-reset] ошибка отправки почты:', err?.message ?? err);
      return res.status(500).json({ ok: false, error: 'mail_failed' });
    }
  } else {
    // eslint-disable-next-line no-console
    console.log('[password-reset] ALLOW_RESET_WITHOUT_MAIL=1 — письмо не отправлялось, код только в консоли');
  }

  const out = { ok: true, mailDelivered: isMailDeliveryConfigured() };
  if (process.env.SHOW_RESET_CODE === '1') out.devCode = code;
  return res.json(out);
});

app.post('/api/auth/verify-reset-code', (req, res) => {
  const uname = normalizeUsername(req.body?.username ?? req.body?.login);
  const em = normalizeEmail(req.body?.email);
  const rawCode = String(req.body?.code ?? '').trim();

  if (!uname || uname.length < 3) return res.status(400).json({ ok: false, error: 'invalid_username' });
  if (!em || !em.includes('@')) return res.status(400).json({ ok: false, error: 'invalid_email' });
  if (!/^\d{6}$/.test(rawCode)) return res.status(400).json({ ok: false, error: 'invalid_code' });

  const user = db
    .prepare('SELECT id FROM users WHERE username = ? AND lower(email) = lower(?) LIMIT 1')
    .get(uname, em);
  if (!user) return res.status(400).json({ ok: false, error: 'bad_code_or_expired' });

  const row = db
    .prepare(
      `SELECT id FROM password_reset_codes
       WHERE user_id = ? AND code = ? AND expires_at >= ? LIMIT 1`
    )
    .get(user.id, rawCode, nowMs());
  if (!row) return res.status(400).json({ ok: false, error: 'bad_code_or_expired' });

  return res.json({ ok: true });
});

app.post('/api/auth/reset-password', (req, res) => {
  const uname = normalizeUsername(req.body?.username ?? req.body?.login);
  const em = normalizeEmail(req.body?.email);
  const code = String(req.body?.code ?? '').trim();
  const newPassword = String(req.body?.newPassword ?? '');

  if (!uname || uname.length < 3) return res.status(400).json({ ok: false, error: 'invalid_username' });
  if (!em || !em.includes('@')) return res.status(400).json({ ok: false, error: 'invalid_email' });
  if (!/^\d{6}$/.test(code)) return res.status(400).json({ ok: false, error: 'invalid_code' });
  if (!newPassword || newPassword.length < 6) {
    return res.status(400).json({ ok: false, error: 'invalid_password' });
  }

  const user = db
    .prepare('SELECT id FROM users WHERE username = ? AND lower(email) = lower(?) LIMIT 1')
    .get(uname, em);
  if (!user) return res.status(400).json({ ok: false, error: 'bad_code_or_expired' });

  const row = db
    .prepare(
      `SELECT id FROM password_reset_codes
       WHERE user_id = ? AND code = ? AND expires_at >= ? LIMIT 1`
    )
    .get(user.id, code, nowMs());
  if (!row) return res.status(400).json({ ok: false, error: 'bad_code_or_expired' });

  const hash = bcrypt.hashSync(newPassword, 10);
  db.prepare('UPDATE users SET password_hash = ? WHERE id = ?').run(hash, user.id);
  db.prepare('DELETE FROM password_reset_codes WHERE user_id = ?').run(user.id);
  return res.json({ ok: true });
});

app.delete('/api/users/me', requireAuth, (req, res) => {
  const userId = Number(req.user?.sub);
  if (!Number.isFinite(userId)) return res.status(401).json({ error: 'unauthorized' });
  const existed = db.prepare('SELECT id FROM users WHERE id = ?').get(userId);
  if (!existed) return res.status(404).json({ error: 'not_found' });
  db.prepare('DELETE FROM users WHERE id = ?').run(userId);
  return res.json({ ok: true });
});

app.get('/api/admin/users', requireAdmin, (req, res) => {
  const users = db
    .prepare(
      `SELECT id, username, email, created_at, last_login_at
       FROM users
       ORDER BY created_at DESC`
    )
    .all();
  res.json({ ok: true, users });
});

/** Сколько строк в основных таблицах (проверка без скачивания файла). */
app.get('/api/admin/database/stats', requireAdmin, (req, res) => {
  const count = (sql) => Number(db.prepare(sql).get()?.c ?? 0);
  res.json({
    ok: true,
    dbFile: DB_FILE,
    users: count('SELECT COUNT(*) AS c FROM users'),
    installs: count('SELECT COUNT(*) AS c FROM installs'),
    support_conversations: count('SELECT COUNT(*) AS c FROM support_conversations'),
    support_messages: count('SELECT COUNT(*) AS c FROM support_messages'),
  });
});

/** Скачать SQLite с сервера (полная копия через await db.backup). */
app.get('/api/admin/database/export', requireAdmin, async (req, res) => {
  const dbPath = path.resolve(DB_FILE);
  if (!fs.existsSync(dbPath)) {
    return res.status(404).json({ ok: false, error: 'database_not_found', path: DB_FILE });
  }
  const tmpPath = path.join(os.tmpdir(), `medtest1-export-${Date.now()}.sqlite`);
  try {
    await db.backup(tmpPath);
    const name = path.basename(dbPath) || 'data.sqlite';
    res.setHeader('Content-Type', 'application/octet-stream');
    res.setHeader('Content-Disposition', `attachment; filename="${name}"`);
    await new Promise((resolve, reject) => {
      const stream = fs.createReadStream(tmpPath);
      stream.on('error', reject);
      res.on('finish', resolve);
      res.on('close', resolve);
      stream.pipe(res);
    });
  } catch (e) {
    if (!res.headersSent) {
      res.status(500).json({ ok: false, error: 'export_failed', message: String(e?.message ?? e) });
    }
  } finally {
    fs.unlink(tmpPath, () => {});
  }
});

app.get('/api/admin/installs', requireAdmin, (req, res) => {
  const installs = db
    .prepare(
      `SELECT i.id, i.device_id, i.first_seen_at, i.last_seen_at, i.app_version, i.platform,
              COUNT(l.user_id) AS linked_users
       FROM installs i
       LEFT JOIN install_user_links l ON l.install_id = i.id
       GROUP BY i.id
       ORDER BY i.first_seen_at DESC`
    )
    .all();
  res.json({ ok: true, installs });
});

function ensureConversationForUser(userId) {
  const ts = nowMs();
  const existing = db
    .prepare('SELECT id FROM support_conversations WHERE user_id = ? LIMIT 1')
    .get(Number(userId));
  if (existing?.id) return Number(existing.id);

  const info = db
    .prepare(
      `INSERT INTO support_conversations (user_id, created_at, updated_at)
       VALUES (?, ?, ?)`
    )
    .run(Number(userId), ts, ts);
  return Number(info.lastInsertRowid);
}

const SUPPORT_IMAGE_MIME = new Set(['image/jpeg', 'image/png', 'image/webp']);
const MAX_SUPPORT_IMAGE_BYTES = 1024 * 1024;

function saveSupportImage(imageBase64, imageMimeType) {
  const mime = String(imageMimeType ?? '').trim().toLowerCase();
  if (!SUPPORT_IMAGE_MIME.has(mime)) return null;
  const raw = String(imageBase64 ?? '').trim();
  if (!raw) return null;
  let buf;
  try {
    buf = Buffer.from(raw, 'base64');
  } catch {
    return null;
  }
  if (!buf.length || buf.length > MAX_SUPPORT_IMAGE_BYTES) return null;
  const ext = mime === 'image/png' ? 'png' : mime === 'image/webp' ? 'webp' : 'jpg';
  const name = `${crypto.randomUUID()}.${ext}`;
  const abs = path.join(UPLOADS_DIR, 'support', name);
  try {
    fs.writeFileSync(abs, buf);
  } catch {
    return null;
  }
  return `/uploads/support/${name}`;
}

function resolveMessageImageUrl({ imageUrl, imageBase64, imageMimeType }) {
  const direct = String(imageUrl ?? '').trim();
  if (direct.startsWith('/uploads/support/')) return direct;
  if (imageBase64) return saveSupportImage(imageBase64, imageMimeType);
  return null;
}

function mapSupportMessageRow(m, messageById, userLabel) {
  const replyIdRaw = m.reply_to_message_id != null ? Number(m.reply_to_message_id) : null;
  const replyId = Number.isFinite(replyIdRaw) && replyIdRaw > 0 ? replyIdRaw : null;
  const replied = replyId && messageById ? messageById.get(replyId) : null;
  return {
    id: Number(m.id),
    conversationId: Number(m.conversation_id),
    sender: m.sender,
    text: m.text,
    createdAt: Number(m.created_at),
    replyToMessageId: replyId || null,
    imageUrl: m.image_url ? String(m.image_url) : null,
    replyPreviewText: replied?.text
      ? String(replied.text).slice(0, 160)
      : replied?.image_url
        ? 'Фото'
        : null,
    replyPreviewSender: replied?.sender ?? null,
    replyPreviewAuthor: replied
      ? replied.sender === 'user'
        ? userLabel
        : 'Умник'
      : null,
    replyPreviewImageUrl: replied?.image_url ? String(replied.image_url) : null,
  };
}

function fetchSupportMessages(conversationId) {
  const convRow = db
    .prepare(
      `SELECT u.username AS username
       FROM support_conversations c
       JOIN users u ON u.id = c.user_id
       WHERE c.id = ?
       LIMIT 1`
    )
    .get(Number(conversationId));
  const userLabel = convRow?.username ? String(convRow.username) : 'Пользователь';

  const rows = db
    .prepare(
      `SELECT id, conversation_id, sender, text, created_at, reply_to_message_id, image_url
       FROM support_messages
       WHERE conversation_id = ?
       ORDER BY created_at ASC`
    )
    .all(Number(conversationId));
  const byId = new Map(rows.map((r) => [Number(r.id), r]));
  return rows.map((m) => mapSupportMessageRow(m, byId, userLabel));
}

function addSupportMessage({ conversationId, sender, text, replyToMessageId, imageUrl }) {
  const clean = String(text ?? '').trim();
  const hasImage = Boolean(imageUrl);
  if (!clean && !hasImage) return { ok: false, error: 'empty_message' };
  if (clean.length > 2000) return { ok: false, error: 'text_too_long' };

  let replyId = null;
  if (replyToMessageId != null && replyToMessageId !== '') {
    replyId = Number(replyToMessageId);
    if (!Number.isFinite(replyId) || replyId <= 0) return { ok: false, error: 'bad_reply_id' };
    const target = db
      .prepare(
        `SELECT id FROM support_messages
         WHERE id = ? AND conversation_id = ? LIMIT 1`
      )
      .get(replyId, Number(conversationId));
    if (!target?.id) return { ok: false, error: 'reply_not_found' };
  }

  const ts = nowMs();
  const info = db
    .prepare(
      `INSERT INTO support_messages
        (conversation_id, sender, text, created_at, reply_to_message_id, image_url)
       VALUES (?, ?, ?, ?, ?, ?)`
    )
    .run(Number(conversationId), sender, clean, ts, replyId, imageUrl ?? null);
  db.prepare('UPDATE support_conversations SET updated_at = ? WHERE id = ?').run(ts, Number(conversationId));
  return { ok: true, id: Number(info.lastInsertRowid), createdAt: ts, text: clean, imageUrl: imageUrl ?? null };
}

function notifyAdminAboutEscalation(conversationId, previewText) {
  const conv = db
    .prepare(
      `SELECT u.username AS username, u.fcm_token AS fcm_token
       FROM support_conversations c
       JOIN users u ON u.id = c.user_id
       WHERE c.id = ?
       LIMIT 1`
    )
    .get(Number(conversationId));
  const username = conv?.username ? String(conv.username) : 'Пользователь';
  const preview = previewText?.trim()
    ? previewText.trim().slice(0, 120)
    : 'Пользователь просит специалиста';

  const adminRow = db
    .prepare('SELECT fcm_token FROM users WHERE lower(username) = lower(?) LIMIT 1')
    .get(ADMIN_USERNAME);
  const adminToken = adminRow?.fcm_token ?? process.env.ADMIN_FCM_TOKEN ?? '';
  if (adminToken) {
    void sendAdminEscalationPush({
      token: adminToken,
      username,
      body: preview,
      conversationId,
    });
  }
}

function notifyUserAboutAdminReply(conversationId, previewText, messageId) {
  const row = db
    .prepare(
      `SELECT u.fcm_token AS fcm_token
       FROM support_conversations c
       JOIN users u ON u.id = c.user_id
       WHERE c.id = ?
       LIMIT 1`
    )
    .get(Number(conversationId));
  const token = row?.fcm_token;
  if (!token) return;
  const body = previewText?.trim()
    ? previewText.trim().slice(0, 120)
    : 'Вам ответили на сообщение';
  void sendSupportReplyPush({ token, body, messageId });
}

app.get('/api/support/messages', requireAuth, (req, res) => {
  const userId = Number(req.user?.sub);
  if (!Number.isFinite(userId)) return res.status(401).json({ ok: false, error: 'unauthorized' });

  const conv = db
    .prepare('SELECT id FROM support_conversations WHERE user_id = ? LIMIT 1')
    .get(userId);
  if (!conv?.id) return res.json({ ok: true, conversationId: null, messages: [] });

  const messages = fetchSupportMessages(Number(conv.id));

  res.json({ ok: true, conversationId: Number(conv.id), messages });
});

app.post('/api/support/upload-image', requireAuth, (req, res) => {
  const { imageBase64, imageMimeType } = req.body ?? {};
  const imageUrl = resolveMessageImageUrl({ imageBase64, imageMimeType });
  if (!imageUrl) return res.status(400).json({ ok: false, error: 'invalid_image' });
  res.json({ ok: true, imageUrl });
});

app.post('/api/support/messages', requireAuth, (req, res) => {
  const userId = Number(req.user?.sub);
  if (!Number.isFinite(userId)) return res.status(401).json({ ok: false, error: 'unauthorized' });
  const { text, replyToMessageId, imageUrl: imageUrlDirect, imageBase64, imageMimeType } = req.body ?? {};

  const imageUrl = resolveMessageImageUrl({
    imageUrl: imageUrlDirect,
    imageBase64,
    imageMimeType,
  });
  if ((imageBase64 || imageUrlDirect) && !imageUrl) {
    return res.status(400).json({ ok: false, error: 'invalid_image' });
  }

  const convId = ensureConversationForUser(userId);
  const r = addSupportMessage({
    conversationId: convId,
    sender: 'user',
    text,
    replyToMessageId,
    imageUrl,
  });
  if (!r.ok) return res.status(400).json({ ok: false, error: r.error });

  if (isEscalationUserMessage(r.text)) {
    const ts = nowMs();
    db.prepare('UPDATE support_conversations SET admin_requested_at = ? WHERE id = ?').run(ts, convId);
    notifyAdminAboutEscalation(convId, r.text);
  }

  const full = fetchSupportMessages(convId).find((m) => m.id === r.id) ?? {
    id: r.id,
    conversationId: convId,
    sender: 'user',
    text: r.text,
    createdAt: r.createdAt,
    replyToMessageId: null,
    imageUrl: r.imageUrl,
    replyPreviewText: null,
    replyPreviewSender: null,
    replyPreviewAuthor: null,
    replyPreviewImageUrl: null,
  };

  res.status(201).json({ ok: true, conversationId: convId, message: full });
});

app.post('/api/support/read', requireAuth, (req, res) => {
  const userId = Number(req.user?.sub);
  if (!Number.isFinite(userId)) return res.status(401).json({ ok: false, error: 'unauthorized' });
  const conv = db.prepare('SELECT id FROM support_conversations WHERE user_id = ? LIMIT 1').get(userId);
  if (!conv?.id) return res.json({ ok: true });
  const ts = nowMs();
  db.prepare(
    `UPDATE support_conversations
     SET user_last_read_at = MAX(COALESCE(user_last_read_at, 0), ?)
     WHERE id = ?`
  ).run(ts, Number(conv.id));
  res.json({ ok: true });
});

app.post('/api/users/fcm-token', requireAuth, (req, res) => {
  const userId = Number(req.user?.sub);
  if (!Number.isFinite(userId)) return res.status(401).json({ ok: false, error: 'unauthorized' });
  const token = String(req.body?.token ?? '').trim();
  if (!token) return res.status(400).json({ ok: false, error: 'token_required' });
  db.prepare('UPDATE users SET fcm_token = ? WHERE id = ?').run(token, userId);
  res.json({ ok: true });
});

app.get('/api/admin/support/conversations', requireAdmin, (req, res) => {
  const rows = db
    .prepare(
      `SELECT c.id AS conversation_id, c.user_id, c.created_at, c.updated_at,
              c.admin_requested_at,
              u.username, u.email,
              (
                SELECT COUNT(1)
                FROM support_messages m
                WHERE m.conversation_id = c.id
                  AND m.sender = 'user'
                  AND m.created_at > COALESCE(c.admin_last_read_at, 0)
              ) AS unread_count,
              CASE
                WHEN COALESCE(c.admin_requested_at, 0) > COALESCE(c.admin_last_read_at, 0) THEN 1
                ELSE 0
              END AS needs_admin_attention
       FROM support_conversations c
       JOIN users u ON u.id = c.user_id
       ORDER BY needs_admin_attention DESC, unread_count DESC, c.updated_at DESC`
    )
    .all()
    .map((r) => ({
      id: Number(r.conversation_id),
      userId: Number(r.user_id),
      username: r.username,
      email: r.email,
      createdAt: Number(r.created_at),
      updatedAt: Number(r.updated_at),
      unreadCount: Number(r.unread_count ?? 0),
      needsAdminAttention: Number(r.needs_admin_attention ?? 0) === 1,
      adminRequestedAt: Number(r.admin_requested_at ?? 0),
    }));
  res.json({ ok: true, conversations: rows });
});

app.get('/api/admin/support/conversations/:id/messages', requireAdmin, (req, res) => {
  const convId = Number(req.params.id);
  if (!Number.isFinite(convId)) return res.status(400).json({ ok: false, error: 'bad_conversation_id' });

  const conv = db.prepare('SELECT id FROM support_conversations WHERE id = ?').get(convId);
  if (!conv) return res.status(404).json({ ok: false, error: 'not_found' });

  const messages = fetchSupportMessages(convId);

  const ts = nowMs();
  const lastUserMessage = messages
    .filter((m) => m.sender === 'user')
    .at(-1);
  const readAt = Math.max(ts, Number(lastUserMessage?.createdAt ?? 0));
  db.prepare(
    `UPDATE support_conversations
     SET admin_last_read_at = MAX(COALESCE(admin_last_read_at, 0), ?),
         admin_requested_at = 0
     WHERE id = ?`
  ).run(readAt, convId);

  res.json({ ok: true, conversationId: convId, messages });
});

app.post('/api/admin/support/conversations/:id/upload-image', requireAdmin, (req, res) => {
  const convId = Number(req.params.id);
  if (!Number.isFinite(convId)) return res.status(400).json({ ok: false, error: 'bad_conversation_id' });
  const { imageBase64, imageMimeType } = req.body ?? {};
  const imageUrl = resolveMessageImageUrl({ imageBase64, imageMimeType });
  if (!imageUrl) return res.status(400).json({ ok: false, error: 'invalid_image' });
  res.json({ ok: true, imageUrl });
});

app.post('/api/admin/support/conversations/:id/messages', requireAdmin, (req, res) => {
  const convId = Number(req.params.id);
  if (!Number.isFinite(convId)) return res.status(400).json({ ok: false, error: 'bad_conversation_id' });
  const { text, replyToMessageId, imageUrl: imageUrlDirect, imageBase64, imageMimeType } = req.body ?? {};

  const conv = db.prepare('SELECT id FROM support_conversations WHERE id = ?').get(convId);
  if (!conv) return res.status(404).json({ ok: false, error: 'not_found' });

  const imageUrl = resolveMessageImageUrl({
    imageUrl: imageUrlDirect,
    imageBase64,
    imageMimeType,
  });
  if ((imageBase64 || imageUrlDirect) && !imageUrl) {
    return res.status(400).json({ ok: false, error: 'invalid_image' });
  }

  const r = addSupportMessage({
    conversationId: convId,
    sender: 'admin',
    text,
    replyToMessageId,
    imageUrl,
  });
  if (!r.ok) return res.status(400).json({ ok: false, error: r.error });
  notifyUserAboutAdminReply(convId, r.text || (r.imageUrl ? 'Фото' : ''), r.id);

  const full = fetchSupportMessages(convId).find((m) => m.id === r.id) ?? {
    id: r.id,
    conversationId: convId,
    sender: 'admin',
    text: r.text,
    createdAt: r.createdAt,
    replyToMessageId: null,
    imageUrl: r.imageUrl,
    replyPreviewText: null,
    replyPreviewSender: null,
    replyPreviewAuthor: null,
    replyPreviewImageUrl: null,
  };

  res.status(201).json({ ok: true, conversationId: convId, message: full });
});

registerDoctorRoutes(app, {
  db,
  requireAuth,
  authFromReq,
  jwt,
  JWT_SECRET,
  ADMIN_KEY,
  nowMs,
  normalizeUsername,
  normalizeEmail,
});

app.listen(PORT, () => {
  // eslint-disable-next-line no-console
  console.log(`[medtest1-backend] listening on http://0.0.0.0:${PORT}`);
  // eslint-disable-next-line no-console
  console.log(`[medtest1-backend] db: ${DB_FILE}`);
});

