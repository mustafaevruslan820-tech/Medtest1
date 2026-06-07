package com.example.medtest1.doctor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medtest1.network.BackendApi
import com.example.medtest1.network.DoctorAssignment
import com.example.medtest1.network.DoctorPeerUnreadItem
import com.example.medtest1.network.DoctorPeerUnreadSummary
import com.example.medtest1.network.DoctorPrescription
import com.example.medtest1.network.DoctorProfile
import com.example.medtest1.network.DoctorShiftInfo
import kotlinx.coroutines.delay
import com.example.medtest1.network.TreatmentReportSummary
import com.example.medtest1.reports.assignmentsToShiftLines
import com.example.medtest1.reports.exportDoctorShiftToPdf
import com.example.medtest1.ui.MedSectionLabel
import com.example.medtest1.ui.MedSubScreenLayout
import com.example.medtest1.ui.MedSurfaceCard
import com.example.medtest1.ui.theme.LocalMedAppColors
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class PatientShiftGroup(
    val title: String,
    val assignments: List<DoctorAssignment>
)

private fun groupPatientsByShift(assignments: List<DoctorAssignment>): List<PatientShiftGroup> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val todayStart = cal.timeInMillis
    val yesterdayStart = todayStart - 86_400_000L

    val today = mutableListOf<DoctorAssignment>()
    val yesterday = mutableListOf<DoctorAssignment>()
    val earlier = mutableListOf<DoctorAssignment>()
    assignments.forEach { assignment ->
        when {
            assignment.assignedAt >= todayStart -> today.add(assignment)
            assignment.assignedAt >= yesterdayStart -> yesterday.add(assignment)
            else -> earlier.add(assignment)
        }
    }
    return buildList {
        if (today.isNotEmpty()) add(PatientShiftGroup("Сегодняшняя смена", today))
        if (yesterday.isNotEmpty()) add(PatientShiftGroup("Вчерашняя смена", yesterday))
        if (earlier.isNotEmpty()) add(PatientShiftGroup("Ранее", earlier))
    }
}

