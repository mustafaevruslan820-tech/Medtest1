package com.example.medtest1.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medtest1.network.BackendApi
import com.example.medtest1.network.DoctorPeerMessage
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
fun DoctorPeerChatScreen(
    modifier: Modifier = Modifier,
    tokenProvider: () -> String,
    peerDoctorId: Long,
    peerTitle: String,
    peerSpecialty: String,
    onBack: () -> Unit
) {
    val app = LocalMedAppColors.current
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf<List<DoctorPeerMessage>>(emptyList()) }
    var draft by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val specialtyLabel = formatDoctorSpecialtyLabel(peerSpecialty)

    LaunchedEffect(peerDoctorId) {
        while (true) {
            messages = BackendApi.getDoctorPeerMessages(tokenProvider(), peerDoctorId)
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
                title = {
                    Column {
                        Text(peerTitle)
                        if (specialtyLabel.isNotBlank()) {
                            Text(
                                specialtyLabel,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                color = app.onHeroMuted
                            )
                        }
                    }
                },
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
                    val align = if (msg.senderIsMe) Alignment.End else Alignment.Start
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = align
                    ) {
                        Surface(
                            color = if (msg.senderIsMe) SupportOutgoingBubble else SupportIncomingBubble,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = SupportChatText,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                        Text(
                            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.createdAt)),
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
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
                    placeholder = { Text("Сообщение коллеге…") },
                    maxLines = 4,
                    shape = RoundedCornerShape(16.dp)
                )
                IconButton(
                    onClick = {
                        val text = draft.trim()
                        if (text.isBlank() || sending) return@IconButton
                        sending = true
                        scope.launch {
                            val ok = BackendApi.sendDoctorPeerMessage(tokenProvider(), peerDoctorId, text)
                            if (ok) {
                                draft = ""
                                messages = BackendApi.getDoctorPeerMessages(tokenProvider(), peerDoctorId)
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
