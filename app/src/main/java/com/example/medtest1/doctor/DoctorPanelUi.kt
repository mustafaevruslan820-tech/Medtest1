package com.example.medtest1.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.medtest1.network.DoctorProfile
import com.example.medtest1.ui.theme.LocalMedAppColors

@Composable
fun DoctorShiftHeroCard(
    onDuty: Boolean,
    shiftLoading: Boolean,
    shiftColor: Color,
    onToggleShift: () -> Unit,
    onEditProfile: () -> Unit,
    onExportPdf: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = app.cardOnHero.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            scheme.primary.copy(alpha = if (onDuty) 0.22f else 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (onDuty) "Смена открыта" else "Смена закрыта",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = shiftColor
                        )
                        Text(
                            if (onDuty) "Пациенты и коллеги видят вас на смене"
                            else "Начните смену, чтобы принимать пациентов",
                            style = MaterialTheme.typography.bodySmall,
                            color = app.onHeroMuted
                        )
                    }
                    if (shiftLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    } else {
                        Button(
                            onClick = onToggleShift,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (onDuty) scheme.tertiaryContainer else scheme.primary,
                                contentColor = if (onDuty) scheme.onTertiaryContainer else scheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (onDuty) "Закончить" else "Начать смену")
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditProfile,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(" Анкета", maxLines = 1)
                    }
                    if (onExportPdf != null) {
                        OutlinedButton(
                            onClick = onExportPdf,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("PDF смены", maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorPanelSectionGrid(
    selectedTab: Int,
    patientsCount: Int,
    completedCount: Int,
    colleaguesCount: Int,
    rejectionsCount: Int,
    peerUnreadTotal: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DoctorPanelSectionTile(
                modifier = Modifier.weight(1f),
                title = "Пациенты",
                count = patientsCount,
                icon = Icons.Filled.Person,
                selected = selectedTab == 0,
                badge = 0,
                onClick = { onSelectTab(0) }
            )
            DoctorPanelSectionTile(
                modifier = Modifier.weight(1f),
                title = "Завершённые",
                count = completedCount,
                icon = Icons.Filled.TaskAlt,
                selected = selectedTab == 1,
                badge = 0,
                onClick = { onSelectTab(1) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DoctorPanelSectionTile(
                modifier = Modifier.weight(1f),
                title = "Коллеги",
                count = colleaguesCount,
                icon = Icons.Filled.Groups,
                selected = selectedTab == 2,
                badge = peerUnreadTotal,
                onClick = { onSelectTab(2) }
            )
            DoctorPanelSectionTile(
                modifier = Modifier.weight(1f),
                title = "Отказы",
                count = rejectionsCount,
                icon = Icons.Filled.PersonOff,
                selected = selectedTab == 3,
                badge = 0,
                onClick = { onSelectTab(3) }
            )
        }
    }
}

@Composable
private fun DoctorPanelSectionTile(
    title: String,
    count: Int,
    icon: ImageVector,
    selected: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val accent = if (selected) scheme.primary else app.onHeroMuted
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(2.dp, scheme.primary, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) scheme.primary.copy(alpha = 0.14f) else app.cardOnHero.copy(alpha = 0.85f)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = app.onHero,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    count.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }
            if (badge > 0) {
                UnreadBadge(
                    count = badge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                )
            }
        }
    }
}

@Composable
fun UnreadBadge(count: Int, modifier: Modifier = Modifier) {
    if (count <= 0) return
    val label = if (count > 99) "99+" else count.toString()
    Box(
        modifier = modifier
            .size(if (count > 9) 22.dp else 18.dp)
            .clip(CircleShape)
            .background(Color(0xFFEF4444)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun ColleagueChatCard(
    doctor: DoctorProfile,
    unreadCount: Int,
    lastPreview: String?,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val specialty = formatDoctorSpecialtyLabel(doctor.specialty)
    val visual = specialtyVisual(doctor.specialty, scheme)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = app.cardOnHero.copy(alpha = 0.92f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DoctorAvatar(doctor = doctor, size = 56.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    doctor.fullName.ifBlank { doctor.username },
                    fontWeight = FontWeight.Bold,
                    color = app.onHero,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (specialty.isNotBlank()) {
                    Text(
                        specialty,
                        style = MaterialTheme.typography.labelMedium,
                        color = visual.accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (!lastPreview.isNullOrBlank()) {
                    Text(
                        lastPreview,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (unreadCount > 0) app.onHero else app.onHeroMuted,
                        fontWeight = if (unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box {
                Button(
                    onClick = onOpenChat,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(" Чат")
                }
                if (unreadCount > 0) {
                    UnreadBadge(
                        count = unreadCount,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-8).dp)
                    )
                }
            }
        }
    }
}