@Composable
fun DoctorPanelScreen(
    modifier: Modifier = Modifier,
    tokenProvider: () -> String,
    doctorDisplayName: String,
    patientMilestoneSeenProvider: (Long) -> Int,
    onPatientMilestoneSeen: (Long, Int) -> Unit,
    onEditProfile: () -> Unit,
    onOpenChat: (assignmentId: Long, patientName: String) -> Unit,
    onOpenPeerChat: (doctorId: Long, doctorName: String, specialty: String) -> Unit,
    onBack: () -> Unit
) {
    val app = LocalMedAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var onDuty by remember { mutableStateOf(false) }
    var shiftLoading by remember { mutableStateOf(true) }
    var currentShift by remember { mutableStateOf<DoctorShiftInfo?>(null) }
    var assignments by remember { mutableStateOf<List<DoctorAssignment>>(emptyList()) }
    var completedAssignments by remember { mutableStateOf<List<DoctorAssignment>>(emptyList()) }
    var colleagues by remember { mutableStateOf<List<DoctorProfile>>(emptyList()) }
    var rejections by remember { mutableStateOf<List<DoctorAssignment>>(emptyList()) }
    var peerUnread by remember { mutableStateOf(DoctorPeerUnreadSummary(0, emptyList())) }
    var peerUnreadMap by remember { mutableStateOf<Map<Long, DoctorPeerUnreadItem>>(emptyMap()) }
    var listTab by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<DoctorAssignment?>(null) }
    var prescriptions by remember { mutableStateOf<List<DoctorPrescription>>(emptyList()) }
    var reports by remember { mutableStateOf<List<TreatmentReportSummary>>(emptyList()) }
    var patientMilestones by remember { mutableStateOf<List<CareMilestone>>(emptyList()) }
    var milestoneHighlightIndex by remember { mutableIntStateOf(-1) }
    val assignmentMilestones = remember { mutableStateMapOf<Long, List<CareMilestone>>() }
    var sendingPrescription by remember { mutableStateOf(false) }
    var conclusion by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var pendingShiftExport by remember { mutableStateOf<DoctorShiftInfo?>(null) }

    val shiftPdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        val shift = pendingShiftExport
        if (uri == null || shift == null) return@rememberLauncherForActivityResult
        scope.launch {
            val rxCounts = mutableMapOf<Long, Int>()
            val repCounts = mutableMapOf<Long, Int>()
            assignments.forEach { a ->
                val detail = BackendApi.getAssignmentDetail(tokenProvider(), a.id)
                rxCounts[a.id] = detail.prescriptions.size
                repCounts[a.id] = detail.reports.size
            }
            val shiftPatients = assignments.filter { it.assignedAt >= shift.startedAt }
            val ok = exportDoctorShiftToPdf(
                context = context,
                uri = uri,
                doctorName = doctorDisplayName,
                shiftStartedAt = shift.startedAt,
                shiftEndedAt = shift.endedAt ?: System.currentTimeMillis(),
                patients = assignmentsToShiftLines(shiftPatients, rxCounts, repCounts)
            )
            status = if (ok) "PDF-отчёт о смене сохранён." else "Не удалось сохранить PDF."
            pendingShiftExport = null
        }
    }

    fun refresh() {
        scope.launch {
            runCatching {
                val (_, duty) = BackendApi.getMyShift(tokenProvider())
                onDuty = duty
                currentShift = BackendApi.getMyShiftDetail(tokenProvider())
                assignments = BackendApi.getDoctorAssignments(tokenProvider())
                completedAssignments = BackendApi.getDoctorCompletedAssignments(tokenProvider())
                colleagues = BackendApi.listOnDutyDoctors(tokenProvider())
                    .filter { it.username != doctorDisplayName }
                rejections = BackendApi.getDoctorRejections(tokenProvider())
                peerUnread = BackendApi.getDoctorPeerUnreadSummary(tokenProvider())
                peerUnreadMap = peerUnread.items.associateBy { it.peerUserId }
                assignments.forEach { a ->
                    val detail = BackendApi.getAssignmentDetail(tokenProvider(), a.id)
                    assignmentMilestones[a.id] = computeDoctorViewMilestones(
                        prescriptions = detail.prescriptions,
                        reports = detail.reports,
                        careEvents = detail.careEvents
                    )
                    val latest = latestDoneMilestoneIndex(assignmentMilestones[a.id].orEmpty())
                    val seen = patientMilestoneSeenProvider(a.id)
                    if (latest > seen) {
                        onPatientMilestoneSeen(a.id, latest)
                    }
                }
            }.onFailure {
                status = "Не удалось обновить данные. Проверьте интернет."
            }
            shiftLoading = false
        }
    }

    fun loadDetail(assignment: DoctorAssignment) {
        scope.launch {
            val detail = BackendApi.getAssignmentDetail(tokenProvider(), assignment.id)
            selected = detail.assignment ?: assignment
            prescriptions = detail.prescriptions
            reports = detail.reports
            patientMilestones = computeDoctorViewMilestones(
                detail.prescriptions,
                detail.reports,
                detail.careEvents
            )
            val latest = latestDoneMilestoneIndex(patientMilestones)
            val seen = patientMilestoneSeenProvider(assignment.id)
            milestoneHighlightIndex = if (latest > seen) latest else -1
            if (latest > seen) onPatientMilestoneSeen(assignment.id, latest)
        }
    }

    LaunchedEffect(Unit) { refresh() }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            runCatching {
                val summary = BackendApi.getDoctorPeerUnreadSummary(tokenProvider())
                peerUnread = summary
                peerUnreadMap = summary.items.associateBy { it.peerUserId }
            }
        }
    }

    val shiftColor by animateColorAsState(
        targetValue = if (onDuty) app.accentRing else app.onHeroMuted,
        animationSpec = tween(400),
        label = "shift-color"
    )

    if (selected != null) {
        val assignment = selected!!
        MedSubScreenLayout(
            title = assignment.patientUsername,
            subtitle = if (assignment.status == "completed") "Лечение завершено" else "Пациент на лечении",
            onBack = { selected = null },
            modifier = modifier
        ) {
            if (patientMilestones.isNotEmpty()) {
                MedSurfaceCard {
                    Text("Прогресс пациента", fontWeight = FontWeight.Bold)
                    CareMilestoneRow(
                        milestones = patientMilestones,
                        highlightNewFromIndex = milestoneHighlightIndex
                    )
                }
            }
            MedSurfaceCard {
                Text("Профиль пациента", fontWeight = FontWeight.Bold)
                assignment.patientProfileJson?.let { json ->
                    runCatching {
                        val o = JSONObject(json)
                        Text("ФИО: ${o.optString("fullName")}")
                        Text("Дата рождения: ${o.optString("birthDate")}")
                        Text("Группа крови: ${o.optString("bloodType")}")
                        Text("Аллергии: ${o.optString("allergies")}")
                        Text("Хронические: ${o.optString("chronicDiseases")}")
                    }
                } ?: Text("Данные профиля не переданы.")
            }
            MedSurfaceCard {
                Text("План лечения и дневник", fontWeight = FontWeight.Bold)
                assignment.treatmentSyncJson?.let { json ->
                    runCatching {
                        val root = JSONObject(json)
                        val plans = root.optJSONArray("plans")
                        if (plans != null) {
                            for (i in 0 until plans.length()) {
                                val p = plans.getJSONObject(i)
                                Text("• ${p.optString("medicineName")} — ${p.optString("dosage")} в ${p.optString("reminderTime")}")
                            }
                        }
                        val wellbeing = root.optJSONObject("wellbeing")
                        if (wellbeing != null && wellbeing.length() > 0) {
                            Text("Дневник самочувствия:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                            wellbeing.keys().forEach { day ->
                                val e = wellbeing.getJSONObject(day)
                                Text("$day: ${e.optString("mood")} — ${e.optString("comment")}")
                            }
                        }
                    }
                } ?: Text("Пациент ещё не синхронизировал план.")
            }
            Button(
                onClick = { onOpenChat(assignment.id, assignment.patientUsername) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Чат с пациентом") }

            if (assignment.status != "completed") {
                MedSectionLabel("Назначить лечение")
                DoctorPrescriptionPlanner(
                    sending = sendingPrescription,
                    onPlanReady = { plan ->
                        sendingPrescription = true
                        scope.launch {
                            val summary = formatPrescriptionSummary(plan)
                            val json = structuredPlanToJson(plan)
                            val ok = BackendApi.sendPrescription(
                                token = tokenProvider(),
                                assignmentId = assignment.id,
                                prescriptionText = summary,
                                treatmentPlanText = json
                            )
                            sendingPrescription = false
                            status = if (ok) {
                                "План лечения отправлен пациенту на подтверждение."
                            } else {
                                "Не удалось отправить план."
                            }
                            if (ok) loadDetail(assignment)
                        }
                    }
                )
            }

            if (prescriptions.isNotEmpty()) {
                MedSectionLabel("Ранее выданные планы")
                prescriptions.forEach { rx ->
                    MedSurfaceCard {
                        Text(rx.prescriptionText, fontWeight = FontWeight.SemiBold)
                        val planPreview = formatStructuredPlanForPatientCard(rx.treatmentPlanText)
                        if (planPreview.isNotBlank()) {
                            Text(planPreview, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            "Статус: ${prescriptionStatusLabel(rx.patientStatus)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            val pendingReport = reports.firstOrNull { it.status == "pending" }
            if (pendingReport != null) {
                MedSectionLabel("Отчёт на заключение")
                MedSurfaceCard {
                    Text("Отчёт от ${formatTs(pendingReport.createdAt)}", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = conclusion,
                        onValueChange = { conclusion = it },
                        label = { Text("Заключение") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Text(
                        "После завершения пациент переместится во вкладку «Завершённые».",
                        style = MaterialTheme.typography.bodySmall,
                        color = app.onHeroMuted
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                val ok = BackendApi.concludeReport(
                                    token = tokenProvider(),
                                    assignmentId = assignment.id,
                                    reportId = pendingReport.id,
                                    action = "complete",
                                    conclusion = conclusion.ifBlank { "Лечение завершено." },
                                    newTreatmentPlanText = null
                                )
                                status = if (ok) {
                                    "Лечение завершено. Пациент перенесён в «Завершённые»."
                                } else {
                                    "Не удалось завершить лечение."
                                }
                                refresh()
                                selected = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Завершить лечение") }
                }
            }
            status?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
        return
    }

    MedSubScreenLayout(
        title = "Панель врача",
        subtitle = if (onDuty) "Смена открыта — пациенты видят вас" else "Начните смену для приёма пациентов",
        onBack = onBack,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(380)) + slideInVertically(tween(380)) { it / 8 }
        ) {
            DoctorShiftHeroCard(
                onDuty = onDuty,
                shiftLoading = shiftLoading,
                shiftColor = shiftColor,
                onToggleShift = {
                    scope.launch {
                        shiftLoading = true
                        if (onDuty) {
                            val shift = currentShift
                            BackendApi.endShift(tokenProvider())
                            if (shift != null) {
                                pendingShiftExport = shift.copy(
                                    endedAt = System.currentTimeMillis(),
                                    isActive = false
                                )
                                shiftPdfLauncher.launch(
                                    "shift_report_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.pdf"
                                )
                            }
                        } else {
                            BackendApi.startShift(tokenProvider())
                        }
                        refresh()
                    }
                },
                onEditProfile = onEditProfile,
                onExportPdf = currentShift?.let { shift ->
                    {
                        pendingShiftExport = shift.copy(
                            endedAt = System.currentTimeMillis(),
                            isActive = onDuty
                        )
                        shiftPdfLauncher.launch(
                            "shift_report_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.pdf"
                        )
                    }
                }
            )
        }

        DoctorPanelSectionGrid(
            selectedTab = listTab,
            patientsCount = assignments.size,
            completedCount = completedAssignments.size,
            colleaguesCount = colleagues.size,
            rejectionsCount = rejections.size,
            peerUnreadTotal = peerUnread.totalUnread,
            onSelectTab = { listTab = it }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (listTab) {
                0 -> {
                    MedSectionLabel("Активные пациенты")
                    if (assignments.isEmpty()) {
                        Text("Пока нет назначенных пациентов. Начните смену, чтобы вас видели.", modifier = Modifier.padding(8.dp))
                    } else {
                        groupPatientsByShift(assignments).forEach { group ->
                            MedSectionLabel(group.title)
                            group.assignments.forEach { a ->
                                PatientListCard(
                                    assignment = a,
                                    milestones = assignmentMilestones[a.id].orEmpty(),
                                    highlightIndex = latestDoneMilestoneIndex(assignmentMilestones[a.id].orEmpty()),
                                    seenIndex = patientMilestoneSeenProvider(a.id),
                                    statusLabel = "На лечении",
                                    onClick = { loadDetail(a) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    MedSectionLabel("Завершённые пациенты")
                    if (completedAssignments.isEmpty()) {
                        Text("Завершённых курсов пока нет.", modifier = Modifier.padding(8.dp))
                    } else {
                        completedAssignments.forEach { a ->
                            PatientListCard(
                                assignment = a,
                                milestones = emptyList(),
                                highlightIndex = -1,
                                seenIndex = -1,
                                statusLabel = "Лечение завершено",
                                onClick = { loadDetail(a) }
                            )
                        }
                    }
                }
                2 -> {
                    MedSectionLabel("Чат с коллегами")
                    if (peerUnread.totalUnread > 0) {
                        Text(
                            "Непрочитанных сообщений: ${peerUnread.totalUnread}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    val peerCards = buildColleagueChatList(colleagues, peerUnread)
                    if (!onDuty && peerCards.isEmpty()) {
                        Text("Начните смену, чтобы видеть коллег на смене.", modifier = Modifier.padding(8.dp))
                    } else if (peerCards.isEmpty()) {
                        Text("Сейчас на смене только вы.", modifier = Modifier.padding(8.dp))
                    } else {
                        peerCards.forEach { (doc, unread) ->
                            ColleagueChatCard(
                                doctor = doc,
                                unreadCount = unread?.unreadCount ?: 0,
                                lastPreview = unread?.lastMessagePreview,
                                onOpenChat = {
                                    onOpenPeerChat(
                                        doc.userId,
                                        doc.fullName.ifBlank { doc.username },
                                        doc.specialty
                                    )
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
                else -> {
                    MedSectionLabel("Пациенты, отказавшиеся от лечения")
                    if (rejections.isEmpty()) {
                        Text("Отказов пока нет.", modifier = Modifier.padding(8.dp))
                    } else {
                        rejections.forEach { r ->
                            MedSurfaceCard {
                                Text(r.patientUsername, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Отказ: ${formatTs(r.rejectedAt ?: r.assignedAt)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Причина: ${r.rejectionReason.orEmpty().ifBlank { "Не указана" }}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
        status?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
    }
}

@Composable
private fun PatientListCard(
    assignment: DoctorAssignment,
    milestones: List<CareMilestone>,
    highlightIndex: Int,
    seenIndex: Int,
    statusLabel: String,
    onClick: () -> Unit
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 10 }
    ) {
        MedSurfaceCard(modifier = Modifier.clickable(onClick = onClick)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(assignment.patientUsername, fontWeight = FontWeight.Bold)
                Text(
                    statusLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text("Назначен: ${formatTs(assignment.assignedAt)}", style = MaterialTheme.typography.bodySmall, color = app.onHeroMuted)
            if (milestones.isNotEmpty()) {
                CareMilestoneRow(
                    milestones = milestones,
                    highlightNewFromIndex = if (highlightIndex > seenIndex) highlightIndex else -1,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Text(
                "Открыть карточку пациента →",
                style = MaterialTheme.typography.labelSmall,
                color = app.onHeroMuted,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

private fun buildColleagueChatList(
    onDutyColleagues: List<DoctorProfile>,
    peerUnread: DoctorPeerUnreadSummary
): List<Pair<DoctorProfile, DoctorPeerUnreadItem?>> {
    val seen = mutableSetOf<Long>()
    val result = mutableListOf<Pair<DoctorProfile, DoctorPeerUnreadItem?>>()
    onDutyColleagues.forEach { doc ->
        seen.add(doc.userId)
        result.add(doc to peerUnread.items.firstOrNull { it.peerUserId == doc.userId })
    }
    peerUnread.items.forEach { item ->
        if (item.peerUserId !in seen) {
            seen.add(item.peerUserId)
            result.add(
                DoctorProfile(
                    userId = item.peerUserId,
                    username = item.peerUsername,
                    specialty = item.peerSpecialty,
                    fullName = item.peerFullName,
                    experienceYears = 0,
                    education = "",
                    bio = "",
                    onDuty = false
                ) to item
            )
        }
    }
    return result
}

private fun formatTs(ts: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(ts))

private fun prescriptionStatusLabel(status: String): String = when (status) {
    "accepted" -> "Принят пациентом"
    "declined" -> "Отклонён"
    else -> "Ожидает ответа"
}
