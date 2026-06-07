package com.example.medtest1.doctor

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medtest1.network.BackendApi
import com.example.medtest1.ui.MedSubScreenLayout
import com.example.medtest1.ui.MedSurfaceCard
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@Composable
fun DoctorProfileFormScreen(
    modifier: Modifier = Modifier,
    tokenProvider: () -> String,
    onProfileSaved: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var specialty by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var photoBase64 by remember { mutableStateOf<String?>(null) }
    var photoPreview by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var saving by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bmp = BitmapFactory.decodeStream(stream) ?: return@runCatching
                val scaled = if (bmp.width > 256 || bmp.height > 256) {
                    val ratio = minOf(256f / bmp.width, 256f / bmp.height)
                    android.graphics.Bitmap.createScaledBitmap(
                        bmp,
                        (bmp.width * ratio).toInt().coerceAtLeast(1),
                        (bmp.height * ratio).toInt().coerceAtLeast(1),
                        true
                    )
                } else bmp
                photoPreview = scaled
                val out = ByteArrayOutputStream()
                scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 72, out)
                photoBase64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
            }
        }
    }

    LaunchedEffect(tokenProvider()) {
        loading = true
        val profile = runCatching { BackendApi.getMyDoctorProfile(tokenProvider()) }.getOrNull()
        if (profile != null) {
            specialty = profile.specialty
            fullName = profile.fullName
            experience = profile.experienceYears.takeIf { it > 0 }?.toString().orEmpty()
            education = profile.education
            bio = profile.bio
            photoBase64 = profile.photoBase64
            profile.photoBase64?.let { b64 ->
                runCatching {
                    val bytes = Base64.decode(b64, Base64.DEFAULT)
                    photoPreview = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }
        }
        loading = false
    }

    MedSubScreenLayout(
        title = "Анкета врача",
        subtitle = "Заполните профиль перед началом смены",
        onBack = onBack,
        modifier = modifier
    ) {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@MedSubScreenLayout
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(380)) + slideInVertically(tween(380)) { it / 8 }
        ) {
            MedSurfaceCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (photoPreview != null) {
                        Image(
                            bitmap = photoPreview!!.toComposeImageBitmap(),
                            contentDescription = "Фото врача",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Button(onClick = { photoPicker.launch("image/*") }) {
                        Text(if (photoPreview == null) "Добавить фото" else "Изменить фото")
                    }
                }
                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Специальность") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ФИО") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Стаж (лет)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text("Образование") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("О себе") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Button(
                    onClick = {
                        if (specialty.isBlank() || fullName.isBlank() || photoBase64.isNullOrBlank()) {
                            error = "Заполните специальность, ФИО и добавьте фото."
                            return@Button
                        }
                        saving = true
                        error = null
                        scope.launch {
                            val saved = runCatching {
                                BackendApi.updateMyDoctorProfile(
                                    token = tokenProvider(),
                                    specialty = specialty.trim(),
                                    fullName = fullName.trim(),
                                    experienceYears = experience.toIntOrNull() ?: 0,
                                    education = education.trim(),
                                    bio = bio.trim(),
                                    photoBase64 = photoBase64
                                )
                            }.getOrNull()
                            saving = false
                            when {
                                saved != null && saved.isProfileReady() -> onProfileSaved()
                                saved != null -> error = "Профиль сохранён не полностью. Добавьте фото."
                                else -> error = "Не удалось сохранить. Проверьте интернет и войдите снова."
                            }
                        }
                    },
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Сохранить анкету")
                    }
                }
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "Пациенты увидят эти данные при выборе врача.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
