package com.example.medtest1.doctor

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.medtest1.network.DoctorProfile
import com.example.medtest1.ui.MedProfileField
import com.example.medtest1.ui.theme.LocalMedAppColors

@Composable
fun DoctorProfileDialog(
    doctor: DoctorProfile,
    onDismiss: () -> Unit,
    onSelect: (() -> Unit)? = null,
    selectInProgress: Boolean = false
) {
    val scheme = MaterialTheme.colorScheme
    val app = LocalMedAppColors.current
    val specialtyLabel = formatDoctorSpecialtyLabel(doctor.specialty).ifBlank { "Врач" }
    val specialtyStyle = specialtyVisual(doctor.specialty, scheme)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = scheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                app.cardOnHero.copy(alpha = 0.35f),
                                scheme.surface
                            )
                        )
                    )
            ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DoctorAvatar(doctor = doctor, size = 88.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            doctor.fullName.ifBlank { doctor.username },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = specialtyStyle.container.copy(alpha = 0.65f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    specialtyStyle.icon,
                                    contentDescription = null,
                                    tint = specialtyStyle.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    specialtyLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = app.onHero,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (doctor.onDuty) {
                            Text(
                                "● На смене",
                                style = MaterialTheme.typography.labelMedium,
                                color = app.accentRing,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = app.cardOnHero.copy(alpha = 0.55f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (doctor.experienceYears > 0) {
                            MedProfileField(
                                "Стаж",
                                "${doctor.experienceYears} лет",
                                Icons.Filled.WorkHistory,
                                scheme
                            )
                        }
                        if (doctor.education.isNotBlank()) {
                            MedProfileField(
                                "Образование",
                                doctor.education,
                                Icons.Filled.School,
                                scheme
                            )
                        }
                        if (doctor.bio.isNotBlank()) {
                            MedProfileField(
                                "О враче",
                                doctor.bio,
                                Icons.Filled.Person,
                                scheme,
                                showDivider = false
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.surfaceVariant,
                            contentColor = scheme.onSurfaceVariant
                        )
                    ) { Text("Закрыть") }
                    if (onSelect != null) {
                        Button(
                            onClick = onSelect,
                            enabled = !selectInProgress,
                            modifier = Modifier.weight(1f)
                        ) { Text("Выбрать врача") }
                    }
                }
            }
            }
        }
    }
}

@Composable
fun DoctorAvatar(doctor: DoctorProfile, size: androidx.compose.ui.unit.Dp) {
    val scheme = MaterialTheme.colorScheme
    doctor.photoBase64?.let { b64 ->
        runCatching {
            val bytes = Base64.decode(b64, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bmp != null) {
                Image(
                    bitmap = bmp.toComposeImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                return
            }
        }
    }
    Surface(
        shape = CircleShape,
        color = scheme.primaryContainer,
        modifier = Modifier.size(size)
    ) {
        androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.LocalHospital,
                contentDescription = null,
                tint = scheme.onPrimaryContainer,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}

fun DoctorProfile.isProfileReady(): Boolean =
    profileComplete ||
        (specialty.isNotBlank() && fullName.isNotBlank() && !photoBase64.isNullOrBlank())
