package com.example.medtest1.doctor

import com.example.medtest1.data.TreatmentPlan
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

data class PrescriptionMedicineEntry(
    val medicineName: String,
    val dosage: String,
    val reminderTime: String,
    val notes: String = ""
)

data class PrescriptionDayPlan(
    val date: String,
    val medicines: List<PrescriptionMedicineEntry>
)

data class StructuredPrescriptionPlan(
    val startDate: String,
    val endDate: String,
    val days: List<PrescriptionDayPlan>
)

fun structuredPlanToJson(plan: StructuredPrescriptionPlan): String {
    val daysArr = JSONArray()
    plan.days.forEach { day ->
        val meds = JSONArray()
        day.medicines.forEach { m ->
            meds.put(
                JSONObject()
                    .put("name", m.medicineName)
                    .put("dosage", m.dosage)
                    .put("time", m.reminderTime)
                    .put("notes", m.notes)
            )
        }
        daysArr.put(JSONObject().put("date", day.date).put("medicines", meds))
    }
    return JSONObject()
        .put("version", 1)
        .put("startDate", plan.startDate)
        .put("endDate", plan.endDate)
        .put("days", daysArr)
        .toString()
}

fun formatPrescriptionSummary(plan: StructuredPrescriptionPlan): String {
    val start = formatDisplayDate(plan.startDate)
    val end = formatDisplayDate(plan.endDate)
    val meds = plan.days
        .flatMap { it.medicines }
        .map { "${it.medicineName} ${it.dosage} в ${it.reminderTime}" }
        .distinct()
    val header = "Курс лечения: $start — $end"
    return if (meds.isEmpty()) header else "$header\n${meds.joinToString("\n") { "• $it" }}"
}

fun parseStructuredPlanJson(text: String): StructuredPrescriptionPlan? = runCatching {
    val root = JSONObject(text.trim())
    if (!root.has("days")) return@runCatching null
    val daysArr = root.getJSONArray("days")
    val days = buildList {
        for (i in 0 until daysArr.length()) {
            val dayObj = daysArr.getJSONObject(i)
            val date = dayObj.getString("date")
            val medsArr = dayObj.getJSONArray("medicines")
            val medicines = buildList {
                for (j in 0 until medsArr.length()) {
                    val m = medsArr.getJSONObject(j)
                    add(
                        PrescriptionMedicineEntry(
                            medicineName = m.optString("name"),
                            dosage = m.optString("dosage"),
                            reminderTime = m.optString("time", "08:00"),
                            notes = m.optString("notes")
                        )
                    )
                }
            }
            if (medicines.isNotEmpty()) add(PrescriptionDayPlan(date, medicines))
        }
    }
    if (days.isEmpty()) return@runCatching null
    StructuredPrescriptionPlan(
        startDate = root.optString("startDate", days.first().date),
        endDate = root.optString("endDate", days.last().date),
        days = days
    )
}.getOrNull()

fun buildPlansFromStructuredPlan(
    username: String,
    plan: StructuredPrescriptionPlan,
    courseId: Long
): List<TreatmentPlan> {
    val periodStart = plan.startDate
    val periodEnd = plan.endDate
    return plan.days.flatMap { day ->
        day.medicines.map { med ->
            TreatmentPlan(
                username = username,
                medicineName = med.medicineName,
                dosage = med.dosage,
                timesPerDay = 1,
                reminderTime = med.reminderTime,
                startDate = day.date,
                endDate = day.date,
                notes = buildString {
                    append("Период: $periodStart - $periodEnd. Автоплан по рецепту врача.")
                    if (med.notes.isNotBlank()) append(" ${med.notes.trim()}")
                },
                courseId = courseId
            )
        }
    }
}

fun formatStructuredPlanForPatientCard(text: String): String {
    val plan = parseStructuredPlanJson(text) ?: return text
    return formatPrescriptionSummary(plan)
}

data class DoctorChatFormattedMessage(
    val prescriptionText: String? = null,
    val planSummary: String? = null,
    val plainText: String
)

fun parseDoctorChatMessage(raw: String): DoctorChatFormattedMessage {
    val text = raw.trim()
    if (text.isBlank()) return DoctorChatFormattedMessage(plainText = "")

    val planMarkers = listOf("\n\nПлан лечения:\n", "\nПлан лечения:\n", "\n\nНовый план лечения:\n", "\nНовый план лечения:\n")
    var planPart: String? = null
    var beforePlan = text
    for (marker in planMarkers) {
        val idx = text.indexOf(marker, ignoreCase = true)
        if (idx >= 0) {
            beforePlan = text.substring(0, idx).trim()
            planPart = text.substring(idx + marker.length).trim()
            break
        }
    }

    var prescriptionText: String? = null
    val rxPrefixes = listOf("Рецепт:", "Рецепт：")
    val beforeLower = beforePlan.lowercase(Locale.getDefault())
    for (prefix in rxPrefixes) {
        if (beforeLower.startsWith(prefix.lowercase(Locale.getDefault()))) {
            prescriptionText = beforePlan.substring(prefix.length).trim()
            break
        }
    }

    val planSummary = planPart?.let { formatStructuredPlanForPatientCard(it) }?.takeIf { it.isNotBlank() }
    val plain = when {
        prescriptionText == null && planSummary == null -> text
        prescriptionText != null && planSummary == null -> prescriptionText
        prescriptionText == null && planSummary != null -> planSummary
        else -> ""
    }

    return DoctorChatFormattedMessage(
        prescriptionText = prescriptionText,
        planSummary = planSummary,
        plainText = plain
    )
}

fun countPlanDays(text: String): Int =
    parseStructuredPlanJson(text)?.days?.size ?: 0

fun countPlanMedicines(text: String): Int =
    parseStructuredPlanJson(text)?.days?.sumOf { it.medicines.size } ?: 0
