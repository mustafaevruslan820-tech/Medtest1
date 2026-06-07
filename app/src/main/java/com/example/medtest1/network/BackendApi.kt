package com.example.medtest1.network

import com.example.medtest1.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class BackendAuthResult(
    val ok: Boolean,
    val username: String? = null,
    val email: String? = null,
    val userId: Long = 0L,
    val role: String = "patient",
    val token: String? = null,
    val error: String? = null
)

data class DoctorProfile(
    val userId: Long,
    val username: String,
    val specialty: String,
    val fullName: String,
    val experienceYears: Int,
    val education: String,
    val bio: String,
    val photoBase64: String? = null,
    val profileComplete: Boolean = false,
    val onDuty: Boolean = false
)

data class DoctorAssignment(
    val id: Long,
    val doctorUserId: Long,
    val patientUserId: Long,
    val doctorUsername: String,
    val doctorSpecialty: String = "",
    val patientUsername: String,
    val status: String,
    val assignedAt: Long,
    val patientProfileJson: String? = null,
    val treatmentSyncJson: String? = null,
    val rejectionReason: String? = null,
    val rejectedAt: Long? = null
)

data class ApiActionResult(
    val ok: Boolean,
    val error: String? = null,
    val httpCode: Int = -1
)

data class DoctorShiftInfo(
    val id: Long,
    val startedAt: Long,
    val endedAt: Long? = null,
    val isActive: Boolean = false
)

data class DoctorMessage(
    val id: Long,
    val assignmentId: Long,
    val sender: String,
    val text: String,
    val createdAt: Long
)

data class DoctorPeerMessage(
    val id: Long,
    val senderUserId: Long,
    val recipientUserId: Long,
    val text: String,
    val createdAt: Long,
    val senderIsMe: Boolean
)

data class DoctorPrescription(
    val id: Long,
    val prescriptionText: String,
    val treatmentPlanText: String,
    val createdAt: Long,
    val patientStatus: String = "pending",
    val declineReason: String? = null,
    val respondedAt: Long? = null
)

data class CareEvent(
    val id: Long,
    val assignmentId: Long,
    val eventType: String,
    val createdAt: Long,
    val metadataJson: String? = null
)

data class AssignmentDetail(
    val assignment: DoctorAssignment?,
    val prescriptions: List<DoctorPrescription>,
    val reports: List<TreatmentReportSummary>,
    val careEvents: List<CareEvent>
)

data class TreatmentReportSummary(
    val id: Long,
    val status: String,
    val doctorConclusion: String? = null,
    val doctorSignedAt: Long? = null,
    val createdAt: Long
)

data class DoctorAccountSummary(
    val id: Long,
    val username: String,
    val email: String,
    val specialty: String,
    val fullName: String,
    val experienceYears: Int,
    val createdAt: Long,
    val lastLoginAt: Long? = null
)

data class AdminDoctorCreateResult(
    val ok: Boolean,
    val error: String? = null,
    val httpCode: Int = -1
)

data class SupportMessage(
    val id: Long,
    val conversationId: Long,
    val sender: String,
    val text: String,
    val createdAt: Long,
    val replyToMessageId: Long? = null,
    val imageUrl: String? = null,
    val replyPreviewText: String? = null,
    val replyPreviewSender: String? = null,
    val replyPreviewAuthor: String? = null,
    val replyPreviewImageUrl: String? = null
)

data class SupportConversation(
    val id: Long,
    val userId: Long,
    val username: String,
    val email: String,
    val createdAt: Long,
    val updatedAt: Long,
    val unreadCount: Int = 0,
    val needsAdminAttention: Boolean = false,
    val adminRequestedAt: Long = 0L
)

data class BackendPasswordResetResult(
    val ok: Boolean,
    val error: String? = null,
    val devCode: String? = null,
    /** true, если код реально отправлен на почту (нет только консоли). */
    val mailDelivered: Boolean? = null
)

data class SendSupportMessageResult(
    val ok: Boolean,
    val error: String? = null,
    val httpCode: Int = -1
)

data class AdminConversationsResult(
    val conversations: List<SupportConversation>,
    val httpCode: Int,
    val unauthorized: Boolean = false,
    val networkError: Boolean = false
)

data class BackendHealth(
    val ok: Boolean,
    val version: Int = 0,
    val doctorApi: Boolean = false
)

object BackendApi {
    private val baseUrl: String = BuildConfig.BACKEND_BASE_URL
        .trim()
        .let { raw ->
            if (raw.startsWith("http://", ignoreCase = true) || raw.startsWith("https://", ignoreCase = true)) {
                raw
            } else {
                "http://$raw"
            }
        }
        .trimEnd('/')

