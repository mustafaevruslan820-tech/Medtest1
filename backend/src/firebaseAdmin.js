import admin from 'firebase-admin';
import fs from 'node:fs';


export function initFirebaseAdmin() {
  if (admin.apps.length) return true;
  try {
    const path = process.env.FIREBASE_SERVICE_ACCOUNT_PATH;
    if (path) {
      const raw = fs.readFileSync(path, 'utf8');
      admin.initializeApp({ credential: admin.credential.cert(JSON.parse(raw)) });
      return true;
    }
    const rawJson = process.env.FIREBASE_SERVICE_ACCOUNT_JSON;
    if (rawJson) {
      admin.initializeApp({ credential: admin.credential.cert(JSON.parse(rawJson)) });
      return true;
    }
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn('[firebase-admin] init failed:', e?.message ?? e);
  }
  return false;
}

export async function verifyFirebaseIdToken(idToken) {
  return admin.auth().verifyIdToken(idToken);
}

export async function sendAdminEscalationPush({ token, username, body, conversationId }) {
  if (!admin.apps.length || !token) return false;
  try {
    const preview = body || 'Пользователь ждёт ответа';
    await admin.messaging().send({
      token,
      notification: {
        title: 'Нужен Умник',
        body: `${username}: ${preview}`.slice(0, 180),
      },
      data: {
        type: 'support_escalation',
        body: preview,
        username: username || 'Пользователь',
        ...(conversationId != null ? { conversationId: String(conversationId) } : {}),
      },
      android: {
        priority: 'high',
        notification: { channelId: 'support_admin_escalation' },
      },
    });
    return true;
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn('[firebase-admin] admin escalation push failed:', e?.message ?? e);
    return false;
  }
}

export async function sendDoctorAssignmentPush({ token, patientName, assignmentId }) {
  if (!admin.apps.length || !token) return false;
  try {
    await admin.messaging().send({
      token,
      notification: {
        title: 'Новый пациент',
        body: `${patientName} выбрал вас для лечения`,
      },
      data: {
        type: 'doctor_assignment',
        assignmentId: String(assignmentId),
        patientName: patientName || 'Пациент',
      },
      android: { priority: 'high', notification: { channelId: 'doctor_events' } },
    });
    return true;
  } catch (e) {
    console.warn('[firebase-admin] doctor assignment push failed:', e?.message ?? e);
    return false;
  }
}

export async function sendDoctorMessagePush({ token, senderName, body, assignmentId, messageId }) {
  if (!admin.apps.length || !token) return false;
  try {
    const preview = (body || 'Новое сообщение').slice(0, 120);
    await admin.messaging().send({
      token,
      notification: {
        title: senderName || 'Сообщение',
        body: preview,
      },
      data: {
        type: 'doctor_message',
        assignmentId: String(assignmentId),
        messageId: messageId != null ? String(messageId) : '',
        body: preview,
      },
      android: { priority: 'high', notification: { channelId: 'doctor_events' } },
    });
    return true;
  } catch (e) {
    console.warn('[firebase-admin] doctor message push failed:', e?.message ?? e);
    return false;
  }
}

export async function sendPatientPrescriptionPush({ token, assignmentId }) {
  if (!admin.apps.length || !token) return false;
  try {
    await admin.messaging().send({
      token,
      notification: {
        title: 'Рецепт от врача',
        body: 'Примите план лечения в разделе «Лечение»',
      },
      data: {
        type: 'doctor_prescription',
        assignmentId: String(assignmentId),
      },
      android: { priority: 'high', notification: { channelId: 'doctor_events' } },
    });
    return true;
  } catch (e) {
    console.warn('[firebase-admin] prescription push failed:', e?.message ?? e);
    return false;
  }
}

export async function sendDoctorReportPush({ token, patientName, assignmentId, reportId }) {
  if (!admin.apps.length || !token) return false;
  try {
    await admin.messaging().send({
      token,
      notification: {
        title: 'Отчёт по лечению',
        body: `${patientName} отправил отчёт на заключение`,
      },
      data: {
        type: 'doctor_report',
        assignmentId: String(assignmentId),
        reportId: String(reportId),
      },
      android: { priority: 'high', notification: { channelId: 'doctor_events' } },
    });
    return true;
  } catch (e) {
    console.warn('[firebase-admin] doctor report push failed:', e?.message ?? e);
    return false;
  }
}

export async function sendPatientReportConclusionPush({ token, status, assignmentId }) {
  if (!admin.apps.length || !token) return false;
  try {
    const completed = status === 'completed';
    await admin.messaging().send({
      token,
      notification: {
        title: 'Лечение завершено',
        body: 'Врач подписал завершение лечения',
      },
      data: {
        type: 'report_conclusion',
        status: status || 'completed',
        assignmentId: String(assignmentId),
      },
      android: { priority: 'high', notification: { channelId: 'doctor_events' } },
    });
    return true;
  } catch (e) {
    console.warn('[firebase-admin] report conclusion push failed:', e?.message ?? e);
    return false;
  }
}

export async function sendSupportReplyPush({ token, body, messageId }) {
  if (!admin.apps.length || !token) return false;
  try {
    const text = body || 'Вам ответили на сообщение';
    await admin.messaging().send({
      token,
      notification: {
        title: 'Умник ответил',
        body: text,
      },
      data: {
        type: 'support_reply',
        body: text,
        ...(messageId != null ? { messageId: String(messageId) } : {}),
      },
      android: {
        priority: 'high',
        notification: { channelId: 'support_replies' },
      },
    });
    return true;
  } catch (e) {
    // eslint-disable-next-line no-console
    console.warn('[firebase-admin] support push failed:', e?.message ?? e);
    return false;
  }
}
