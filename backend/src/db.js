import Database from 'better-sqlite3';

export function openDb({ filename }) {
  const db = new Database(filename);
  db.exec('PRAGMA journal_mode = WAL;');
  db.exec('PRAGMA foreign_keys = ON;');
  migrate(db);
  return db;
}

function migrate(db) {
  db.exec(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      username TEXT NOT NULL UNIQUE,
      email TEXT NOT NULL UNIQUE,
      password_hash TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      last_login_at INTEGER
    );

    CREATE TABLE IF NOT EXISTS installs (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      device_id TEXT NOT NULL UNIQUE,
      first_seen_at INTEGER NOT NULL,
      last_seen_at INTEGER NOT NULL,
      app_version TEXT,
      platform TEXT
    );

    CREATE TABLE IF NOT EXISTS install_user_links (
      install_id INTEGER NOT NULL,
      user_id INTEGER NOT NULL,
      linked_at INTEGER NOT NULL,
      UNIQUE(install_id, user_id),
      FOREIGN KEY(install_id) REFERENCES installs(id) ON DELETE CASCADE,
      FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS support_conversations (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      user_id INTEGER NOT NULL UNIQUE,
      created_at INTEGER NOT NULL,
      updated_at INTEGER NOT NULL,
      FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS support_messages (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      conversation_id INTEGER NOT NULL,
      sender TEXT NOT NULL CHECK(sender IN ('user', 'admin')),
      text TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(conversation_id) REFERENCES support_conversations(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_support_messages_conversation_created
      ON support_messages(conversation_id, created_at);

    CREATE TABLE IF NOT EXISTS password_reset_codes (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      user_id INTEGER NOT NULL,
      code TEXT NOT NULL,
      expires_at INTEGER NOT NULL,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_password_reset_user
      ON password_reset_codes(user_id);
  `);

  const supportColumns = db.prepare(`PRAGMA table_info(support_conversations)`).all();
  const hasAdminLastReadAt = supportColumns.some((c) => c.name === 'admin_last_read_at');
  if (!hasAdminLastReadAt) {
    db.exec(`ALTER TABLE support_conversations ADD COLUMN admin_last_read_at INTEGER NOT NULL DEFAULT 0;`);
  }
  const hasUserLastReadAt = supportColumns.some((c) => c.name === 'user_last_read_at');
  if (!hasUserLastReadAt) {
    db.exec(`ALTER TABLE support_conversations ADD COLUMN user_last_read_at INTEGER NOT NULL DEFAULT 0;`);
  }
  const hasAdminRequestedAt = supportColumns.some((c) => c.name === 'admin_requested_at');
  if (!hasAdminRequestedAt) {
    db.exec(`ALTER TABLE support_conversations ADD COLUMN admin_requested_at INTEGER NOT NULL DEFAULT 0;`);
  }

  const messageColumns = db.prepare(`PRAGMA table_info(support_messages)`).all();
  if (!messageColumns.some((c) => c.name === 'reply_to_message_id')) {
    db.exec(`ALTER TABLE support_messages ADD COLUMN reply_to_message_id INTEGER;`);
  }
  if (!messageColumns.some((c) => c.name === 'image_url')) {
    db.exec(`ALTER TABLE support_messages ADD COLUMN image_url TEXT;`);
  }

  const userColumns = db.prepare(`PRAGMA table_info(users)`).all();
  if (!userColumns.some((c) => c.name === 'fcm_token')) {
    db.exec(`ALTER TABLE users ADD COLUMN fcm_token TEXT;`);
  }
  if (!userColumns.some((c) => c.name === 'role')) {
    db.exec(`ALTER TABLE users ADD COLUMN role TEXT NOT NULL DEFAULT 'patient';`);
  }

  db.exec(`
    CREATE TABLE IF NOT EXISTS doctor_profiles (
      user_id INTEGER PRIMARY KEY,
      specialty TEXT NOT NULL DEFAULT '',
      full_name TEXT NOT NULL DEFAULT '',
      experience_years INTEGER NOT NULL DEFAULT 0,
      education TEXT NOT NULL DEFAULT '',
      bio TEXT NOT NULL DEFAULT '',
      photo_base64 TEXT,
      updated_at INTEGER NOT NULL DEFAULT 0,
      FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS doctor_shifts (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      doctor_user_id INTEGER NOT NULL,
      started_at INTEGER NOT NULL,
      ended_at INTEGER,
      is_active INTEGER NOT NULL DEFAULT 1,
      FOREIGN KEY(doctor_user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_doctor_shifts_active
      ON doctor_shifts(doctor_user_id, is_active);

    CREATE TABLE IF NOT EXISTS doctor_assignments (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      doctor_user_id INTEGER NOT NULL,
      patient_user_id INTEGER NOT NULL,
      status TEXT NOT NULL DEFAULT 'active',
      assigned_at INTEGER NOT NULL,
      patient_profile_json TEXT,
      treatment_sync_json TEXT,
      FOREIGN KEY(doctor_user_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY(patient_user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_doctor_assignments_doctor
      ON doctor_assignments(doctor_user_id, status);

    CREATE TABLE IF NOT EXISTS doctor_messages (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      assignment_id INTEGER NOT NULL,
      sender TEXT NOT NULL CHECK(sender IN ('doctor', 'patient')),
      text TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(assignment_id) REFERENCES doctor_assignments(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_doctor_messages_assignment
      ON doctor_messages(assignment_id, created_at);

    CREATE TABLE IF NOT EXISTS doctor_prescriptions (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      assignment_id INTEGER NOT NULL,
      prescription_text TEXT NOT NULL,
      treatment_plan_text TEXT NOT NULL DEFAULT '',
      created_at INTEGER NOT NULL,
      FOREIGN KEY(assignment_id) REFERENCES doctor_assignments(id) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS treatment_reports (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      assignment_id INTEGER NOT NULL,
      patient_user_id INTEGER NOT NULL,
      report_data_json TEXT NOT NULL,
      pdf_base64 TEXT,
      status TEXT NOT NULL DEFAULT 'pending',
      doctor_conclusion TEXT,
      doctor_signed_at INTEGER,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(assignment_id) REFERENCES doctor_assignments(id) ON DELETE CASCADE
    );
  `);

  const assignmentColumns = db.prepare(`PRAGMA table_info(doctor_assignments)`).all();
  if (!assignmentColumns.some((c) => c.name === 'rejection_reason')) {
    db.exec(`ALTER TABLE doctor_assignments ADD COLUMN rejection_reason TEXT;`);
  }
  if (!assignmentColumns.some((c) => c.name === 'rejected_at')) {
    db.exec(`ALTER TABLE doctor_assignments ADD COLUMN rejected_at INTEGER;`);
  }

  const prescriptionColumns = db.prepare(`PRAGMA table_info(doctor_prescriptions)`).all();
  if (!prescriptionColumns.some((c) => c.name === 'patient_status')) {
    db.exec(`ALTER TABLE doctor_prescriptions ADD COLUMN patient_status TEXT NOT NULL DEFAULT 'pending';`);
  }
  if (!prescriptionColumns.some((c) => c.name === 'decline_reason')) {
    db.exec(`ALTER TABLE doctor_prescriptions ADD COLUMN decline_reason TEXT;`);
  }
  if (!prescriptionColumns.some((c) => c.name === 'responded_at')) {
    db.exec(`ALTER TABLE doctor_prescriptions ADD COLUMN responded_at INTEGER;`);
  }

  db.exec(`
    CREATE TABLE IF NOT EXISTS care_events (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      assignment_id INTEGER NOT NULL,
      event_type TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      metadata_json TEXT,
      FOREIGN KEY(assignment_id) REFERENCES doctor_assignments(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_care_events_assignment
      ON care_events(assignment_id, event_type, created_at);

    CREATE TABLE IF NOT EXISTS doctor_peer_messages (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      sender_user_id INTEGER NOT NULL,
      recipient_user_id INTEGER NOT NULL,
      text TEXT NOT NULL,
      created_at INTEGER NOT NULL,
      FOREIGN KEY(sender_user_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY(recipient_user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE INDEX IF NOT EXISTS idx_doctor_peer_messages_pair
      ON doctor_peer_messages(sender_user_id, recipient_user_id, created_at);

    CREATE TABLE IF NOT EXISTS doctor_peer_read_state (
      reader_user_id INTEGER NOT NULL,
      peer_user_id INTEGER NOT NULL,
      last_read_message_id INTEGER NOT NULL DEFAULT 0,
      updated_at INTEGER NOT NULL,
      PRIMARY KEY (reader_user_id, peer_user_id),
      FOREIGN KEY(reader_user_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY(peer_user_id) REFERENCES users(id) ON DELETE CASCADE
    );
  `);
}
