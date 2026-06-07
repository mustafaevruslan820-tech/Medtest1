package com.example.medtest1.notifications

import com.example.medtest1.doctor.DoctorSession
import com.example.medtest1.support.SupportAgentInfo
import com.example.medtest1.support.SupportChatSession
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MedtestFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"].orEmpty()
        val title = message.notification?.title.orEmpty()

        when (type) {
            "doctor_peer_message" -> {
                if (DoctorSession.isInPeerChat) return
                val peerId = message.data["senderDoctorId"]?.toLongOrNull() ?: 0L
                val senderName = title.removePrefix("Коллега: ").ifBlank { "Коллега" }
                val body = message.notification?.body ?: message.data["body"] ?: "Новое сообщение"
                DoctorNotifier.showPeer(applicationContext, senderName, body, peerId)
            }
            "doctor_assignment",
            "doctor_message",
            "doctor_prescription",
            "doctor_report",
            "report_conclusion" -> {
                if (DoctorSession.isInDoctorChat) return
                val assignmentId = message.data["assignmentId"]?.toLongOrNull() ?: 0L
                val notifyTitle = title.ifBlank { "Врач" }
                val body = message.notification?.body ?: message.data["body"] ?: "Новое уведомление"
                val openDoctorPanel = type in setOf("doctor_assignment", "doctor_report")
                DoctorNotifier.show(
                    applicationContext,
                    notifyTitle,
                    body,
                    assignmentId = assignmentId,
                    openDoctorPanel = openDoctorPanel
                )
            }
            "support_escalation" -> {
                if (title.contains("Нужен Умник", ignoreCase = true) || type == "support_escalation") {
                    val body = message.notification?.body ?: message.data["body"] ?: "Пользователь ждёт ответа"
                    val username = message.data["username"] ?: "Пользователь"
                    SupportAdminNotifier.showEscalation(applicationContext, username, body)
                    applicationContext.getSharedPreferences("medtest_session", MODE_PRIVATE)
                        .edit()
                        .putLong("admin_last_escalation_at", System.currentTimeMillis())
                        .apply()
                }
            }
            "support_reply" -> {
                if (SupportChatSession.isUserInChat) return
                val body = message.notification?.body ?: message.data["body"] ?: "Вам ответили на сообщение"
                SupportChatNotifier.showNewAdminReply(
                    applicationContext,
                    body,
                    senderName = SupportAgentInfo.HUMAN_NAME
                )
                val prefs = applicationContext.getSharedPreferences("medtest_session", MODE_PRIVATE)
                val messageId = message.data["messageId"]?.toLongOrNull()
                if (messageId != null) {
                    val prev = prefs.getLong("support_last_notified_admin_id", 0L)
                    if (messageId > prev) {
                        prefs.edit().putLong("support_last_notified_admin_id", messageId).apply()
                    }
                }
            }
            else -> {
                if (title.contains("поддерж", ignoreCase = true)) {
                    if (SupportChatSession.isUserInChat) return
                    val body = message.notification?.body ?: message.data["body"] ?: "Вам ответили на сообщение"
                    SupportChatNotifier.showNewAdminReply(
                        applicationContext,
                        body,
                        senderName = SupportAgentInfo.HUMAN_NAME
                    )
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        // Token is registered from the main app after login.
    }
}
