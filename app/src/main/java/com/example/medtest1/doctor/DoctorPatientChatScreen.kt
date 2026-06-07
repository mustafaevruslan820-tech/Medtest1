package com.example.medtest1.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medtest1.network.BackendApi
import com.example.medtest1.network.DoctorMessage
import com.example.medtest1.support.SupportChatGradient
import com.example.medtest1.support.SupportChatText
import com.example.medtest1.support.SupportIncomingBubble
import com.example.medtest1.support.SupportOutgoingBubble
import com.example.medtest1.ui.theme.LocalMedAppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPatientChatScreen(
    modifier: Modifier = Modifier,
    tokenProvider: () -> String,
    assignmentId: Long,
    title: String,
    viewerIsDoctor: Boolean,
    onBack: () -> Unit
) {
    val app = LocalMedAppColors.current
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf<List<DoctorMessage>>(emptyList()) }
    var draft by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    DisposableEffect(assignmentId) {
        DoctorSession.isInDoctorChat = true
        DoctorSession.activeAssignmentId = assignmentId
        onDispose {
            DoctorSession.isInDoctorChat = false
        }
    }

    LaunchedEffect(assignmentId) {
        while (true) {
            messages = BackendApi.getDoctorMessages(tokenProvider(), assignmentId)
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.lastIndex)
            }
            delay(2000)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = app.onHero)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = app.onHero
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SupportChatGradient)
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    DoctorChatMessageBubble(
                        message = msg,
                        viewerIsDoctor = viewerIsDoctor
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Сообщение…") },
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp)
                )
                IconButton(
                    onClick = {
                        val text = draft.trim()
                        if (text.isBlank() || sending) return@IconButton
                        sending = true
                        scope.launch {
                            val ok = BackendApi.sendDoctorMessage(tokenProvider(), assignmentId, text)
                            if (ok) {
                                draft = ""
                                messages = BackendApi.getDoctorMessages(tokenProvider(), assignmentId)
                                if (messages.isNotEmpty()) {
                                    listState.animateScrollToItem(messages.lastIndex)
                                }
                            }
                            sending = false
                        }
                    },
                    enabled = draft.isNotBlank() && !sending
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить", tint = app.onHero)
                }
            }
        }
    }
}

@Composable
private fun DoctorChatMessageBubble(
    message: DoctorMessage,
    viewerIsDoctor: Boolean
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val isMine = if (viewerIsDoctor) message.sender == "doctor" else message.sender == "patient"
    val importantKind = classifyImportantDoctorMessage(message.text, !viewerIsDoctor)
    val bubbleColor = if (isMine) SupportOutgoingBubble else SupportIncomingBubble
    val align = if (isMine) Alignment.End else Alignment.Start
    val accent = when (importantKind) {
        ImportantDoctorMessageKind.Prescription -> Color(0xFFFFD54F)
        ImportantDoctorMessageKind.Plan -> Color(0xFF4DD0E1)
        null -> Color.Transparent
    }
    val formatted = remember(message.text) { parseDoctorChatMessage(message.text) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        if (importantKind != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
            ) {
                Icon(
                    imageVector = if (importantKind == ImportantDoctorMessageKind.Prescription) {
                        Icons.Filled.Medication
                    } else {
                        Icons.Filled.Star
                    },
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (importantKind == ImportantDoctorMessageKind.Prescription) {
                        "Важно: рецепт"
                    } else {
                        "Важно: план лечения"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
            modifier = if (importantKind != null) {
                Modifier.border(2.dp, accent, RoundedCornerShape(16.dp))
            } else {
                Modifier
            }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formatted.prescriptionText?.let { rx ->
                    Text(
                        text = rx,
                        color = SupportChatText,
                        fontWeight = if (importantKind != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                formatted.planSummary?.let { plan ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = scheme.surface.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "План лечения",
                                style = MaterialTheme.typography.labelMedium,
                                color = accent.takeIf { it != Color.Transparent } ?: scheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            plan.lines().forEach { line ->
                                Text(
                                    text = line,
                                    color = SupportChatText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                if (formatted.prescriptionText == null && formatted.planSummary == null) {
                    Text(
                        text = formatted.plainText.ifBlank { message.text },
                        color = SupportChatText,
                        fontWeight = if (importantKind != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.createdAt)),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}