    suspend fun register(
        username: String,
        email: String,
        password: String,
        deviceId: String,
        appVersion: String,
        platform: String = "android"
    ): BackendAuthResult = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("username", username)
            .put("email", email)
            .put("password", password)
            .put("deviceId", deviceId)
            .put("appVersion", appVersion)
            .put("platform", platform)
        postJson("$baseUrl/api/auth/register", body)
    }

    suspend fun login(
        usernameOrEmail: String,
        password: String,
        deviceId: String,
        appVersion: String,
        platform: String = "android"
    ): BackendAuthResult = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("usernameOrEmail", usernameOrEmail)
            .put("password", password)
            .put("deviceId", deviceId)
            .put("appVersion", appVersion)
            .put("platform", platform)
        postJson("$baseUrl/api/auth/login", body)
    }

    /** Обновить пароль в SQLite по Firebase idToken (после сброса пароля в Firebase). */
    suspend fun syncPasswordFromFirebase(
        idToken: String,
        password: String,
        deviceId: String,
        appVersion: String,
        platform: String = "android"
    ): BackendAuthResult = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("idToken", idToken)
            .put("password", password)
            .put("deviceId", deviceId)
            .put("appVersion", appVersion)
            .put("platform", platform)
        postJson("$baseUrl/api/auth/sync-password-from-firebase", body)
    }

    suspend fun trackInstall(
        deviceId: String,
        appVersion: String,
        username: String? = null,
        platform: String = "android"
    ): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("deviceId", deviceId)
            .put("appVersion", appVersion)
            .put("platform", platform)
        if (!username.isNullOrBlank()) body.put("username", username)
        val res = rawPost("$baseUrl/api/events/install", body, bearerToken = null, adminKey = null)
        res.first in 200..299
    }

    suspend fun deleteMe(token: String): Boolean = withContext(Dispatchers.IO) {
        val url = URL("$baseUrl/api/users/me")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            setRequestProperty("Authorization", "Bearer $token")
            connectTimeout = 12_000
            readTimeout = 12_000
        }
        try {
            val code = conn.responseCode
            code in 200..299
        } finally {
            conn.disconnect()
        }
    }

    suspend fun getSupportMessages(token: String): List<SupportMessage> = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/support/messages", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
        val json = runCatching { JSONObject(text) }.getOrNull() ?: return@withContext emptyList()
        val arr = json.optJSONArray("messages") ?: return@withContext emptyList()
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                add(parseSupportMessage(o))
            }
        }
    }

    suspend fun uploadSupportImage(
        token: String,
        imageBase64: String,
        imageMimeType: String
    ): String? = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("imageBase64", imageBase64)
            .put("imageMimeType", imageMimeType)
        val (code, responseText) = rawPost(
            url = "$baseUrl/api/support/upload-image",
            body = body,
            bearerToken = token,
            adminKey = null,
            readTimeoutMs = 90_000
        )
        if (code !in 200..299 || responseText.isNullOrBlank()) return@withContext null
        runCatching { JSONObject(responseText).optString("imageUrl").ifBlank { null } }.getOrNull()
    }

    suspend fun sendSupportMessage(
        token: String,
        text: String,
        replyToMessageId: Long? = null,
        imageUrl: String? = null,
        imageBase64: String? = null,
        imageMimeType: String? = null
    ): SendSupportMessageResult = withContext(Dispatchers.IO) {
        val body = JSONObject().put("text", text)
        if (replyToMessageId != null && replyToMessageId > 0) {
            body.put("replyToMessageId", replyToMessageId)
        }
        if (!imageUrl.isNullOrBlank()) {
            body.put("imageUrl", imageUrl)
        } else if (!imageBase64.isNullOrBlank()) {
            body.put("imageBase64", imageBase64)
            body.put("imageMimeType", imageMimeType ?: "image/jpeg")
        }
        val (code, responseText) = rawPost(
            url = "$baseUrl/api/support/messages",
            body = body,
            bearerToken = token,
            adminKey = null,
            readTimeoutMs = 90_000
        )
        if (code in 200..299) {
            val imageMissing = (!imageUrl.isNullOrBlank() || !imageBase64.isNullOrBlank()) &&
                !responseHasSavedImage(responseText)
            if (imageMissing) {
                return@withContext SendSupportMessageResult(ok = false, error = "image_not_saved", httpCode = code)
            }
            return@withContext SendSupportMessageResult(ok = true, httpCode = code)
        }
        val err = runCatching { JSONObject(responseText.orEmpty()).optString("error") }
            .getOrNull()
            ?.ifBlank { null }
        SendSupportMessageResult(
            ok = false,
            error = err ?: if (code < 0) "network_error" else "server_error",
            httpCode = code
        )
    }

    suspend fun markSupportRead(token: String): Boolean = withContext(Dispatchers.IO) {
        val (code, _) = rawPost(
            "$baseUrl/api/support/read",
            JSONObject(),
            bearerToken = token,
            adminKey = null
        )
        code in 200..299
    }

    suspend fun registerFcmToken(token: String, fcmToken: String): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject().put("token", fcmToken)
        val (code, _) = rawPost("$baseUrl/api/users/fcm-token", body, bearerToken = token, adminKey = null)
        code in 200..299
    }

    suspend fun forgotPassword(username: String, email: String): BackendPasswordResetResult =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("username", username.trim())
                .put("email", email.trim().lowercase())
            val (code, text) = rawPost("$baseUrl/api/auth/forgot-password", body, bearerToken = null, adminKey = null)
            when {
                code < 0 -> BackendPasswordResetResult(ok = false, error = "network_error")
                text.isNullOrBlank() -> BackendPasswordResetResult(ok = false, error = "empty_response")
                else -> {
                    val json = runCatching { JSONObject(text) }.getOrNull()
                        ?: return@withContext BackendPasswordResetResult(ok = false, error = "bad_json")
                    val ok = json.optBoolean("ok", false)
                    val err = json.optString("error").ifBlank { null }
                    val dev = json.optString("devCode").ifBlank { null }
                    val mailDel = if (json.has("mailDelivered")) json.optBoolean("mailDelivered") else null
                    when {
                        ok && code in 200..299 -> BackendPasswordResetResult(ok = true, devCode = dev, mailDelivered = mailDel)
                        err != null -> BackendPasswordResetResult(ok = false, error = err)
                        code !in 200..299 -> BackendPasswordResetResult(ok = false, error = "server_error")
                        else -> BackendPasswordResetResult(ok = false, error = "unknown")
                    }
                }
            }
        }

    suspend fun resetPasswordWithCode(
        username: String,
        email: String,
        code: String,
        newPassword: String
    ): BackendPasswordResetResult =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("username", username.trim())
                .put("email", email.trim().lowercase())
                .put("code", code.trim())
                .put("newPassword", newPassword)
            val (codeHttp, text) = rawPost("$baseUrl/api/auth/reset-password", body, bearerToken = null, adminKey = null)
            when {
                codeHttp < 0 -> BackendPasswordResetResult(ok = false, error = "network_error")
                text.isNullOrBlank() -> BackendPasswordResetResult(ok = false, error = "empty_response")
                else -> {
                    val json = runCatching { JSONObject(text) }.getOrNull()
                        ?: return@withContext BackendPasswordResetResult(ok = false, error = "bad_json")
                    val ok = json.optBoolean("ok", false) && codeHttp in 200..299
                    val err = json.optString("error").ifBlank { null }
                    if (ok) BackendPasswordResetResult(ok = true)
                    else BackendPasswordResetResult(ok = false, error = err ?: "unknown")
                }
            }
        }

    suspend fun verifyResetCode(
        username: String,
        email: String,
        code: String
    ): BackendPasswordResetResult =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("username", username.trim())
                .put("email", email.trim().lowercase())
                .put("code", code.trim())
            val (codeHttp, text) = rawPost("$baseUrl/api/auth/verify-reset-code", body, bearerToken = null, adminKey = null)
            when {
                codeHttp < 0 -> BackendPasswordResetResult(ok = false, error = "network_error")
                text.isNullOrBlank() -> BackendPasswordResetResult(ok = false, error = "empty_response")
                else -> {
                    val json = runCatching { JSONObject(text) }.getOrNull()
                        ?: return@withContext BackendPasswordResetResult(ok = false, error = "bad_json")
                    val ok = json.optBoolean("ok", false) && codeHttp in 200..299
                    val err = json.optString("error").ifBlank { null }
                    if (ok) BackendPasswordResetResult(ok = true)
                    else BackendPasswordResetResult(ok = false, error = err ?: "unknown")
                }
            }
        }

    suspend fun adminListSupportConversationsDetailed(adminKey: String): AdminConversationsResult =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet("$baseUrl/api/admin/support/conversations", bearerToken = null, adminKey = adminKey)
            when {
                code < 0 -> AdminConversationsResult(
                    conversations = emptyList(),
                    httpCode = code,
                    networkError = true
                )
                code == 401 -> AdminConversationsResult(
                    conversations = emptyList(),
                    httpCode = code,
                    unauthorized = true
                )
                code !in 200..299 || text.isNullOrBlank() -> AdminConversationsResult(
                    conversations = emptyList(),
                    httpCode = code,
                    networkError = true
                )
                else -> {
                    val json = runCatching { JSONObject(text) }.getOrNull()
                    if (json == null) {
                        return@withContext AdminConversationsResult(
                            conversations = emptyList(),
                            httpCode = code,
                            networkError = true
                        )
                    }
                    val arr = json.optJSONArray("conversations")
                        ?: return@withContext AdminConversationsResult(
                            emptyList(),
                            httpCode = code,
                            networkError = true
                        )
                    val list = buildList {
                        for (i in 0 until arr.length()) {
                            val o = arr.optJSONObject(i) ?: continue
                            add(
                                SupportConversation(
                                    id = o.optLong("id"),
                                    userId = o.optLong("userId"),
                                    username = o.optString("username"),
                                    email = o.optString("email"),
                                    createdAt = o.optLong("createdAt"),
                                    updatedAt = o.optLong("updatedAt"),
                                    unreadCount = o.optInt("unreadCount", 0),
                                    needsAdminAttention = o.optBoolean("needsAdminAttention", false),
                                    adminRequestedAt = o.optLong("adminRequestedAt", 0L)
                                )
                            )
                        }
                    }
                    AdminConversationsResult(conversations = list, httpCode = code)
                }
            }
        }

    suspend fun adminGetSupportMessages(adminKey: String, conversationId: Long): List<SupportMessage> =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet(
                "$baseUrl/api/admin/support/conversations/$conversationId/messages",
                bearerToken = null,
                adminKey = adminKey
            )
            if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
            val json = runCatching { JSONObject(text) }.getOrNull() ?: return@withContext emptyList()
            val arr = json.optJSONArray("messages") ?: return@withContext emptyList()
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    add(parseSupportMessage(o))
                }
            }
        }

    suspend fun adminUploadSupportImage(
        adminKey: String,
        conversationId: Long,
        imageBase64: String,
        imageMimeType: String
    ): String? = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("imageBase64", imageBase64)
            .put("imageMimeType", imageMimeType)
        val (code, responseText) = rawPost(
            url = "$baseUrl/api/admin/support/conversations/$conversationId/upload-image",
            body = body,
            bearerToken = null,
            adminKey = adminKey,
            readTimeoutMs = 90_000
        )
        if (code !in 200..299 || responseText.isNullOrBlank()) return@withContext null
        runCatching { JSONObject(responseText).optString("imageUrl").ifBlank { null } }.getOrNull()
    }

    suspend fun adminSendSupportMessage(
        adminKey: String,
        conversationId: Long,
        text: String,
        replyToMessageId: Long? = null,
        imageUrl: String? = null,
        imageBase64: String? = null,
        imageMimeType: String? = null
    ): SendSupportMessageResult =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("text", text)
            if (replyToMessageId != null && replyToMessageId > 0) {
                body.put("replyToMessageId", replyToMessageId)
            }
            if (!imageUrl.isNullOrBlank()) {
                body.put("imageUrl", imageUrl)
            } else if (!imageBase64.isNullOrBlank()) {
                body.put("imageBase64", imageBase64)
                body.put("imageMimeType", imageMimeType ?: "image/jpeg")
            }
            val (code, responseText) = rawPost(
                url = "$baseUrl/api/admin/support/conversations/$conversationId/messages",
                body = body,
                bearerToken = null,
                adminKey = adminKey,
                readTimeoutMs = 90_000
            )
            if (code in 200..299) {
                val imageMissing = (!imageUrl.isNullOrBlank() || !imageBase64.isNullOrBlank()) &&
                    !responseHasSavedImage(responseText)
                if (imageMissing) {
                    return@withContext SendSupportMessageResult(ok = false, error = "image_not_saved", httpCode = code)
                }
                return@withContext SendSupportMessageResult(ok = true, httpCode = code)
            }
            val err = runCatching { JSONObject(responseText.orEmpty()).optString("error") }
                .getOrNull()
                ?.ifBlank { null }
            SendSupportMessageResult(
                ok = false,
                error = err ?: if (code < 0) "network_error" else "server_error",
                httpCode = code
            )
        }

    private fun responseHasSavedImage(responseText: String?): Boolean {
        val json = runCatching { JSONObject(responseText.orEmpty()) }.getOrNull() ?: return false
        val message = json.optJSONObject("message") ?: return false
        return !message.optString("imageUrl").isNullOrBlank()
    }

    private fun parseSupportMessage(o: org.json.JSONObject): SupportMessage {
        val replyRaw = if (o.has("replyToMessageId") && !o.isNull("replyToMessageId")) {
            o.optLong("replyToMessageId").takeIf { it > 0 }
        } else {
            null
        }
        return SupportMessage(
            id = o.optLong("id"),
            conversationId = o.optLong("conversationId"),
            sender = o.optString("sender"),
            text = o.optString("text"),
            createdAt = o.optLong("createdAt"),
            replyToMessageId = replyRaw,
            imageUrl = o.optString("imageUrl").ifBlank { null },
            replyPreviewText = o.optString("replyPreviewText").ifBlank { null },
            replyPreviewSender = o.optString("replyPreviewSender").ifBlank { null },
            replyPreviewAuthor = o.optString("replyPreviewAuthor").ifBlank { null },
            replyPreviewImageUrl = o.optString("replyPreviewImageUrl").ifBlank { null }
        )
    }

    private fun postJson(url: String, body: JSONObject): BackendAuthResult {
        val (code, responseText) = rawPost(url, body, bearerToken = null, adminKey = null)
        if (code < 0) {
            return BackendAuthResult(ok = false, error = "network_error")
        }
        if (responseText.isNullOrBlank()) {
            return BackendAuthResult(ok = false, error = "empty_response")
        }
        val json = runCatching { JSONObject(responseText) }.getOrNull()
            ?: return BackendAuthResult(ok = false, error = "bad_json")

        val ok = json.optBoolean("ok", false) && code in 200..299
        val error = json.optString("error").ifBlank { null }
        val token = json.optString("token").ifBlank { null }
        val userObj = json.optJSONObject("user")
        val username = userObj?.optString("username")?.ifBlank { null }
        val email = userObj?.optString("email")?.ifBlank { null }
        val userId = userObj?.optLong("id", 0L) ?: 0L
        val role = userObj?.optString("role")?.ifBlank { null } ?: "patient"
        return BackendAuthResult(
            ok = ok,
            username = username,
            email = email,
            userId = userId,
            role = role,
            token = token,
            error = error
        )
    }

    suspend fun fetchHealth(): BackendHealth = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/health", bearerToken = null, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) {
            return@withContext BackendHealth(ok = false)
        }
        val json = runCatching { JSONObject(text) }.getOrNull()
            ?: return@withContext BackendHealth(ok = false)
        val features = json.optJSONObject("features")
        val doctorFromHealth = features?.optBoolean("doctorApi", false) == true
        BackendHealth(
            ok = json.optBoolean("ok", false),
            version = json.optInt("version", 0),
            doctorApi = doctorFromHealth || probeDoctorApiRoute()
        )
    }

    /** 401/403 = маршрут есть; 404 = на сервере старая сборка без doctorApi.js */
    private fun probeDoctorApiRoute(): Boolean {
        val (code, _) = rawGet("$baseUrl/api/admin/doctors", bearerToken = null, adminKey = null)
        return code != 404
    }

    suspend fun fetchMe(token: String): BackendAuthResult = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/users/me", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) {
            return@withContext BackendAuthResult(ok = false, error = "network_error")
        }
        val json = runCatching { JSONObject(text) }.getOrNull()
            ?: return@withContext BackendAuthResult(ok = false, error = "bad_json")
        val user = json.optJSONObject("user") ?: return@withContext BackendAuthResult(ok = false)
        BackendAuthResult(
            ok = true,
            userId = user.optLong("id"),
            username = user.optString("username"),
            email = user.optString("email"),
            role = user.optString("role", "patient")
        )
    }

    suspend fun adminCreateDoctor(
        token: String,
        adminKey: String,
        username: String,
        email: String,
        password: String,
        specialty: String
    ): AdminDoctorCreateResult = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("username", username.trim())
            .put("email", email.trim().lowercase())
            .put("password", password)
            .put("specialty", specialty.trim())
        val (code, text) = rawPost(
            "$baseUrl/api/admin/doctors",
            body,
            bearerToken = token.ifBlank { null },
            adminKey = adminKey.ifBlank { null }
        )
        if (code in 200..299) {
            AdminDoctorCreateResult(ok = true, httpCode = code)
        } else {
            val err = runCatching { JSONObject(text.orEmpty()).optString("error") }
                .getOrNull()
                ?.ifBlank { null }
            AdminDoctorCreateResult(
                ok = false,
                error = err ?: if (code < 0) "network_error" else "server_error",
                httpCode = code
            )
        }
    }

    suspend fun adminDeleteDoctor(token: String, adminKey: String, doctorUserId: Long): Boolean =
        withContext(Dispatchers.IO) {
            val url = URL("$baseUrl/api/admin/doctors/$doctorUserId")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("Accept", "application/json")
                if (token.isNotBlank()) setRequestProperty("Authorization", "Bearer $token")
                if (adminKey.isNotBlank()) setRequestProperty("x-admin-key", adminKey)
                connectTimeout = 12_000
                readTimeout = 12_000
            }
            try {
                conn.responseCode in 200..299
            } finally {
                conn.disconnect()
            }
        }

    suspend fun adminListDoctors(token: String, adminKey: String): List<DoctorAccountSummary> =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet(
                "$baseUrl/api/admin/doctors",
                bearerToken = token.ifBlank { null },
                adminKey = adminKey.ifBlank { null }
            )
            if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
            val arr = runCatching { JSONObject(text).optJSONArray("doctors") }.getOrNull()
                ?: return@withContext emptyList()
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    add(
                        DoctorAccountSummary(
                            id = o.optLong("id"),
                            username = o.optString("username"),
                            email = o.optString("email"),
                            specialty = o.optString("specialty"),
                            fullName = o.optString("fullName"),
                            experienceYears = o.optInt("experienceYears"),
                            createdAt = o.optLong("createdAt"),
                            lastLoginAt = o.optLong("lastLoginAt").takeIf { it > 0L }
                        )
                    )
                }
            }
        }

    suspend fun listOnDutyDoctors(token: String): List<DoctorProfile> = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/doctors/on-duty", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
        parseDoctorList(text)
    }

    suspend fun getDoctorProfile(token: String, doctorUserId: Long): DoctorProfile? =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet(
                "$baseUrl/api/doctors/$doctorUserId/profile",
                bearerToken = token,
                adminKey = null
            )
            if (code !in 200..299 || text.isNullOrBlank()) return@withContext null
            runCatching { JSONObject(text).optJSONObject("profile") }.getOrNull()?.let(::parseDoctorProfile)
        }

    suspend fun getMyDoctorProfile(token: String): DoctorProfile? = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/doctors/me/profile", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext null
        runCatching { JSONObject(text).optJSONObject("profile") }.getOrNull()?.let(::parseDoctorProfile)
    }

    suspend fun updateMyDoctorProfile(
        token: String,
        specialty: String,
        fullName: String,
        experienceYears: Int,
        education: String,
        bio: String,
        photoBase64: String?
    ): DoctorProfile? = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("specialty", specialty)
            .put("fullName", fullName)
            .put("experienceYears", experienceYears)
            .put("education", education)
            .put("bio", bio)
        if (!photoBase64.isNullOrBlank()) body.put("photoBase64", photoBase64)
        val (code, text) = rawPut("$baseUrl/api/doctors/me/profile", body, token)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext null
        runCatching { JSONObject(text).optJSONObject("profile") }.getOrNull()?.let(::parseDoctorProfile)
    }

    suspend fun getMyShift(token: String): Pair<Boolean, Boolean> = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/doctors/me/shift", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext false to false
        val json = runCatching { JSONObject(text) }.getOrNull() ?: return@withContext false to false
        json.optBoolean("ok", false) to json.optBoolean("onDuty", false)
    }

    suspend fun getMyShiftDetail(token: String): DoctorShiftInfo? = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/doctors/me/shift", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext null
        val json = runCatching { JSONObject(text) }.getOrNull() ?: return@withContext null
        val shift = json.optJSONObject("shift") ?: return@withContext null
        DoctorShiftInfo(
            id = shift.optLong("id"),
            startedAt = shift.optLong("startedAt"),
            endedAt = shift.optLong("endedAt").takeIf { it > 0L },
            isActive = shift.optBoolean("isActive", false)
        )
    }

    suspend fun startShift(token: String): Boolean = withContext(Dispatchers.IO) {
        val (code, _) = rawPost("$baseUrl/api/doctors/me/shift/start", JSONObject(), token, null)
        code in 200..299
    }

    suspend fun endShift(token: String): Boolean = withContext(Dispatchers.IO) {
        val (code, _) = rawPost("$baseUrl/api/doctors/me/shift/end", JSONObject(), token, null)
        code in 200..299
    }

    suspend fun assignDoctor(token: String, doctorUserId: Long, patientProfileJson: String): DoctorAssignment? =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("doctorUserId", doctorUserId)
                .put("patientProfileJson", patientProfileJson)
            val (code, text) = rawPost("$baseUrl/api/doctors/assign", body, token, null)
            if (code !in 200..299 || text.isNullOrBlank()) return@withContext null
            val a = runCatching { JSONObject(text).optJSONObject("assignment") }.getOrNull()
                ?: return@withContext null
            DoctorAssignment(
                id = a.optLong("id"),
                doctorUserId = a.optLong("doctorUserId"),
                patientUserId = 0L,
                doctorUsername = a.optString("doctorUsername"),
                patientUsername = "",
                status = "active",
                assignedAt = a.optLong("assignedAt")
            )
        }

    suspend fun rejectDoctorAssignment(token: String, reason: String): ApiActionResult =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("reason", reason.trim())
            val (code, text) = rawPost("$baseUrl/api/patient/assignment/reject", body, token, null)
            if (code in 200..299) return@withContext ApiActionResult(ok = true, httpCode = code)
            val err = runCatching { JSONObject(text.orEmpty()).optString("error") }
                .getOrNull()
                ?.ifBlank { null }
            ApiActionResult(ok = false, error = err, httpCode = code)
        }

    suspend fun getDoctorRejections(token: String): List<DoctorAssignment> = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/doctors/me/rejections", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
        val arr = runCatching { JSONObject(text).optJSONArray("rejections") }.getOrNull()
            ?: return@withContext emptyList()
        buildList {
            for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { add(parseAssignment(it)) }
            }
        }
    }

    suspend fun getPatientAssignment(token: String): DoctorAssignment? = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/patient/assignment", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext null
        val a = runCatching { JSONObject(text).optJSONObject("assignment") }.getOrNull()
            ?: return@withContext null
        parseAssignment(a)
    }

    suspend fun getDoctorAssignments(token: String): List<DoctorAssignment> = withContext(Dispatchers.IO) {
        val (code, text) = rawGet("$baseUrl/api/doctors/me/assignments", bearerToken = token, adminKey = null)
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
        val arr = runCatching { JSONObject(text).optJSONArray("assignments") }.getOrNull()
            ?: return@withContext emptyList()
        buildList {
            for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { add(parseAssignment(it)) }
            }
        }
    }

    suspend fun getDoctorCompletedAssignments(token: String): List<DoctorAssignment> = withContext(Dispatchers.IO) {
        val (code, text) = rawGet(
            "$baseUrl/api/doctors/me/assignments/completed",
            bearerToken = token,
            adminKey = null
        )
        if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
        val arr = runCatching { JSONObject(text).optJSONArray("assignments") }.getOrNull()
            ?: return@withContext emptyList()
        buildList {
            for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { add(parseAssignment(it)) }
            }
        }
    }

    suspend fun getDoctorPeerMessages(token: String, otherDoctorId: Long): List<DoctorPeerMessage> =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet(
                "$baseUrl/api/doctors/peer/$otherDoctorId/messages",
                bearerToken = token,
                adminKey = null
            )
            if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
            val arr = runCatching { JSONObject(text).optJSONArray("messages") }.getOrNull()
                ?: return@withContext emptyList()
            buildList {
                for (i in 0 until arr.length()) {
                    val m = arr.optJSONObject(i) ?: continue
                    add(
                        DoctorPeerMessage(
                            id = m.optLong("id"),
                            senderUserId = m.optLong("senderUserId"),
                            recipientUserId = m.optLong("recipientUserId"),
                            text = m.optString("text"),
                            createdAt = m.optLong("createdAt"),
                            senderIsMe = m.optBoolean("senderIsMe", false)
                        )
                    )
                }
            }
        }

    suspend fun sendDoctorPeerMessage(token: String, otherDoctorId: Long, text: String): Boolean =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("text", text.trim())
            val (code, _) = rawPost(
                "$baseUrl/api/doctors/peer/$otherDoctorId/messages",
                body,
                token,
                null
            )
            code in 200..299
        }

    suspend fun getAssignmentDetail(token: String, assignmentId: Long): AssignmentDetail =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet(
                "$baseUrl/api/assignments/$assignmentId",
                bearerToken = token,
                adminKey = null
            )
            if (code !in 200..299 || text.isNullOrBlank()) {
                return@withContext AssignmentDetail(null, emptyList(), emptyList(), emptyList())
            }
            val json = runCatching { JSONObject(text) }.getOrNull()
                ?: return@withContext AssignmentDetail(null, emptyList(), emptyList(), emptyList())
            val assignment = json.optJSONObject("assignment")?.let(::parseAssignment)
            val prescriptions = json.optJSONArray("prescriptions")?.let { arr ->
                buildList {
                    for (i in 0 until arr.length()) {
                        val p = arr.optJSONObject(i) ?: continue
                        add(parsePrescription(p))
                    }
                }
            } ?: emptyList()
            val reports = json.optJSONArray("reports")?.let { arr ->
                buildList {
                    for (i in 0 until arr.length()) {
                        val r = arr.optJSONObject(i) ?: continue
                        add(
                            TreatmentReportSummary(
                                id = r.optLong("id"),
                                status = r.optString("status"),
                                doctorConclusion = r.optString("doctorConclusion").ifBlank { null },
                                doctorSignedAt = r.optLong("doctorSignedAt").takeIf { it > 0L },
                                createdAt = r.optLong("createdAt")
                            )
                        )
                    }
                }
            } ?: emptyList()
            val careEvents = json.optJSONArray("careEvents")?.let { arr ->
                buildList {
                    for (i in 0 until arr.length()) {
                        val e = arr.optJSONObject(i) ?: continue
                        add(parseCareEvent(e))
                    }
                }
            } ?: emptyList()
            AssignmentDetail(assignment, prescriptions, reports, careEvents)
        }

    suspend fun respondToPrescription(
        token: String,
        assignmentId: Long,
        prescriptionId: Long,
        action: String,
        reason: String? = null
    ): ApiActionResult = withContext(Dispatchers.IO) {
        val body = JSONObject().put("action", action)
        if (!reason.isNullOrBlank()) body.put("reason", reason.trim())
        val (code, text) = rawPost(
            "$baseUrl/api/assignments/$assignmentId/prescriptions/$prescriptionId/respond",
            body,
            token,
            null
        )
        if (code in 200..299) return@withContext ApiActionResult(ok = true, httpCode = code)
        if (code == 404 && action == "accept") {
            val fallbackText = "Пациент принял план лечения"
            if (sendDoctorMessage(token, assignmentId, fallbackText)) {
                return@withContext ApiActionResult(ok = true, httpCode = code)
            }
        }
        val err = runCatching { JSONObject(text.orEmpty()).optString("error") }
            .getOrNull()
            ?.ifBlank { null }
        ApiActionResult(ok = false, error = err, httpCode = code)
    }

    suspend fun recordCareEvent(
        token: String,
        assignmentId: Long,
        eventType: String
    ): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject().put("eventType", eventType)
        val (code, _) = rawPost("$baseUrl/api/assignments/$assignmentId/care-events", body, token, null)
        code in 200..299
    }

    suspend fun syncPatientTreatment(token: String, assignmentId: Long, treatmentSyncJson: String): Boolean =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("treatmentSyncJson", treatmentSyncJson)
            val (code, _) = rawPut("$baseUrl/api/assignments/$assignmentId/patient-sync", body, token)
            code in 200..299
        }

    suspend fun getDoctorMessages(token: String, assignmentId: Long): List<DoctorMessage> =
        withContext(Dispatchers.IO) {
            val (code, text) = rawGet(
                "$baseUrl/api/assignments/$assignmentId/messages",
                bearerToken = token,
                adminKey = null
            )
            if (code !in 200..299 || text.isNullOrBlank()) return@withContext emptyList()
            val arr = runCatching { JSONObject(text).optJSONArray("messages") }.getOrNull()
                ?: return@withContext emptyList()
            buildList {
                for (i in 0 until arr.length()) {
                    val m = arr.optJSONObject(i) ?: continue
                    add(
                        DoctorMessage(
                            id = m.optLong("id"),
                            assignmentId = m.optLong("assignmentId"),
                            sender = m.optString("sender"),
                            text = m.optString("text"),
                            createdAt = m.optLong("createdAt")
                        )
                    )
                }
            }
        }

    suspend fun sendDoctorMessage(token: String, assignmentId: Long, text: String): Boolean =
        withContext(Dispatchers.IO) {
            val body = JSONObject().put("text", text)
            val (code, _) = rawPost("$baseUrl/api/assignments/$assignmentId/messages", body, token, null)
            code in 200..299
        }

    suspend fun sendPrescription(
        token: String,
        assignmentId: Long,
        prescriptionText: String,
        treatmentPlanText: String
    ): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("prescriptionText", prescriptionText)
            .put("treatmentPlanText", treatmentPlanText)
        val (code, _) = rawPost("$baseUrl/api/assignments/$assignmentId/prescription", body, token, null)
        code in 200..299
    }

    suspend fun sendTreatmentReport(
        token: String,
        assignmentId: Long,
        reportDataJson: String,
        pdfBase64: String?
    ): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject().put("reportDataJson", reportDataJson)
        if (!pdfBase64.isNullOrBlank()) body.put("pdfBase64", pdfBase64)
        val (code, _) = rawPost("$baseUrl/api/assignments/$assignmentId/report", body, token, null)
        code in 200..299
    }

    suspend fun concludeReport(
        token: String,
        assignmentId: Long,
        reportId: Long,
        action: String,
        conclusion: String,
        newTreatmentPlanText: String?
    ): Boolean = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("action", action)
            .put("conclusion", conclusion)
        if (!newTreatmentPlanText.isNullOrBlank()) {
            body.put("newTreatmentPlanText", newTreatmentPlanText)
        }
        val (code, _) = rawPut(
            "$baseUrl/api/assignments/$assignmentId/report/$reportId/conclusion",
            body,
            token
        )
        code in 200..299
    }

    private fun parseDoctorList(text: String): List<DoctorProfile> {
        val arr = runCatching { JSONObject(text).optJSONArray("doctors") }.getOrNull()
            ?: return emptyList()
        return buildList {
            for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { add(parseDoctorProfile(it)) }
            }
        }
    }

    private fun parseDoctorProfile(o: JSONObject): DoctorProfile = DoctorProfile(
        userId = o.optLong("userId"),
        username = o.optString("username"),
        specialty = o.optString("specialty"),
        fullName = o.optString("fullName"),
        experienceYears = o.optInt("experienceYears"),
        education = o.optString("education"),
        bio = o.optString("bio"),
        photoBase64 = o.optString("photoBase64").ifBlank { null },
        profileComplete = o.optBoolean("profileComplete", false),
        onDuty = o.optBoolean("onDuty", false)
    )

    private fun parsePrescription(p: JSONObject): DoctorPrescription = DoctorPrescription(
        id = p.optLong("id"),
        prescriptionText = p.optString("prescriptionText"),
        treatmentPlanText = p.optString("treatmentPlanText"),
        createdAt = p.optLong("createdAt"),
        patientStatus = p.optString("patientStatus", "pending"),
        declineReason = p.optString("declineReason").ifBlank { null },
        respondedAt = p.optLong("respondedAt").takeIf { it > 0L }
    )

    private fun parseCareEvent(e: JSONObject): CareEvent = CareEvent(
        id = e.optLong("id"),
        assignmentId = e.optLong("assignmentId"),
        eventType = e.optString("eventType"),
        createdAt = e.optLong("createdAt"),
        metadataJson = e.optString("metadataJson").ifBlank { null }
    )

    private fun parseAssignment(o: JSONObject): DoctorAssignment = DoctorAssignment(
        id = o.optLong("id"),
        doctorUserId = o.optLong("doctorUserId"),
        patientUserId = o.optLong("patientUserId"),
        doctorUsername = o.optString("doctorUsername"),
        doctorSpecialty = o.optString("doctorSpecialty"),
        patientUsername = o.optString("patientUsername"),
        status = o.optString("status", "active"),
        assignedAt = o.optLong("assignedAt"),
        patientProfileJson = o.optString("patientProfileJson").ifBlank { null },
        treatmentSyncJson = o.optString("treatmentSyncJson").ifBlank { null },
        rejectionReason = o.optString("rejectionReason").ifBlank { null },
        rejectedAt = o.optLong("rejectedAt").takeIf { it > 0L }
    )

    private fun rawPut(url: String, body: JSONObject, bearerToken: String): Pair<Int, String?> {
        val payload = body.toString().toByteArray(Charsets.UTF_8)
        val conn = try {
            (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Authorization", "Bearer $bearerToken")
                connectTimeout = 20_000
                readTimeout = 20_000
                setFixedLengthStreamingMode(payload.size)
            }
        } catch (_: Exception) {
            return -1 to null
        }
        return try {
            conn.outputStream.use { it.write(payload) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            code to stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
        } catch (_: Exception) {
            -1 to null
        } finally {
            conn.disconnect()
        }
    }

    private fun rawGet(url: String, bearerToken: String?, adminKey: String?): Pair<Int, String?> {
        val conn = try {
            (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                if (!bearerToken.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $bearerToken")
                if (!adminKey.isNullOrBlank()) setRequestProperty("x-admin-key", adminKey)
                connectTimeout = 12_000
                readTimeout = 12_000
            }
        } catch (_: Exception) {
            return -1 to null
        }
        return try {
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
            code to text
        } catch (_: Exception) {
            -1 to null
        } finally {
            conn.disconnect()
        }
    }

    private fun rawPost(
        url: String,
        body: JSONObject,
        bearerToken: String?,
        adminKey: String?,
        readTimeoutMs: Int = 12_000
    ): Pair<Int, String?> {
        val payload = body.toString().toByteArray(Charsets.UTF_8)
        val conn = try {
            (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Length", payload.size.toString())
                if (!bearerToken.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $bearerToken")
                if (!adminKey.isNullOrBlank()) setRequestProperty("x-admin-key", adminKey)
                connectTimeout = 20_000
                readTimeout = readTimeoutMs
                setFixedLengthStreamingMode(payload.size)
            }
        } catch (_: Exception) {
            return -1 to null
        }
        return try {
            conn.outputStream.use { stream ->
                stream.write(payload)
                stream.flush()
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
            code to text
        } catch (_: Exception) {
            -1 to null
        } finally {
            conn.disconnect()
        }
    }
}

