@file:Suppress(
    "UNUSED_PARAMETER",
    "UNUSED_VARIABLE",
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE",
    "RedundantQualifierName"
)

package com.example.medtest1

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import coil.compose.AsyncImage
import com.airbnb.lottie.LottieComposition
import com.example.medtest1.BuildConfig
import com.example.medtest1.ui.AssetImage
import com.example.medtest1.ui.WelcomeLogo
import com.example.medtest1.ui.assetImageModel
import com.example.medtest1.data.AppDatabaseHelper
import com.example.medtest1.data.SavedMedicine
import com.example.medtest1.data.ScannedMedicineCard
import com.example.medtest1.data.TreatmentPlan
import com.example.medtest1.data.UserProfile
import com.example.medtest1.data.WellbeingEntry
import com.example.medtest1.network.BackendApi
import com.example.medtest1.network.SupportMessage
import com.example.medtest1.doctor.DoctorSession
import com.example.medtest1.notifications.DoctorNotifier
import com.example.medtest1.notifications.SupportChatNotifier
import com.example.medtest1.support.SupportAdminScreen
import com.example.medtest1.support.SupportChatScreen
import com.example.medtest1.notifications.SupportAdminNotifier
import com.example.medtest1.doctor.AdminPanelScreen
import com.example.medtest1.doctor.DoctorPanelScreen
import com.example.medtest1.doctor.DoctorPatientChatScreen
import com.example.medtest1.doctor.DoctorPeerChatScreen
import com.example.medtest1.doctor.DoctorProfileFormScreen
import com.example.medtest1.doctor.isProfileReady
import com.example.medtest1.doctor.DoctorTreatmentSection
import com.example.medtest1.doctor.TreatmentReportOptionsDialog
import com.example.medtest1.network.DoctorAssignment
import com.example.medtest1.support.SupportAgentInfo
import com.example.medtest1.support.SupportChatSession
import com.example.medtest1.ui.HomeExpandableFolder
import com.example.medtest1.ui.HomeBottomNavBar
import com.example.medtest1.ui.MedClinicalInfoDialog
import com.example.medtest1.ui.MedClinicalInfoRow
import com.example.medtest1.ui.MedMenuCard
import com.example.medtest1.ui.MedMenuRow
import com.example.medtest1.ui.MedProfileField
import com.example.medtest1.ui.MedSosHighlightCard
import com.example.medtest1.ui.MedProfileHeader
import com.example.medtest1.ui.MedSectionLabel
import com.example.medtest1.ui.MedSubScreenLayout
import com.example.medtest1.ui.MedSurfaceCard
import com.example.medtest1.ui.MedScanIconButton
import androidx.compose.material3.FilledTonalButton
import com.google.firebase.messaging.FirebaseMessaging
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.tasks.await
import com.example.medtest1.notifications.TreatmentReminderReceiver
import com.example.medtest1.reports.exportTreatmentPlansToPdf
import com.example.medtest1.ui.theme.LocalMedAppColors
import com.example.medtest1.ui.theme.MedAppColors
import com.example.medtest1.ui.theme.UiMetrics
import com.example.medtest1.ui.theme.Medtest1Theme
import com.example.medtest1.ui.theme.rememberUiMetrics
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databaseHelper = AppDatabaseHelper(this)
        val openSupportChat = intent?.getBooleanExtra(EXTRA_OPEN_SUPPORT_CHAT, false) == true
        val openSupportAdmin = intent?.getBooleanExtra(EXTRA_OPEN_SUPPORT_ADMIN, false) == true
        val openDoctorPanel = intent?.getBooleanExtra(EXTRA_OPEN_DOCTOR_PANEL, false) == true
        val openDoctorChat = intent?.getBooleanExtra(EXTRA_OPEN_DOCTOR_CHAT, false) == true
        val openAssignmentId = intent?.getLongExtra(EXTRA_ASSIGNMENT_ID, 0L) ?: 0L
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themePrefs = remember {
                context.getSharedPreferences("medtest_session", Context.MODE_PRIVATE)
            }
            var darkTheme by remember {
                mutableStateOf(themePrefs.getBoolean("pref_dark_theme", true))
            }
            Medtest1Theme(darkTheme = darkTheme) {
                MedApp(
                    databaseHelper = databaseHelper,
                    darkTheme = darkTheme,
                    initialOpenSupportChat = openSupportChat,
                    initialOpenSupportAdmin = openSupportAdmin,
                    initialOpenDoctorPanel = openDoctorPanel,
                    initialOpenDoctorChat = openDoctorChat,
                    initialAssignmentId = openAssignmentId,
                    onDarkThemeChange = { d ->
                        darkTheme = d
                        themePrefs.edit { putBoolean("pref_dark_theme", d) }
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_OPEN_SUPPORT_CHAT = "extra_open_support_chat"
        const val EXTRA_OPEN_SUPPORT_ADMIN = "extra_open_support_admin"
        const val EXTRA_OPEN_DOCTOR_PANEL = "extra_open_doctor_panel"
        const val EXTRA_OPEN_DOCTOR_CHAT = "extra_open_doctor_chat"
        const val EXTRA_ASSIGNMENT_ID = "extra_assignment_id"
    }
}

data class DayMedicationEntry(
    val medicineName: String = "",
    val dosage: String = "",
    val reminderTime: String = "08:00",
    val notes: String = ""
)

private data class ScannedMedicineCodeInfo(
    val rawValue: String,
    val gtin: String?,
    val expiryDate: String?,
    val batch: String?,
    val serial: String?
)

private data class HomeCompletedCourse(
    val folderKey: Long,
    val title: String,
    val plans: List<TreatmentPlan>
)

private data class ScannedMedicineCandidate(
    val id: Long = 0L,
    val name: String,
    val barcode: String,
    val infoUrl: String,
    val scannedAtMillis: Long = 0L
)

private enum class HomeSnackbarKind {
    Added,
    Renamed,
    Info
}

private fun clearRestoredSessionIfNeeded(context: Context, sessionPrefs: android.content.SharedPreferences) {
    val installMarker = File(context.noBackupFilesDir, "install.marker")
    if (!installMarker.exists()) {
        // noBackupFilesDir is not restored from Auto Backup, so missing marker means
        // this is a fresh install/restore boundary. Drop potentially restored auth state.
        sessionPrefs.edit {
            remove("active_user")
            remove("auth_token")
            remove("support_admin_key")
        }
        installMarker.parentFile?.mkdirs()
        installMarker.writeText("ok")
    }
}

/**
 * Сброс пароля идёт через Firebase;
 */
private suspend fun syncFirebaseAuthUser(context: Context, email: String, password: String) {
    val em = email.trim().lowercase()
    if (em.isBlank()) return
    val firebaseApp = runCatching {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        } else {
            FirebaseApp.getInstance()
        }
    }.getOrNull() ?: return

    val auth = FirebaseAuth.getInstance(firebaseApp)
    try {
        try {
            auth.createUserWithEmailAndPassword(em, password).await()
        } catch (e: FirebaseAuthException) {
            if (e.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                auth.signInWithEmailAndPassword(em, password).await()
            } else {
                throw e
            }
        }
    } catch (_: Exception) {
        // Не мешаем входу/регистрации по backend
    } finally {
        runCatching { auth.signOut() }
    }
}

private enum class ScannedMedicineSortOption {
    ByDate,
    ByAlphabet
}

private fun formatScannedMedicineDateTime(millis: Long): String {
    if (millis <= 0L) return "Не указана"
    val fmt = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
    return fmt.format(Date(millis))
}

private enum class AssistantTourSpotlight {
    None,
    HomeUmnik,
    HomeStop,
    HomeDiaryAction,
    HomeReport,
    HomeNavTreatment,
    HomeNavDiary,
    HomeNavNext,
    HomeScanner,
    HomeProfile,
    TreatmentPeriodCard,
    TreatmentTabs,
    DiaryDayStrip,
    ProfileInfoCard,
    ProfileMedicalPassport,
    ProfileSos,
    SettingsSoundCard
}

private data class AssistantTourStep(
    val title: String,
    val body: String,
    val spotlight: AssistantTourSpotlight,
    val targetScreen: Screen,
    /** Второстепенные кнопки и действия — только текст, без подсветки */
    val extraText: String = ""
)

private fun Modifier.assistantTourTarget(
    active: Boolean,
    pulseScale: Float,
    shape: Shape,
    borderColor: Color,
    onBounds: ((Rect) -> Unit)?
): Modifier {
    var m = scale(if (active) pulseScale else 1f)
    m = m.then(if (active) Modifier.border(2.dp, borderColor, shape) else Modifier)
    return if (active && onBounds != null) {
        m.onGloballyPositioned { coords -> onBounds(coords.boundsInRoot()) }
    } else {
        m
    }
}

private val assistantTourAssetFiles = arrayOf(
    "assistant_umnik_tour_home.png",
    "assistant_step_1.png",
    "assistant_step_2.png",
    "assistant_step_3.png",
    "assistant_step_4.png"
)

private fun assistantTourAsset(stepIndex: Int): String {
    if (assistantTourAssetFiles.isEmpty()) return "assistant_character.png"
    val idx = stepIndex % assistantTourAssetFiles.size
    return assistantTourAssetFiles[idx]
}

@Composable
private fun AnimatedAssistantIconButton(
    onClick: () -> Unit,
    imageAsset: String,
    imageModel: Any? = null,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    contentDescription: String = "Умник"
) {
    val app = LocalMedAppColors.current
    val transition = rememberInfiniteTransition(label = "assistant-icon")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.09f,
        animationSpec = infiniteRepeatable(
            animation = tween(820, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "assistant-icon-scale"
    )
    val bob by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "assistant-icon-bob"
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(680, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "assistant-icon-ring"
    )
    Box(
        modifier = modifier
            .size(size + 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = bob
            }
            .clip(CircleShape)
            .border(2.dp, app.accentRing.copy(alpha = ringAlpha), CircleShape)
            .background(app.accentRingSoft.copy(alpha = 0.55f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                model = assetImageModel(imageAsset),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun AnimatedScannerCameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBusy: Boolean = false
) {
    val app = LocalMedAppColors.current
    val transition = rememberInfiniteTransition(label = "scanner-icon")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(760, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner-scale"
    )
    val glow by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner-glow"
    )

    Box(
        modifier = modifier
            .size(86.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .border(3.dp, app.accentRing.copy(alpha = glow), CircleShape)
            .background(if (isBusy) app.scannerBusy else app.scannerIdle, CircleShape)
            .clickable(enabled = !isBusy, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_scanner_camera),
            contentDescription = if (isBusy) "Сканирование штрихкода" else "Сканировать штрихкод",
            tint = Color.Unspecified,
            modifier = Modifier.size(58.dp)
        )
    }
}

private suspend fun registerSupportFcmToken(sessionPrefs: android.content.SharedPreferences) {
    val authToken = sessionPrefs.getString("auth_token", "").orEmpty()
    if (authToken.isBlank()) return
    val fcmToken = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull() ?: return
    val saved = sessionPrefs.getString("fcm_token_saved", "").orEmpty()
    if (saved == fcmToken) return
    val ok = runCatching { BackendApi.registerFcmToken(authToken, fcmToken) }.getOrDefault(false)
    if (ok) {
        sessionPrefs.edit { putString("fcm_token_saved", fcmToken) }
    }
}

private fun isAdminSession(username: String, role: String): Boolean =
    role.equals("admin", ignoreCase = true) || username.equals("Admin", ignoreCase = true)

private fun markAdminMessagesNotified(
    sessionPrefs: android.content.SharedPreferences,
    messages: List<SupportMessage>
) {
    val maxAdminId = messages.filter { it.sender == "admin" }.maxOfOrNull { it.id } ?: return
    val prev = sessionPrefs.getLong("support_last_notified_admin_id", 0L)
    if (maxAdminId > prev) {
        sessionPrefs.edit { putLong("support_last_notified_admin_id", maxAdminId) }
    }
}

@Composable
private fun MedApp(
    databaseHelper: AppDatabaseHelper,
    darkTheme: Boolean,
    initialOpenSupportChat: Boolean = false,
    initialOpenSupportAdmin: Boolean = false,
    initialOpenDoctorPanel: Boolean = false,
    initialOpenDoctorChat: Boolean = false,
    initialAssignmentId: Long = 0L,
    onDarkThemeChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf(Screen.Onboarding) }
    var showStartupLoading by remember { mutableStateOf(true) }
    var activeUser by remember { mutableStateOf("") }
    var prefillProfile by remember { mutableStateOf<UserProfile?>(null) }
    val treatmentPlans = remember { mutableStateListOf<TreatmentPlan>() }
    val wellbeingEntries = remember { mutableStateMapOf<String, WellbeingEntry>() }
    val savedMedicines = remember { mutableStateListOf<SavedMedicine>() }
    val persistedScannedMedicines = remember { mutableStateListOf<ScannedMedicineCandidate>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionPrefs = remember(context) {
        context.getSharedPreferences("medtest_session", Context.MODE_PRIVATE)
    }
    var pendingOpenSupportChat by remember { mutableStateOf(initialOpenSupportChat) }
    var pendingOpenSupportAdmin by remember { mutableStateOf(initialOpenSupportAdmin) }
    var pendingOpenDoctorPanel by remember { mutableStateOf(initialOpenDoctorPanel) }
    var pendingOpenDoctorChat by remember { mutableStateOf(initialOpenDoctorChat) }
    var pendingAssignmentId by remember { mutableStateOf(initialAssignmentId) }
    var userRole by remember { mutableStateOf("patient") }
    var patientAssignment by remember { mutableStateOf<DoctorAssignment?>(null) }
    var doctorChatAssignmentId by remember { mutableStateOf(0L) }
    var doctorChatTitle by remember { mutableStateOf("") }
    var doctorChatAsDoctor by remember { mutableStateOf(false) }
    var peerChatDoctorId by remember { mutableStateOf(0L) }
    var peerChatTitle by remember { mutableStateOf("") }
    var peerChatSpecialty by remember { mutableStateOf("") }
    val activity = context as? Activity

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    var showAssistantTour by remember { mutableStateOf(false) }
    var assistantTourStep by remember { mutableIntStateOf(0) }
    var assistantSpotlightBounds by remember { mutableStateOf<Rect?>(null) }
    val assistantTourSteps = remember {
        listOf(
            AssistantTourStep(
                title = "Умник",
                body = "Помощник и этот тур — нажмите снова в любой момент.",
                spotlight = AssistantTourSpotlight.HomeUmnik,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Стоп",
                body = "Прервать текущее лечение (с подтверждением).",
                spotlight = AssistantTourSpotlight.HomeStop,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Дневник",
                body = "Быстрый переход к дневнику самочувствия.",
                spotlight = AssistantTourSpotlight.HomeDiaryAction,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Отчёт",
                body = "Экспорт PDF о лечении, если курс выполнен.",
                spotlight = AssistantTourSpotlight.HomeReport,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Лечение",
                body = "Нижнее меню: открыть планировщик приёма.",
                spotlight = AssistantTourSpotlight.HomeNavTreatment,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Дневник внизу",
                body = "То же, что верхняя кнопка «Дневник».",
                spotlight = AssistantTourSpotlight.HomeNavDiary,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Далее",
                body = "Ближайший приём по расписанию.",
                spotlight = AssistantTourSpotlight.HomeNavNext,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Сканер",
                body = "Штрихкод упаковки — добавить в список сканирований.",
                spotlight = AssistantTourSpotlight.HomeScanner,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Профиль",
                body = "Личные данные, медпаспорт, SOS и настройки.",
                spotlight = AssistantTourSpotlight.HomeProfile,
                targetScreen = Screen.Home
            ),
            AssistantTourStep(
                title = "Ещё на главной",
                body = "Список можно потянуть вниз для обновления.",
                spotlight = AssistantTourSpotlight.None,
                targetScreen = Screen.Home,
                extraText = "Блок «Сканированные таблетки»: сортировка, «Перейти» к справке, «Изменить» название, «Удалить» запись — без отдельной подсветки."
            ),
            AssistantTourStep(
                title = "Период курса",
                body = "Даты начала и конца, затем заполнение по дням.",
                spotlight = AssistantTourSpotlight.TreatmentPeriodCard,
                targetScreen = Screen.Treatment
            ),
            AssistantTourStep(
                title = "Вкладки расписания",
                body = "Сегодня, скоро или уже завершённые приёмы.",
                spotlight = AssistantTourSpotlight.TreatmentTabs,
                targetScreen = Screen.Treatment
            ),
            AssistantTourStep(
                title = "Окно дня и записи",
                body = "Ниже — сохранённое расписание.",
                spotlight = AssistantTourSpotlight.None,
                targetScreen = Screen.Treatment,
                extraText = "Окно по дням: название, доза, время, заметки, «+ таблетка», сохранение дня. В списке: свайп и «Удалить запись», стрелки если несколько приёмов в день. Кнопка «Назад на главный» внизу."
            ),
            AssistantTourStep(
                title = "Дни в дневнике",
                body = "Выберите день, чтобы открыть самочувствие.",
                spotlight = AssistantTourSpotlight.DiaryDayStrip,
                targetScreen = Screen.WellbeingDiary
            ),
            AssistantTourStep(
                title = "Запись самочувствия",
                body = "Оценка и комментарий сохраняются в карточке дня.",
                spotlight = AssistantTourSpotlight.None,
                targetScreen = Screen.WellbeingDiary,
                extraText = "В окне: «Отлично» / «Хорошо» / «Плохо», поле комментария, «Сохранить» или «Отмена». Внизу экрана — «Назад на главный»."
            ),
            AssistantTourStep(
                title = "Ваши данные",
                body = "Фото, логин и контакты в одной карточке.",
                spotlight = AssistantTourSpotlight.ProfileInfoCard,
                targetScreen = Screen.PersonalProfile
            ),
            AssistantTourStep(
                title = "Медицинский паспорт",
                body = "Краткая медицинская сводка для справки.",
                spotlight = AssistantTourSpotlight.ProfileMedicalPassport,
                targetScreen = Screen.PersonalProfile
            ),
            AssistantTourStep(
                title = "SOS",
                body = "Экстренная карточка с важными сведениями.",
                spotlight = AssistantTourSpotlight.ProfileSos,
                targetScreen = Screen.PersonalProfile
            ),
            AssistantTourStep(
                title = "Профиль — остальное",
                body = "Дополнительные действия без подсветки.",
                spotlight = AssistantTourSpotlight.None,
                targetScreen = Screen.PersonalProfile,
                extraText = "В разделе «Разделы»: настройки, редактирование анкеты. Стрелка «Назад» вверху — на главную."
            ),
            AssistantTourStep(
                title = "Звук напоминаний",
                body = "Системный сигнал или свой файл для уведомлений.",
                spotlight = AssistantTourSpotlight.SettingsSoundCard,
                targetScreen = Screen.Settings
            ),
            AssistantTourStep(
                title = "Настройки — остальное",
                body = "Действия ниже блока звука.",
                spotlight = AssistantTourSpotlight.None,
                targetScreen = Screen.Settings,
                extraText = "«Сохранить настройки», «Выйти из аккаунта», «Удалить аккаунт», «Назад» — выполняют соответствующие действия с подтверждением там, где нужно."
            )
        )
    }
    val assistantSpotlight = assistantTourSteps.getOrNull(assistantTourStep)?.spotlight
        ?: AssistantTourSpotlight.None

    fun navigateAfterAuth(role: String, hasLocalProfile: Boolean) {
        val effectiveRole = when {
            role.equals("admin", ignoreCase = true) -> "admin"
            activeUser.equals("Admin", ignoreCase = true) -> "admin"
            else -> role
        }
        userRole = effectiveRole
        sessionPrefs.edit { putString("user_role", effectiveRole) }
        when (effectiveRole) {
            "admin" -> currentScreen = Screen.Home
            "doctor" -> {
                scope.launch {
                    val token = sessionPrefs.getString("auth_token", "").orEmpty()
                    val profile = if (token.isNotBlank()) {
                        runCatching { BackendApi.getMyDoctorProfile(token) }.getOrNull()
                    } else {
                        null
                    }
                    val hadCompleteProfile = sessionPrefs.getBoolean("doctor_profile_complete", false)
                    currentScreen = when {
                        profile?.isProfileReady() == true -> {
                            sessionPrefs.edit { putBoolean("doctor_profile_complete", true) }
                            Screen.DoctorPanel
                        }
                        profile == null && hadCompleteProfile -> Screen.DoctorPanel
                        else -> Screen.DoctorProfileForm
                    }
                }
            }
            else -> currentScreen = if (hasLocalProfile) Screen.Home else Screen.Profile
        }
    }

    fun refreshHomeData(username: String) {
        if (username.isBlank()) return
        prefillProfile = databaseHelper.getProfile(username)
        treatmentPlans.clear()
        treatmentPlans.addAll(databaseHelper.getTreatmentPlans(username))
        wellbeingEntries.clear()
        wellbeingEntries.putAll(databaseHelper.getWellbeingEntries(username))
        savedMedicines.clear()
        savedMedicines.addAll(databaseHelper.getSavedMedicines(username))
        persistedScannedMedicines.clear()
        persistedScannedMedicines.addAll(databaseHelper.getScannedMedicineCards(username).map {
            ScannedMedicineCandidate(
                id = it.id,
                name = it.name,
                barcode = it.barcode,
                infoUrl = it.infoUrl,
                scannedAtMillis = it.updatedAt
            )
        })
    }

    LaunchedEffect(activeUser) {
        if (activeUser.isNotBlank()) {
            refreshHomeData(activeUser)
            registerSupportFcmToken(sessionPrefs)
            val token = sessionPrefs.getString("auth_token", "").orEmpty()
            if (token.isNotBlank()) {
                val me = runCatching { BackendApi.fetchMe(token) }.getOrNull()
                if (me?.ok == true && !me.role.isNullOrBlank()) {
                    val role = if (isAdminSession(activeUser, me.role)) "admin" else me.role
                    userRole = role
                    sessionPrefs.edit { putString("user_role", role) }
                } else if (activeUser.equals("Admin", ignoreCase = true)) {
                    userRole = "admin"
                    sessionPrefs.edit { putString("user_role", "admin") }
                }
                if (userRole == "patient") {
                    patientAssignment = runCatching { BackendApi.getPatientAssignment(token) }.getOrNull()
                }
            }
        } else {
            savedMedicines.clear()
            persistedScannedMedicines.clear()
        }
    }

    LaunchedEffect(activeUser, pendingOpenSupportChat) {
        if (activeUser.isNotBlank() && pendingOpenSupportChat) {
            currentScreen = Screen.SupportChat
            pendingOpenSupportChat = false
        }
    }

    LaunchedEffect(activeUser, pendingOpenSupportAdmin) {
        if (activeUser.isNotBlank() && pendingOpenSupportAdmin) {
            currentScreen = Screen.SupportAdmin
            pendingOpenSupportAdmin = false
        }
    }

    LaunchedEffect(activeUser, pendingOpenDoctorPanel) {
        if (activeUser.isNotBlank() && pendingOpenDoctorPanel) {
            currentScreen = Screen.DoctorPanel
            pendingOpenDoctorPanel = false
        }
    }

    LaunchedEffect(activeUser, pendingOpenDoctorChat, pendingAssignmentId) {
        if (activeUser.isNotBlank() && pendingOpenDoctorChat && pendingAssignmentId > 0L) {
            doctorChatAssignmentId = pendingAssignmentId
            doctorChatTitle = if (userRole == "doctor") "Чат с пациентом" else "Чат с врачом"
            doctorChatAsDoctor = userRole == "doctor"
            currentScreen = Screen.DoctorPatientChat
            pendingOpenDoctorChat = false
            pendingAssignmentId = 0L
        }
    }

    LaunchedEffect(activeUser) {
        if (activeUser.isBlank()) return@LaunchedEffect
        registerSupportFcmToken(sessionPrefs)
        while (activeUser.isNotBlank()) {
            delay(120_000)
            registerSupportFcmToken(sessionPrefs)
        }
    }

    LaunchedEffect(activeUser, userRole, patientAssignment?.id, currentScreen) {
        if (activeUser.isBlank()) return@LaunchedEffect
        DoctorNotifier.ensureChannel(context)
        while (activeUser.isNotBlank()) {
            val token = sessionPrefs.getString("auth_token", "").orEmpty()
            if (token.isNotBlank()) {
                when (userRole) {
                    "patient" -> {
                        val assignmentId = patientAssignment?.id ?: 0L
                        if (assignmentId > 0L) {
                            val inChat = currentScreen == Screen.DoctorPatientChat && DoctorSession.isInDoctorChat
                            val detail = runCatching {
                                BackendApi.getAssignmentDetail(token, assignmentId)
                            }.getOrDefault(
                                com.example.medtest1.network.AssignmentDetail(
                                    null, emptyList(), emptyList(), emptyList()
                                )
                            )
                            val prescriptions = detail.prescriptions
                            val reports = detail.reports
                            val newestRx = prescriptions.maxOfOrNull { it.createdAt } ?: 0L
                            val lastRx = sessionPrefs.getLong("care_last_rx_$assignmentId", 0L)
                            if (!inChat && newestRx > lastRx) {
                                DoctorNotifier.show(
                                    context,
                                    "Рецепт от врача",
                                    "Врач выдал рецепт и план лечения",
                                    assignmentId = assignmentId
                                )
                                sessionPrefs.edit { putLong("care_last_rx_$assignmentId", newestRx) }
                            }
                            val concluded = reports.filter { it.doctorSignedAt != null }
                                .maxByOrNull { it.doctorSignedAt ?: 0L }
                            val lastConclusion = sessionPrefs.getLong("care_last_conclusion_$assignmentId", 0L)
                            val signedAt = concluded?.doctorSignedAt ?: 0L
                            if (!inChat && signedAt > lastConclusion) {
                                val title = "Лечение завершено"
                                DoctorNotifier.show(
                                    context,
                                    title,
                                    concluded?.doctorConclusion ?: "Врач оставил заключение",
                                    assignmentId = assignmentId
                                )
                                sessionPrefs.edit { putLong("care_last_conclusion_$assignmentId", signedAt) }
                            }
                            val messages = runCatching {
                                BackendApi.getDoctorMessages(token, assignmentId)
                            }.getOrDefault(emptyList())
                            val lastDoctorMsg = messages.filter { it.sender == "doctor" }
                                .maxByOrNull { it.id }
                            val lastNotifiedMsg = sessionPrefs.getLong("care_last_doctor_msg_$assignmentId", 0L)
                            if (!inChat && lastDoctorMsg != null && lastDoctorMsg.id > lastNotifiedMsg &&
                                !lastDoctorMsg.text.startsWith("Рецепт:")
                            ) {
                                DoctorNotifier.show(
                                    context,
                                    "Сообщение от врача",
                                    lastDoctorMsg.text.take(120),
                                    assignmentId = assignmentId
                                )
                                sessionPrefs.edit { putLong("care_last_doctor_msg_$assignmentId", lastDoctorMsg.id) }
                            }
                        }
                    }
                    "doctor" -> {
                        val inChat = currentScreen == Screen.DoctorPatientChat && DoctorSession.isInDoctorChat
                        val inPanel = currentScreen == Screen.DoctorPanel
                        val assignments = runCatching {
                            BackendApi.getDoctorAssignments(token)
                        }.getOrDefault(emptyList())
                        val newestAssignment = assignments.maxByOrNull { it.assignedAt }
                        val lastAssignmentAt = sessionPrefs.getLong("care_last_assignment_at", 0L)
                        if (!inPanel && newestAssignment != null && newestAssignment.assignedAt > lastAssignmentAt) {
                            DoctorNotifier.show(
                                context,
                                "Новый пациент",
                                "${newestAssignment.patientUsername} выбрал вас",
                                assignmentId = newestAssignment.id,
                                openDoctorPanel = true
                            )
                            sessionPrefs.edit { putLong("care_last_assignment_at", newestAssignment.assignedAt) }
                        }
                        assignments.forEach { assignment ->
                            val detail = runCatching {
                                BackendApi.getAssignmentDetail(token, assignment.id)
                            }.getOrDefault(
                                com.example.medtest1.network.AssignmentDetail(
                                    null, emptyList(), emptyList(), emptyList()
                                )
                            )
                            val reports = detail.reports
                            val pending = reports.firstOrNull { it.status == "pending" }
                            val lastReport = sessionPrefs.getLong("care_last_report_${assignment.id}", 0L)
                            if (!inPanel && pending != null && pending.createdAt > lastReport) {
                                DoctorNotifier.show(
                                    context,
                                    "Отчёт по лечению",
                                    "${assignment.patientUsername} отправил отчёт",
                                    assignmentId = assignment.id,
                                    openDoctorPanel = true
                                )
                                sessionPrefs.edit { putLong("care_last_report_${assignment.id}", pending.createdAt) }
                            }
                            if (!inChat || DoctorSession.activeAssignmentId != assignment.id) {
                                val messages = runCatching {
                                    BackendApi.getDoctorMessages(token, assignment.id)
                                }.getOrDefault(emptyList())
                                val lastPatientMsg = messages.filter { it.sender == "patient" }
                                    .maxByOrNull { it.id }
                                val lastNotified = sessionPrefs.getLong("care_last_patient_msg_${assignment.id}", 0L)
                                if (lastPatientMsg != null && lastPatientMsg.id > lastNotified) {
                                    DoctorNotifier.show(
                                        context,
                                        assignment.patientUsername,
                                        lastPatientMsg.text.take(120),
                                        assignmentId = assignment.id,
                                        openDoctorPanel = true
                                    )
                                    sessionPrefs.edit {
                                        putLong("care_last_patient_msg_${assignment.id}", lastPatientMsg.id)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            delay(2500)
        }
    }

    LaunchedEffect(activeUser, currentScreen) {
        if (activeUser.isBlank()) return@LaunchedEffect
        SupportChatNotifier.ensureChannel(context)
        while (true) {
            val token = sessionPrefs.getString("auth_token", "").orEmpty()
            if (token.isNotBlank()) {
                val messages = runCatching { BackendApi.getSupportMessages(token) }.getOrDefault(emptyList())
                val inOpenChat = currentScreen == Screen.SupportChat && SupportChatSession.isUserInChat
                if (inOpenChat) {
                    markAdminMessagesNotified(sessionPrefs, messages)
                    runCatching { BackendApi.markSupportRead(token) }
                } else {
                    val lastNotified = sessionPrefs.getLong("support_last_notified_admin_id", 0L)
                    val freshAdmin = messages
                        .filter { it.sender == "admin" && it.id > lastNotified }
                        .maxByOrNull { it.id }
                    if (freshAdmin != null) {
                        val preview = freshAdmin.text.ifBlank { "Вам ответили на сообщение" }
                        SupportChatNotifier.showNewAdminReply(
                            context,
                            preview,
                            senderName = SupportAgentInfo.HUMAN_NAME
                        )
                        sessionPrefs.edit { putLong("support_last_notified_admin_id", freshAdmin.id) }
                    }
                }
            }
            delay(1500)
        }
    }

    LaunchedEffect(activeUser, currentScreen) {
        if (!activeUser.equals("Admin", ignoreCase = true)) return@LaunchedEffect
        SupportAdminNotifier.ensureChannel(context)
        while (true) {
            val key = sessionPrefs.getString("support_admin_key", "").orEmpty().trim()
            if (key.isNotBlank() && currentScreen != Screen.SupportAdmin) {
                val lastTs = sessionPrefs.getLong("support_last_admin_escalation_ts", 0L)
                val convs = runCatching {
                    BackendApi.adminListSupportConversationsDetailed(key).conversations
                }.getOrDefault(emptyList())
                val urgent = convs
                    .filter { it.needsAdminAttention && it.adminRequestedAt > lastTs }
                    .maxByOrNull { it.adminRequestedAt }
                if (urgent != null) {
                    SupportAdminNotifier.showEscalation(
                        context,
                        urgent.username.ifBlank { "Пользователь" },
                        "Просит специалиста Умника"
                    )
                    sessionPrefs.edit { putLong("support_last_admin_escalation_ts", urgent.adminRequestedAt) }
                }
            }
            delay(3000)
        }
    }

    LaunchedEffect(currentScreen, activeUser) {
        if (currentScreen == Screen.Home && activeUser.isNotBlank()) {
            refreshHomeData(activeUser)
        }
    }

    LaunchedEffect(Unit) {
        val launchStartedAt = System.currentTimeMillis()
        clearRestoredSessionIfNeeded(context, sessionPrefs)
        val deviceId = sessionPrefs.getString("device_id", null)
            ?: UUID.randomUUID().toString().also { sessionPrefs.edit { putString("device_id", it) } }
        runCatching {
            BackendApi.trackInstall(
                deviceId = deviceId,
                appVersion = BuildConfig.VERSION_NAME,
                username = sessionPrefs.getString("active_user", "").orEmpty().ifBlank { null }
            )
        }
        val savedUser = sessionPrefs.getString("active_user", "").orEmpty()
        if (savedUser.isNotBlank()) {
            activeUser = savedUser
            userRole = sessionPrefs.getString("user_role", "patient").orEmpty().ifBlank { "patient" }
            if (isAdminSession(savedUser, userRole)) {
                userRole = "admin"
                sessionPrefs.edit { putString("user_role", "admin") }
            }
            prefillProfile = databaseHelper.getProfile(savedUser)
            treatmentPlans.clear()
            treatmentPlans.addAll(databaseHelper.getTreatmentPlans(savedUser))
            wellbeingEntries.clear()
            wellbeingEntries.putAll(databaseHelper.getWellbeingEntries(savedUser))
            val token = sessionPrefs.getString("auth_token", "").orEmpty()
            currentScreen = when (userRole) {
                "admin" -> Screen.Home
                "doctor" -> {
                    val profile = if (token.isNotBlank()) {
                        runCatching { BackendApi.getMyDoctorProfile(token) }.getOrNull()
                    } else {
                        null
                    }
                    val hadCompleteProfile = sessionPrefs.getBoolean("doctor_profile_complete", false)
                    when {
                        profile?.isProfileReady() == true -> {
                            sessionPrefs.edit { putBoolean("doctor_profile_complete", true) }
                            Screen.DoctorPanel
                        }
                        profile == null && hadCompleteProfile -> Screen.DoctorPanel
                        else -> Screen.DoctorProfileForm
                    }
                }
                else -> if (prefillProfile == null) Screen.Profile else Screen.Home
            }
        }
        val minLoadingDurationMs = 1200L
        val elapsedMs = System.currentTimeMillis() - launchStartedAt
        if (elapsedMs < minLoadingDurationMs) {
            delay(minLoadingDurationMs - elapsedMs)
        }
        showStartupLoading = false
    }

    BackHandler(enabled = currentScreen != Screen.Onboarding) {
        when (currentScreen) {
            Screen.Treatment -> currentScreen = Screen.Home
            Screen.WellbeingDiary -> currentScreen = Screen.Home
            Screen.Settings -> currentScreen = Screen.PersonalProfile
            Screen.SupportChat -> currentScreen = Screen.Settings
            Screen.SupportAdmin -> currentScreen = Screen.Settings
            Screen.AdminPanel -> currentScreen = Screen.Home
            Screen.DoctorPanel -> activity?.finish()
            Screen.DoctorProfileForm -> currentScreen = Screen.DoctorPanel
            Screen.DoctorPeerChat -> currentScreen = Screen.DoctorPanel
            Screen.DoctorPatientChat -> {
                currentScreen = if (doctorChatAsDoctor) Screen.DoctorPanel else Screen.Treatment
            }
            Screen.PersonalProfile -> currentScreen = Screen.Home
            Screen.Home -> currentScreen = Screen.Profile
            Screen.Profile -> currentScreen = if (prefillProfile == null) Screen.Login else Screen.PersonalProfile
            Screen.Register -> currentScreen = Screen.Login
            Screen.Login -> currentScreen = Screen.Onboarding
            else -> activity?.finish()
        }
    }

    LaunchedEffect(showAssistantTour, assistantTourStep) {
        assistantSpotlightBounds = null
        if (showAssistantTour && assistantTourStep in assistantTourSteps.indices) {
            currentScreen = assistantTourSteps[assistantTourStep].targetScreen
        }
    }

    LaunchedEffect(showAssistantTour) {
        if (!showAssistantTour) {
            assistantSpotlightBounds = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = currentScreen,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
                label = "screen-crossfade"
            ) { animatedScreen ->
                when (animatedScreen) {
            Screen.Onboarding -> OnboardingScreen(
                modifier = Modifier.padding(innerPadding),
                onContinue = { currentScreen = Screen.Login }
            )

            Screen.Login -> LoginScreen(
                modifier = Modifier.padding(innerPadding),
                onLogin = { username, password ->
                    val normalized = username.trim()
                    val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(normalized).matches()
                    if (!isEmail && !isRussianEnglishLogin(normalized)) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Введите корректный логин на русском или английском языке."
                            )
                        }
                    } else {
                        scope.launch {
                            val deviceId = sessionPrefs.getString("device_id", null)
                                ?: UUID.randomUUID().toString().also { sessionPrefs.edit { putString("device_id", it) } }

                            val authResult = runCatching {
                                BackendApi.login(
                                    usernameOrEmail = normalized,
                                    password = password,
                                    deviceId = deviceId,
                                    appVersion = BuildConfig.VERSION_NAME
                                )
                            }
                            val auth = authResult.getOrNull()
                            if (authResult.isFailure) {
                                snackbarHostState.showSnackbar(
                                    "Нет связи с сервером. Проверьте Wi‑Fi и что backend запущен на ${BuildConfig.BACKEND_BASE_URL}."
                                )
                                return@launch
                            }

                            if (auth?.ok != true || auth.token.isNullOrBlank() || auth.username.isNullOrBlank()) {
                                val wrongCreds = auth?.error.isNullOrBlank() || auth?.error == "invalid_credentials"
                                if (wrongCreds) {
                                    val localUsername = databaseHelper.verifyLocalCredentials(normalized, password)
                                    if (localUsername != null) {
                                        val email = databaseHelper.getEmailByUsername(localUsername)
                                        if (email.isNullOrBlank()) {
                                            snackbarHostState.showSnackbar(
                                                "В локальной базе нет email для этого аккаунта. Зарегистрируйтесь заново."
                                            )
                                            return@launch
                                        }
                                        val reg = runCatching {
                                            BackendApi.register(
                                                username = localUsername.trim(),
                                                email = email.trim().lowercase(),
                                                password = password,
                                                deviceId = deviceId,
                                                appVersion = BuildConfig.VERSION_NAME
                                            )
                                        }.getOrNull()
                                        if (reg?.ok == true && !reg.token.isNullOrBlank() && !reg.username.isNullOrBlank()) {
                                            sessionPrefs.edit {
                                                putString("auth_token", reg.token)
                                                putString("active_user", reg.username)
                                            }
                                            activeUser = reg.username
                                            prefillProfile = databaseHelper.getProfile(activeUser)
                                            treatmentPlans.clear()
                                            treatmentPlans.addAll(databaseHelper.getTreatmentPlans(activeUser))
                                            wellbeingEntries.clear()
                                            wellbeingEntries.putAll(databaseHelper.getWellbeingEntries(activeUser))
                                            syncFirebaseAuthUser(context, email.trim().lowercase(), password)
                                            navigateAfterAuth(reg.role, prefillProfile != null)
                                            snackbarHostState.showSnackbar("Аккаунт снова привязан к серверу (база на ПК была новой).")
                                            return@launch
                                        }
                                        when (reg?.error) {
                                            "user_exists" -> snackbarHostState.showSnackbar(
                                                "Сервер уже знает этот логин, но пароль не подходит. Используйте «Забыли пароль?» или тот пароль, который задавали при последней регистрации на сервере."
                                            )
                                            "network_error" -> snackbarHostState.showSnackbar(
                                                "Нет связи с сервером при восстановлении аккаунта."
                                            )
                                            else -> snackbarHostState.showSnackbar(
                                                "Не удалось восстановить аккаунт. Проверьте backend и адрес ${BuildConfig.BACKEND_BASE_URL} в app/build.gradle.kts."
                                            )
                                        }
                                        return@launch
                                    }
                                    val emForFb = when {
                                        android.util.Patterns.EMAIL_ADDRESS.matcher(normalized).matches() ->
                                            normalized.lowercase()
                                        else -> databaseHelper.getEmailByUsername(normalized.trim())?.trim()?.lowercase()
                                    }
                                    if (!emForFb.isNullOrBlank()) {
                                        val firebaseApp = runCatching {
                                            if (FirebaseApp.getApps(context).isEmpty()) {
                                                FirebaseApp.initializeApp(context)
                                            } else {
                                                FirebaseApp.getInstance()
                                            }
                                        }.getOrNull()
                                        if (firebaseApp != null) {
                                            val fbAuth = FirebaseAuth.getInstance(firebaseApp)
                                            val fbUser = runCatching {
                                                fbAuth.signInWithEmailAndPassword(emForFb, password).await().user
                                            }.getOrNull()
                                            if (fbUser != null) {
                                                val idTok = runCatching {
                                                    fbUser.getIdToken(false).await().token
                                                }.getOrNull()
                                                if (!idTok.isNullOrBlank()) {
                                                    val sync = runCatching {
                                                        BackendApi.syncPasswordFromFirebase(
                                                            idToken = idTok,
                                                            password = password,
                                                            deviceId = deviceId,
                                                            appVersion = BuildConfig.VERSION_NAME
                                                        )
                                                    }.getOrNull()
                                                    runCatching { fbAuth.signOut() }
                                                    if (sync?.ok == true &&
                                                        !sync.token.isNullOrBlank() &&
                                                        !sync.username.isNullOrBlank()
                                                    ) {
                                                        sessionPrefs.edit {
                                                            putString("auth_token", sync.token)
                                                            putString("active_user", sync.username)
                                                        }
                                                        runCatching {
                                                            databaseHelper.updatePasswordByEmail(emForFb, password)
                                                        }
                                                        activeUser = sync.username
                                                        prefillProfile = databaseHelper.getProfile(activeUser)
                                                        treatmentPlans.clear()
                                                        treatmentPlans.addAll(
                                                            databaseHelper.getTreatmentPlans(activeUser)
                                                        )
                                                        wellbeingEntries.clear()
                                                        wellbeingEntries.putAll(
                                                            databaseHelper.getWellbeingEntries(activeUser)
                                                        )
                                                        navigateAfterAuth(sync.role, prefillProfile != null)
                                                        return@launch
                                                    }
                                                    if (sync?.error == "firebase_admin_not_configured") {
                                                        snackbarHostState.showSnackbar(
                                                            "Пароль изменён в Firebase, но backend не настроен: задайте FIREBASE_SERVICE_ACCOUNT_PATH или FIREBASE_SERVICE_ACCOUNT_JSON (см. backend/README.md)."
                                                        )
                                                        return@launch
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                snackbarHostState.showSnackbar(
                                    if (auth?.error == "network_error" || auth?.error == "empty_response") {
                                        "Сервер недоступен. Проверьте backend и адрес в приложении."
                                    } else if (wrongCreds) {
                                        "Неверный логин или пароль."
                                    } else {
                                        "Не удалось войти. Попробуйте позже."
                                    }
                                )
                                return@launch
                            }

                            sessionPrefs.edit {
                                putString("auth_token", auth.token)
                                putString("active_user", auth.username)
                            }

                            activeUser = auth.username
                            prefillProfile = databaseHelper.getProfile(activeUser)
                            treatmentPlans.clear()
                            treatmentPlans.addAll(databaseHelper.getTreatmentPlans(activeUser))
                            wellbeingEntries.clear()
                            wellbeingEntries.putAll(databaseHelper.getWellbeingEntries(activeUser))
                            val loggedEmail = auth.email?.trim()?.lowercase()
                            if (!loggedEmail.isNullOrBlank()) {
                                syncFirebaseAuthUser(context, loggedEmail, password)
                            }
                            navigateAfterAuth(auth.role, prefillProfile != null)
                        }
                    }
                },
                onOpenRegister = { currentScreen = Screen.Register },
                onRequestPasswordCode = { username, email ->
                    val un = username.trim()
                    val em = email.trim().lowercase()
                    if (!isRussianEnglishLogin(un)) {
                        return@LoginScreen PasswordResetFlowResult(false, "Проверьте формат логина.")
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                        return@LoginScreen PasswordResetFlowResult(false, "Введите корректный email.")
                    }

                    val firebaseApp = runCatching {
                        if (FirebaseApp.getApps(context).isEmpty()) {
                            FirebaseApp.initializeApp(context)
                        } else {
                            FirebaseApp.getInstance()
                        }
                    }.getOrNull()
                        ?: return@LoginScreen PasswordResetFlowResult(
                            false,
                            "Firebase не настроен. Добавьте google-services.json в app/ и синхронизируйте проект."
                        )

                    val auth = FirebaseAuth.getInstance(firebaseApp)
                    return@LoginScreen try {
                        auth.sendPasswordResetEmail(em).await()
                        PasswordResetFlowResult(
                            ok = true,
                            message = "Письмо для сброса пароля отправлено на $em."
                        )
                    } catch (e: FirebaseAuthException) {
                        val msg = when (e.errorCode) {
                            "ERROR_INVALID_EMAIL" -> "Некорректный email."
                            "ERROR_TOO_MANY_REQUESTS" -> "Слишком много попыток. Попробуйте позже."
                            "ERROR_USER_NOT_FOUND" -> "Аккаунт с таким email не найден в Firebase."
                            else -> "Не удалось отправить письмо: ${e.localizedMessage ?: e.errorCode}"
                        }
                        PasswordResetFlowResult(false, msg)
                    } catch (e: Exception) {
                        PasswordResetFlowResult(
                            false,
                            e.localizedMessage ?: "Проверьте интернет и настройки Firebase."
                        )
                    }
                },
                onVerifyResetCode = { _, _, _ ->
                    PasswordResetFlowResult(
                        false,
                        "Сброс пароля выполняется только по ссылке из письма Firebase."
                    )
                },
                onCompletePasswordReset = { _, _, _, _ ->
                    PasswordResetFlowResult(
                        false,
                        "Сброс пароля выполняется только по ссылке из письма Firebase."
                    )
                }
            )

            Screen.Register -> RegisterScreen(
                modifier = Modifier.padding(innerPadding),
                onRegister = { username, password, email ->
                    if (!isRussianEnglishLogin(username)) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Логин должен быть 3-30 символов и может содержать русские и английские буквы, цифры, точку, дефис и подчеркивание."
                            )
                        }
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        scope.launch { snackbarHostState.showSnackbar("Введите корректный email.") }
                    } else {
                        scope.launch {
                            val normalizedEmail = email.trim().lowercase()
                            val normalizedUser = username.trim()
                            val deviceId = sessionPrefs.getString("device_id", null)
                                ?: UUID.randomUUID().toString().also { sessionPrefs.edit { putString("device_id", it) } }

                            val authResult = runCatching {
                                BackendApi.register(
                                    username = normalizedUser,
                                    email = normalizedEmail,
                                    password = password,
                                    deviceId = deviceId,
                                    appVersion = BuildConfig.VERSION_NAME
                                )
                            }
                            val auth = authResult.getOrNull()
                            if (authResult.isFailure) {
                                snackbarHostState.showSnackbar(
                                    "Нет связи с сервером. Backend на ${BuildConfig.BACKEND_BASE_URL}, один Wi‑Fi с ПК. Эмулятор: 10.0.2.2 и тот же порт, что в build.gradle."
                                )
                                return@launch
                            }

                            if (auth?.ok != true || auth.token.isNullOrBlank() || auth.username.isNullOrBlank()) {
                                val msg = when (auth?.error) {
                                    "user_exists" -> "Логин или email уже занят."
                                    "invalid_password" -> "Пароль слишком короткий."
                                    "network_error", "empty_response" ->
                                        "Сервер недоступен. Убедитесь, что backend запущен на ${BuildConfig.BACKEND_BASE_URL} (см. app/build.gradle.kts → BACKEND_BASE_URL)."
                                    else -> "Не удалось зарегистрироваться. Попробуйте позже."
                                }
                                snackbarHostState.showSnackbar(msg)
                                return@launch
                            }

                            runCatching { databaseHelper.registerUser(normalizedUser, password, normalizedEmail) }
                            sessionPrefs.edit {
                                putString("auth_token", auth.token)
                                putString("active_user", auth.username)
                            }

                            activeUser = auth.username
                            prefillProfile = databaseHelper.getProfile(activeUser)
                            wellbeingEntries.clear()
                            syncFirebaseAuthUser(context, normalizedEmail, password)
                            currentScreen = Screen.Profile
                        }
                    }
                },
                onBackToLogin = { currentScreen = Screen.Login }
            )

            Screen.Profile -> ProfileScreen(
                modifier = Modifier.padding(innerPadding),
                username = activeUser,
                initialProfile = prefillProfile,
                onSave = { profile ->
                    if (databaseHelper.upsertProfile(profile)) {
                        prefillProfile = profile
                        treatmentPlans.clear()
                        treatmentPlans.addAll(databaseHelper.getTreatmentPlans(activeUser))
                        wellbeingEntries.clear()
                        wellbeingEntries.putAll(databaseHelper.getWellbeingEntries(activeUser))
                        currentScreen = Screen.Home
                        scope.launch {
                            snackbarHostState.showSnackbar("Профиль сохранен. Открыт главный экран.")
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Не удалось сохранить профиль.")
                        }
                    }
                }
            )

            Screen.Home -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
                username = activeUser,
                profile = prefillProfile,
                plans = treatmentPlans,
                wellbeingEntries = wellbeingEntries,
                isReportReady = isReportReadyForExport(treatmentPlans, wellbeingEntries),
                isAdmin = isAdminSession(activeUser, userRole),
                patientAssignmentId = patientAssignment?.id,
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                onOpenAdminPanel = { currentScreen = Screen.AdminPanel },
                onStartTreatment = { currentScreen = Screen.Treatment },
                onStopTreatment = {
                    val deleted = databaseHelper.deleteTreatmentPlansForUser(activeUser)
                    if (deleted) {
                        treatmentPlans.clear()
                        scope.launch { snackbarHostState.showSnackbar("Лечение прекращено.") }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Не удалось прекратить лечение.") }
                    }
                },
                onOpenDiary = { currentScreen = Screen.WellbeingDiary },
                onOpenProfile = { currentScreen = Screen.PersonalProfile },
                onOpenAssistant = {
                    assistantTourStep = 0
                    showAssistantTour = true
                },
                assistantTourActive = showAssistantTour,
                assistantSpotlight = assistantSpotlight,
                onAssistantSpotlightBounds = { assistantSpotlightBounds = it },
                persistedScannedMedicines = persistedScannedMedicines,
                onRefreshHomeMenu = {
                    if (activeUser.isNotBlank()) {
                        refreshHomeData(activeUser)
                    }
                },
                onPersistScannedMedicine = { candidate ->
                    if (activeUser.isBlank()) {
                        false
                    } else if (databaseHelper.upsertScannedMedicineCard(
                            ScannedMedicineCard(
                                username = activeUser,
                                name = candidate.name,
                                barcode = candidate.barcode,
                                infoUrl = candidate.infoUrl
                            )
                        )
                    ) {
                        refreshHomeData(activeUser)
                        true
                    } else {
                        false
                    }
                },
                onDeleteScannedMedicine = { candidate ->
                    if (activeUser.isNotBlank() && candidate.id > 0L) {
                        val ok = databaseHelper.deleteScannedMedicineCard(activeUser, candidate.id)
                        if (ok) {
                            refreshHomeData(activeUser)
                            scope.launch { snackbarHostState.showSnackbar("Упаковка удалена.") }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Не удалось удалить упаковку.") }
                        }
                    }
                },
                onRenameScannedMedicine = { candidate, newName ->
                    if (activeUser.isBlank() || candidate.id <= 0L) {
                        false
                    } else {
                        val ok = databaseHelper.updateScannedMedicineCardName(activeUser, candidate.id, newName)
                        if (ok) {
                            persistedScannedMedicines.replaceAll {
                                if (it.id == candidate.id) it.copy(name = newName.trim()) else it
                            }
                        }
                        ok
                    }
                },
                onExportPdf = { uri ->
                    val ok = exportTreatmentPlansToPdf(
                        context = context,
                        uri = uri,
                        username = activeUser,
                        plans = treatmentPlans,
                        userDisplayName = prefillProfile?.fullName,
                        birthDate = prefillProfile?.birthDate,
                        wellbeingByDate = wellbeingEntries
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (ok) "PDF успешно экспортирован." else "Не удалось экспортировать PDF."
                        )
                    }
                },
                onDeleteSavedMedicine = { medicine ->
                    val ok = databaseHelper.deleteSavedMedicine(medicine.id)
                    if (ok) {
                        savedMedicines.remove(medicine)
                        scope.launch { snackbarHostState.showSnackbar("Удалено из списка.") }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Не удалось удалить.") }
                    }
                }
            )

            Screen.PersonalProfile -> PersonalProfileScreen(
                modifier = Modifier.padding(innerPadding),
                username = activeUser,
                profile = prefillProfile,
                plans = treatmentPlans,
                onEditProfile = { currentScreen = Screen.Profile },
                onOpenSettings = { currentScreen = Screen.Settings },
                assistantTourActive = showAssistantTour,
                assistantSpotlight = assistantSpotlight,
                onAssistantSpotlightBounds = { assistantSpotlightBounds = it },
                onBack = { currentScreen = Screen.Home }
            )

            Screen.Settings -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                profile = prefillProfile,
                recentMedications24h = recentMedicationsLast24Hours(treatmentPlans),
                isAdmin = isAdminSession(activeUser, userRole),
                isDarkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                assistantTourActive = showAssistantTour,
                assistantSpotlight = assistantSpotlight,
                onAssistantSpotlightBounds = { assistantSpotlightBounds = it },
                onSaveSettings = { updatedProfile ->
                    if (databaseHelper.upsertProfile(updatedProfile)) {
                        prefillProfile = updatedProfile
                        scope.launch { snackbarHostState.showSnackbar("Настройки сохранены.") }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Не удалось сохранить настройки.") }
                    }
                },
                currentAdminKey = sessionPrefs.getString("support_admin_key", "").orEmpty(),
                onSaveAdminKey = { key ->
                    sessionPrefs.edit { putString("support_admin_key", key) }
                },
                onLogout = {
                    sessionPrefs.edit {
                        remove("active_user")
                        remove("auth_token")
                        remove("user_role")
                    }
                    activeUser = ""
                    prefillProfile = null
                    treatmentPlans.clear()
                    wellbeingEntries.clear()
                    savedMedicines.clear()
                    currentScreen = Screen.Login
                },
                onDeleteAccount = {
                    scope.launch {
                        val token = sessionPrefs.getString("auth_token", "").orEmpty()
                        val remoteDeleted = if (token.isNotBlank()) {
                            runCatching { BackendApi.deleteMe(token) }.getOrDefault(false)
                        } else {
                            false
                        }
                        val localDeleted = databaseHelper.deleteAccount(activeUser)
                        if (localDeleted || remoteDeleted) {
                            sessionPrefs.edit {
                                remove("active_user")
                                remove("auth_token")
                                remove("user_role")
                            }
                            activeUser = ""
                            userRole = "patient"
                            prefillProfile = null
                            treatmentPlans.clear()
                            wellbeingEntries.clear()
                            savedMedicines.clear()
                            currentScreen = Screen.Login
                            val msg = if (remoteDeleted) {
                                "Аккаунт удален."
                            } else {
                                "Аккаунт удален на устройстве. Сервер был недоступен."
                            }
                            snackbarHostState.showSnackbar(msg)
                        } else {
                            snackbarHostState.showSnackbar("Не удалось удалить аккаунт.")
                        }
                    }
                },
                onOpenSupportChat = { currentScreen = Screen.SupportChat },
                onOpenSupportAdmin = { currentScreen = Screen.SupportAdmin },
                onOpenAdminPanel = { currentScreen = Screen.AdminPanel },
                onBack = { currentScreen = Screen.PersonalProfile }
            )

            Screen.SupportChat -> SupportChatScreen(
                modifier = Modifier.padding(innerPadding),
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                userProfile = prefillProfile,
                username = activeUser,
                onMessagesSeen = { markAdminMessagesNotified(sessionPrefs, it) },
                onBack = { currentScreen = Screen.Settings }
            )

            Screen.SupportAdmin -> SupportAdminScreen(
                modifier = Modifier.padding(innerPadding),
                adminKeyProvider = { sessionPrefs.getString("support_admin_key", "").orEmpty() },
                onBack = { currentScreen = Screen.Settings }
            )

            Screen.AdminPanel -> AdminPanelScreen(
                modifier = Modifier.padding(innerPadding),
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                adminKeyProvider = { sessionPrefs.getString("support_admin_key", "").orEmpty() },
                onSaveAdminKey = { key ->
                    sessionPrefs.edit { putString("support_admin_key", key) }
                },
                onBack = { currentScreen = Screen.Home }
            )

            Screen.DoctorPanel -> DoctorPanelScreen(
                modifier = Modifier.padding(innerPadding),
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                doctorDisplayName = activeUser,
                patientMilestoneSeenProvider = { assignmentId ->
                    sessionPrefs.getInt("doctor_milestone_seen_$assignmentId", -1)
                },
                onPatientMilestoneSeen = { assignmentId, index ->
                    sessionPrefs.edit { putInt("doctor_milestone_seen_$assignmentId", index) }
                },
                onEditProfile = { currentScreen = Screen.DoctorProfileForm },
                onOpenChat = { assignmentId, patientName ->
                    doctorChatAssignmentId = assignmentId
                    doctorChatTitle = patientName
                    doctorChatAsDoctor = true
                    currentScreen = Screen.DoctorPatientChat
                },
                onOpenPeerChat = { doctorId, doctorName, specialty ->
                    peerChatDoctorId = doctorId
                    peerChatTitle = doctorName
                    peerChatSpecialty = specialty
                    currentScreen = Screen.DoctorPeerChat
                },
                onBack = {
                    sessionPrefs.edit {
                        remove("active_user")
                        remove("auth_token")
                        remove("user_role")
                    }
                    activeUser = ""
                    userRole = "patient"
                    currentScreen = Screen.Login
                }
            )

            Screen.DoctorProfileForm -> DoctorProfileFormScreen(
                modifier = Modifier.padding(innerPadding),
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                onProfileSaved = {
                    sessionPrefs.edit { putBoolean("doctor_profile_complete", true) }
                    currentScreen = Screen.DoctorPanel
                },
                onBack = { currentScreen = Screen.DoctorPanel }
            )

            Screen.DoctorPatientChat -> DoctorPatientChatScreen(
                modifier = Modifier.fillMaxSize(),
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                assignmentId = doctorChatAssignmentId,
                title = doctorChatTitle,
                viewerIsDoctor = doctorChatAsDoctor,
                onBack = {
                    currentScreen = if (doctorChatAsDoctor) Screen.DoctorPanel else Screen.Treatment
                }
            )

            Screen.DoctorPeerChat -> DoctorPeerChatScreen(
                modifier = Modifier.fillMaxSize(),
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                peerDoctorId = peerChatDoctorId,
                peerTitle = peerChatTitle,
                peerSpecialty = peerChatSpecialty,
                onBack = { currentScreen = Screen.DoctorPanel }
            )

            Screen.Treatment -> TreatmentScreen(
                modifier = Modifier.padding(innerPadding),
                username = activeUser,
                profile = prefillProfile,
                plans = treatmentPlans,
                wellbeingEntries = wellbeingEntries,
                tokenProvider = { sessionPrefs.getString("auth_token", "").orEmpty() },
                patientMilestoneSeenIndex = patientAssignment?.id?.let { id ->
                    sessionPrefs.getInt("patient_milestone_seen_$id", -1)
                } ?: -1,
                onPatientMilestoneSeen = { index ->
                    patientAssignment?.id?.let { id ->
                        sessionPrefs.edit { putInt("patient_milestone_seen_$id", index) }
                    }
                },
                onAssignmentChanged = { patientAssignment = it },
                onAutoPlansCreated = { autoPlans ->
                    var saved = 0
                    autoPlans.forEach { plan ->
                        val insertedId = databaseHelper.addTreatmentPlan(plan)
                        if (insertedId != -1L) {
                            treatmentPlans.add(0, plan.copy(id = insertedId))
                            scheduleTreatmentReminder(
                                context = context,
                                alarmManager = alarmManager,
                                plan = plan,
                                profile = prefillProfile
                            )
                            saved++
                        }
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (saved > 0) {
                                "Автоплан создан: $saved приёмов в расписании."
                            } else {
                                "Не удалось сохранить автоплан."
                            }
                        )
                    }
                },
                onOpenDoctorChat = { assignmentId, doctorName ->
                    doctorChatAssignmentId = assignmentId
                    doctorChatTitle = doctorName
                    doctorChatAsDoctor = false
                    currentScreen = Screen.DoctorPatientChat
                },
                assistantTourActive = showAssistantTour,
                assistantSpotlight = assistantSpotlight,
                onAssistantSpotlightBounds = { assistantSpotlightBounds = it },
                onSaveDayPlans = { dayPlans ->
                    var allSaved = true
                    dayPlans.forEach { plan ->
                        val insertedId = databaseHelper.addTreatmentPlan(plan)
                        if (insertedId != -1L) {
                            treatmentPlans.add(0, plan.copy(id = insertedId))
                            scheduleTreatmentReminder(
                                context = context,
                                alarmManager = alarmManager,
                                plan = plan,
                                profile = prefillProfile
                            )
                        } else {
                            allSaved = false
                        }
                    }
                    if (allSaved) {
                        scope.launch { snackbarHostState.showSnackbar("День лечения сохранен.") }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Часть записей не удалось сохранить.") }
                    }
                },
                onDeletePlan = { plan ->
                    val deleted = databaseHelper.deleteTreatmentPlan(plan.id)
                    if (deleted) {
                        treatmentPlans.remove(plan)
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (deleted) "Запись лечения удалена." else "Не удалось удалить запись."
                        )
                    }
                },
                onBack = { currentScreen = Screen.Home }
            )

            Screen.WellbeingDiary -> WellbeingDiaryScreen(
                modifier = Modifier.padding(innerPadding),
                plans = treatmentPlans,
                wellbeingByDate = wellbeingEntries,
                assistantTourActive = showAssistantTour,
                assistantSpotlight = assistantSpotlight,
                onAssistantSpotlightBounds = { assistantSpotlightBounds = it },
                onSaveComment = { day, status, comment ->
                    val saved = databaseHelper.upsertWellbeingEntry(activeUser, day, status, comment)
                    if (saved) {
                        if (comment.isBlank() && status.isBlank()) {
                            wellbeingEntries.remove(day)
                        } else {
                            wellbeingEntries[day] = WellbeingEntry(
                                username = activeUser,
                                date = day,
                                status = status,
                                comment = comment.trim()
                            )
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar("Комментарий самочувствия обновлен.")
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Не удалось сохранить комментарий.")
                        }
                    }
                },
                onBack = { currentScreen = Screen.Home }
            )
            }
            }
            if (showAssistantTour && assistantTourStep in assistantTourSteps.indices) {
                AssistantTourOverlay(
                    spotlightBounds = assistantSpotlightBounds,
                    stepIndex = assistantTourStep,
                    totalSteps = assistantTourSteps.size,
                    step = assistantTourSteps[assistantTourStep],
                    onClose = {
                        showAssistantTour = false
                        assistantSpotlightBounds = null
                        assistantTourStep = 0
                        currentScreen = Screen.Home
                    },
                    onBack = { assistantTourStep = (assistantTourStep - 1).coerceAtLeast(0) },
                    onNext = {
                        if (assistantTourStep < assistantTourSteps.lastIndex) {
                            assistantTourStep += 1
                        } else {
                            showAssistantTour = false
                            assistantSpotlightBounds = null
                            assistantTourStep = 0
                            currentScreen = Screen.Home
                        }
                    }
                )
            }
            if (showStartupLoading) {
                StartupLoadingOverlay()
            }
        }
    }

}

@Composable
private fun StartupLoadingOverlay() {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(app.heroGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AssetImage(
                    fileName = "startup_loading_photo.png",
                    contentDescription = "Загрузка приложения",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            CircularProgressIndicator(
                modifier = Modifier.size(72.dp),
                color = scheme.primary,
                trackColor = Color.White.copy(alpha = 0.25f),
                strokeWidth = 7.dp
            )
            Text(
                text = "Загрузка приложения...",
                style = MaterialTheme.typography.titleMedium,
                color = app.onHero
            )
        }
    }
}

@Composable
private fun AssistantTourOverlay(
    spotlightBounds: Rect?,
    stepIndex: Int,
    totalSteps: Int,
    step: AssistantTourStep,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val hintScroll = rememberScrollState()
    val stepsIndicatorScroll = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            drawRect(Color.Black.copy(alpha = 0.58f))
            spotlightBounds?.let { r ->
                val pad = 10.dp.toPx()
                val radius = 18.dp.toPx()
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(r.left - pad, r.top - pad),
                    size = Size(r.width + 2 * pad, r.height + 2 * pad),
                    cornerRadius = CornerRadius(radius, radius),
                    blendMode = BlendMode.Clear
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetImage(
                    fileName = assistantTourAsset(stepIndex),
                    contentDescription = "Умник",
                    modifier = Modifier
                        .size(124.dp)
                        .clip(CircleShape)
                        .border(3.dp, app.accentRing.copy(alpha = 0.9f), CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp),
                colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(hintScroll)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Умник — справка по приложению",
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface
                    )
                    Text(
                        text = "Шаг ${stepIndex + 1} из $totalSteps: ${step.title}",
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.primary
                    )
                    Text(
                        text = step.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurface
                    )
                    if (step.spotlight != AssistantTourSpotlight.None) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = app.cardOnHeroSecondary)
                        ) {
                            Text(
                                text = "На экране подсвечен ключевой элемент.",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                    if (step.extraText.isNotBlank()) {
                        Text(
                            text = step.extraText,
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(stepsIndicatorScroll),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == stepIndex) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == stepIndex) app.accentRing else app.accentRingSoft)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onClose,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Закрыть")
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    enabled = stepIndex > 0
                ) {
                    Text("Назад")
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (stepIndex < totalSteps - 1) "Далее" else "Готово")
                }
            }
        }
    }
}

@Composable
private fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit
) {
    val app = LocalMedAppColors.current
    val uiMetrics = rememberUiMetrics()

    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showContent = true }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(app.heroGradient)
            .padding(20.dp)
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(700)) + slideInVertically(tween(700), initialOffsetY = { it / 4 }),
            exit = fadeOut(tween(300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(uiMetrics.onboardingLogoSize)
                        .clip(CircleShape)
                        .border(3.dp, app.accentRing.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    WelcomeLogo(
                        contentDescription = "Умное Здоровье",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                MedicinePackLoopAnimation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(uiMetrics.onboardingAnimHeight)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Добро пожаловать в Умное Здоровье",
                    style = MaterialTheme.typography.headlineSmall,
                    color = app.onHero
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "умный контроль лечения, напоминания, сканирование упаковок, личный дневник самочувствия с дальнейшим отчетом лечения в одном приложении.",
                    color = app.onHeroMuted
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                    Text("Начать")
                }
            }
        }
    }
}

@Composable
private fun MedicinePackLoopAnimation(modifier: Modifier = Modifier) {
    val ill = LocalMedAppColors.current
    val transition = rememberInfiniteTransition(label = "medicine-pack-loop")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "medicine-pack-t"
    )
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "medicine-pack-shimmer"
    )
    val sparkleT by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "medicine-pack-sparkle-t"
    )

    // 0..1 : 0..0.25 open flap, 0.25..1 pills fall
    val openPhase = (t / 0.25f).coerceIn(0f, 1f)
    // springy opening: slight overshoot then settle
    val springy = run {
        val s = FastOutSlowInEasing.transform(openPhase)
        // damped oscillation around 1.0 near the end of opening
        val wobble = (kotlin.math.sin(openPhase * 3.6f * Math.PI.toFloat()) * (1f - openPhase) * 0.22f)
        (s + wobble).coerceIn(0f, 1.12f)
    }
    val flapAngle = (-78f * springy)

    val pills = remember {
        listOf(
            PillSeed(x = 0.42f, delay = 0.18f, size = 0.086f, spread = -0.16f, spin = 0.9f),
            PillSeed(x = 0.50f, delay = 0.24f, size = 0.096f, spread = 0.03f, spin = 1.2f),
            PillSeed(x = 0.58f, delay = 0.30f, size = 0.084f, spread = 0.18f, spin = 1.0f),
            PillSeed(x = 0.46f, delay = 0.36f, size = 0.078f, spread = -0.10f, spin = 1.35f),
            PillSeed(x = 0.62f, delay = 0.42f, size = 0.090f, spread = 0.14f, spin = 0.85f),
            PillSeed(x = 0.52f, delay = 0.48f, size = 0.084f, spread = -0.02f, spin = 1.15f),
            PillSeed(x = 0.40f, delay = 0.54f, size = 0.078f, spread = -0.22f, spin = 0.95f),
            PillSeed(x = 0.60f, delay = 0.60f, size = 0.086f, spread = 0.23f, spin = 1.05f),
            PillSeed(x = 0.48f, delay = 0.66f, size = 0.076f, spread = -0.08f, spin = 1.45f),
            PillSeed(x = 0.56f, delay = 0.72f, size = 0.082f, spread = 0.07f, spin = 1.25f),
            PillSeed(x = 0.44f, delay = 0.78f, size = 0.074f, spread = -0.28f, spin = 0.8f),
            PillSeed(x = 0.64f, delay = 0.84f, size = 0.080f, spread = 0.30f, spin = 0.9f)
        )
    }
    val grainDots = remember {
        val dots = ArrayList<Pair<Float, Float>>(120)
        var s = 1337
        fun rnd(): Float {
            s = (s * 1103515245 + 12345)
            return (((s ushr 16) and 0x7FFF) / 32767f)
        }
        repeat(120) { dots.add(rnd() to rnd()) }
        dots
    }
    val sparklePoints = remember {
        val pts = ArrayList<Triple<Float, Float, Float>>(18) // x,y,size
        var s = 90210
        fun rnd(): Float {
            s = (s * 1664525 + 1013904223)
            return (((s ushr 8) and 0xFFFFFF) / 16777215f)
        }
        repeat(18) {
            pts.add(Triple(0.18f + rnd() * 0.64f, 0.06f + rnd() * 0.30f, 0.6f + rnd() * 1.4f))
        }
        pts
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // “floor” line + soft shadow gradient (depth)
        val floorY = h * 0.93f
        drawLine(
            color = Color.White.copy(alpha = 0.10f),
            start = Offset(w * 0.18f, floorY),
            end = Offset(w * 0.82f, floorY),
            strokeWidth = (w * 0.006f).coerceIn(2f, 6f),
            cap = StrokeCap.Round
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x22000000), Color.Transparent),
                startY = floorY - h * 0.08f,
                endY = floorY + h * 0.08f
            ),
            topLeft = Offset(0f, floorY - h * 0.08f),
            size = Size(w, h * 0.16f)
        )

        val packW = w * 0.28f
        val packH = h * 0.62f
        val breathe = (kotlin.math.sin(t * 6.28f) * 0.5f + 0.5f) // 0..1
        val packScale = 0.99f + 0.02f * (0.5f - kotlin.math.abs(breathe - 0.5f)) * 2f // ~0.99..1.01
        val packParallaxY = (breathe - 0.5f) * (h * 0.012f)
        val packX = (w - packW) / 2f
        val packY = h * 0.14f + packParallaxY

        val packBodyBrush = Brush.linearGradient(
            colors = listOf(ill.illustrationPackDeep, ill.illustrationPackMid),
            start = Offset(packX, packY),
            end = Offset(packX + packW, packY + packH)
        )
        val packEdgeBrush = Brush.linearGradient(
            colors = listOf(ill.illustrationPackDeep, ill.illustrationPackMid),
            start = Offset(packX, packY),
            end = Offset(packX, packY + packH)
        )
        val packAccent = ill.illustrationPackAccent
        val shadowColor = Color(0x22000000)
        val gloss = Color.White.copy(alpha = 0.16f)

        // draw some pills BEHIND the pack (depth)
        val fallT = ((t - 0.25f) / 0.75f).coerceIn(0f, 1f)
        fun computePillPose(index: Int, pill: PillSeed): Pair<Float, Float>? {
            val local = ((fallT - pill.delay).coerceAtLeast(0f) * 1.35f).coerceIn(0f, 1f)
            if (local <= 0f) return null
            val eased = FastOutSlowInEasing.transform(local)
            val spreadPx = pill.spread * w * 0.38f
            val drift = kotlin.math.sin((t + index * 0.11f) * 6.28f) * (w * 0.010f)
            val x = (w * pill.x) + spreadPx * eased + drift
            val yStart = packY + packH * 0.18f
            val yEnd = floorY - (h * 0.012f)
            var y = yStart + (yEnd - yStart) * eased
            // 2 small bounces + settle
            if (local > 0.84f) {
                val b = ((local - 0.84f) / 0.16f).coerceIn(0f, 1f)
                val amp = (h * 0.05f) * (1f - b)
                val bounce = kotlin.math.abs(kotlin.math.sin(b * Math.PI.toFloat() * 2.2f))
                y -= bounce * amp
            }
            // short slide/roll after landing
            val slide = ((local - 0.86f) / 0.14f).coerceIn(0f, 1f)
            val slideEase = 1f - (1f - slide) * (1f - slide)
            val x2 = x + (spreadPx * 0.22f * slideEase)
            return x2 to y
        }

        fun drawPill(index: Int, pill: PillSeed, behind: Boolean) {
            val pose = computePillPose(index, pill) ?: return
            val (x, y) = pose
            val r = (w.coerceAtMost(h) * pill.size).coerceIn(10f, 26f)
            val rotateDeg = (t * 360f * pill.spin) % 360f
            val local = ((fallT - pill.delay).coerceAtLeast(0f) * 1.35f).coerceIn(0f, 1f)
            val blur = ((1f - local) * 0.9f).coerceIn(0f, 1f)

            // motion blur trails (fast phase)
            if (blur > 0.25f) {
                repeat(2) { ti ->
                    val a = (blur * (0.14f - ti * 0.05f)).coerceAtLeast(0f)
                    val dx = (ti + 1) * (w * 0.010f) * (if (behind) 0.7f else 1f)
                    val dy = (ti + 1) * (h * 0.010f)
                    drawRoundRect(
                        color = Color(0x1A000000).copy(alpha = a),
                        topLeft = Offset(x - r + dx, y - r * 0.55f + dy),
                        size = Size(r * 2f, r * 1.1f),
                        cornerRadius = CornerRadius(r, r)
                    )
                }
            }

            // shadow (stronger near floor)
            val floorProximity = ((y - (floorY - h * 0.22f)) / (h * 0.22f)).coerceIn(0f, 1f)
            drawRoundRect(
                color = Color(0x22000000).copy(alpha = 0.10f + 0.18f * floorProximity),
                topLeft = Offset(x - r + 3f, y - r * 0.55f + 4f),
                size = Size(r * 2f, r * 1.1f),
                cornerRadius = CornerRadius(r, r)
            )

            rotate(degrees = rotateDeg, pivot = Offset(x, y)) {
                val capsuleLight = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFE7ECF6)),
                    start = Offset(x - r, y - r),
                    end = Offset(x + r, y + r)
                )
                val capsuleGreen = Brush.linearGradient(
                    colors = listOf(ill.illustrationPillGreenStart, ill.illustrationPillGreenEnd),
                    start = Offset(x, y - r),
                    end = Offset(x + r, y + r)
                )
                drawRoundRect(
                    brush = capsuleLight,
                    topLeft = Offset(x - r, y - r * 0.55f),
                    size = Size(r * 2f, r * 1.1f),
                    cornerRadius = CornerRadius(r, r)
                )
                drawRoundRect(
                    brush = capsuleGreen,
                    topLeft = Offset(x, y - r * 0.55f),
                    size = Size(r, r * 1.1f),
                    cornerRadius = CornerRadius(r, r)
                )
                // seam + AO
                drawLine(
                    color = Color(0x26000000),
                    start = Offset(x, y - r * 0.52f),
                    end = Offset(x, y + r * 0.52f),
                    strokeWidth = (r * 0.10f).coerceIn(1.2f, 3.2f),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0x12000000),
                    start = Offset(x - r * 0.06f, y - r * 0.50f),
                    end = Offset(x - r * 0.06f, y + r * 0.50f),
                    strokeWidth = (r * 0.18f).coerceIn(1.6f, 4.5f),
                    cap = StrokeCap.Round
                )
                // diagonal highlight stripe
                rotate(degrees = -24f, pivot = Offset(x, y)) {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.18f),
                        topLeft = Offset(x - r * 0.95f, y - r * 0.38f),
                        size = Size(r * 0.70f, r * 0.22f),
                        cornerRadius = CornerRadius(r, r)
                    )
                }
            }
        }

        pills.forEachIndexed { index, pill ->
            if (index % 4 == 0) drawPill(index, pill, behind = true)
        }

        // PACK (scaled for parallax “breathing”)
        withTransform({
            val pivot = Offset(packX + packW / 2f, packY + packH / 2f)
            scale(scaleX = packScale, scaleY = packScale, pivot = pivot)
        }) {
            // Shadow
            drawRoundRect(
                color = shadowColor,
                topLeft = Offset(packX + 8f, packY + 10f),
                size = Size(packW, packH),
                cornerRadius = CornerRadius(22f, 22f)
            )

            // Main pack (gradient for more realism)
            drawRoundRect(
                brush = packBodyBrush,
                topLeft = Offset(packX, packY),
                size = Size(packW, packH),
                cornerRadius = CornerRadius(22f, 22f)
            )
            // bevel edges (thin gradients)
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                    startX = packX,
                    endX = packX + packW * 0.22f
                ),
                topLeft = Offset(packX + 2f, packY + 2f),
                size = Size(packW * 0.22f, packH - 4f),
                cornerRadius = CornerRadius(20f, 20f)
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color(0x24000000)),
                    startX = packX + packW * 0.78f,
                    endX = packX + packW
                ),
                topLeft = Offset(packX + packW * 0.78f, packY + 2f),
                size = Size(packW * 0.22f - 2f, packH - 4f),
                cornerRadius = CornerRadius(20f, 20f)
            )
            // Left edge shading
            drawRoundRect(
                brush = packEdgeBrush,
                topLeft = Offset(packX, packY),
                size = Size(packW * 0.16f, packH),
                cornerRadius = CornerRadius(22f, 22f),
                alpha = 0.55f
            )

            // Cardboard grain texture (dots)
            grainDots.forEachIndexed { i, (gx, gy) ->
                val px = packX + gx * packW
                val py = packY + gy * packH
                val rr = (w * 0.003f).coerceIn(0.6f, 1.4f)
                val a = (0.05f + (i % 5) * 0.01f).coerceAtMost(0.09f)
                drawCircle(ill.illustrationPackDeep.copy(alpha = a), radius = rr, center = Offset(px, py))
            }

            // Gloss highlight (matte)
            drawRoundRect(
                color = gloss,
                topLeft = Offset(packX + packW * 0.08f, packY + packH * 0.08f),
                size = Size(packW * 0.34f, packH * 0.76f),
                cornerRadius = CornerRadius(18f, 18f)
            )

            // Moving shimmer stripe (arc)
            val arc = kotlin.math.sin(shimmer * Math.PI.toFloat())
            val stripeX = packX - packW * 0.38f + (packW * 1.76f) * shimmer
            val stripeY = packY + packH * (0.10f + 0.10f * arc)
            val stripePath = Path().apply {
                moveTo(stripeX, stripeY)
                lineTo(stripeX + packW * 0.20f, stripeY)
                lineTo(stripeX + packW * 0.52f, stripeY + packH)
                lineTo(stripeX + packW * 0.32f, stripeY + packH)
                close()
            }
            drawPath(path = stripePath, color = Color.White.copy(alpha = 0.10f))

            // Accent stripe
            drawRoundRect(
                color = packAccent.copy(alpha = 0.85f),
                topLeft = Offset(packX + packW * 0.12f, packY + packH * 0.18f),
                size = Size(packW * 0.76f, packH * 0.16f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Inner cavity becomes visible when open
            if (openPhase > 0.22f) {
                val cavityAlpha = ((openPhase - 0.22f) / 0.78f).coerceIn(0f, 1f)
                drawRoundRect(
                    color = ill.illustrationPackDeep.copy(alpha = 0.55f * cavityAlpha),
                    topLeft = Offset(packX + packW * 0.12f, packY + packH * 0.04f),
                    size = Size(packW * 0.76f, packH * 0.22f),
                    cornerRadius = CornerRadius(18f, 18f)
                )
            }

            // Open flap + contact shadow at hinge
            val flapH = packH * 0.22f
            val hinge = Offset(packX + packW * 0.12f, packY + flapH * 0.95f)
            val contact = springy.coerceIn(0f, 1f)
            val hingeShadowW = packW * (0.22f + 0.26f * contact)
            val hingeShadowH = flapH * (0.10f + 0.22f * contact)
            drawRoundRect(
                color = Color(0x33000000).copy(alpha = 0.18f + 0.22f * contact),
                topLeft = Offset(hinge.x - hingeShadowW * 0.35f, hinge.y - hingeShadowH * 0.10f),
                size = Size(hingeShadowW, hingeShadowH),
                cornerRadius = CornerRadius(14f, 14f)
            )

            // lid shadow on pack while opening
            val lidShadowAlpha = (springy * 0.35f).coerceIn(0f, 0.35f)
            drawRoundRect(
                color = Color(0x1F000000).copy(alpha = lidShadowAlpha),
                topLeft = Offset(packX + packW * 0.10f, packY + flapH * 0.80f),
                size = Size(packW * 0.80f, flapH * 0.55f),
                cornerRadius = CornerRadius(16f, 16f)
            )
            rotate(degrees = flapAngle, pivot = hinge) {
                val flapPath = Path().apply {
                    moveTo(packX + packW * 0.10f, packY + flapH)
                    lineTo(packX + packW * 0.90f, packY + flapH)
                    lineTo(packX + packW * 0.82f, packY)
                    lineTo(packX + packW * 0.18f, packY)
                    close()
                }
                val flapTop = Brush.linearGradient(
                    colors = listOf(ill.illustrationPackDeep, ill.illustrationPackMid),
                    start = Offset(packX, packY),
                    end = Offset(packX + packW, packY + flapH)
                )
                drawPath(flapPath, color = Color(0x2A000000))
                drawPath(path = flapPath, brush = flapTop)
                drawPath(path = flapPath, color = Color.White.copy(alpha = 0.12f))
                drawRoundRect(
                    color = ill.illustrationPackDeep.copy(alpha = 0.75f),
                    topLeft = Offset(packX + packW * 0.12f, packY + flapH * 0.90f),
                    size = Size(packW * 0.76f, flapH * 0.10f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
            }
        }

        pills.forEachIndexed { index, pill ->
            if (index % 4 != 0) drawPill(index, pill, behind = false)
        }

        // premium sparkles (diamonds + crosses)
        val spAlphaBase = (kotlin.math.sin((sparkleT * 6.28f) + 1.0f) * 0.5f + 0.5f)
        sparklePoints.forEachIndexed { i, (sx, sy, ss) ->
            val a = (0.08f + 0.18f * spAlphaBase) * (0.6f + (i % 3) * 0.18f)
            val cx = w * sx
            val cy = h * sy
            val r = (w * 0.010f * ss).coerceIn(2.5f, 7.5f)
            val diamond = Path().apply {
                moveTo(cx, cy - r)
                lineTo(cx + r, cy)
                lineTo(cx, cy + r)
                lineTo(cx - r, cy)
                close()
            }
            drawPath(diamond, Color.White.copy(alpha = a))
            drawLine(
                Color.White.copy(alpha = a * 0.65f),
                Offset(cx - r, cy),
                Offset(cx + r, cy),
                strokeWidth = (r * 0.18f).coerceAtLeast(1.2f),
                cap = StrokeCap.Round
            )
            drawLine(
                Color.White.copy(alpha = a * 0.65f),
                Offset(cx, cy - r),
                Offset(cx, cy + r),
                strokeWidth = (r * 0.18f).coerceAtLeast(1.2f),
                cap = StrokeCap.Round
            )
        }
    }
}

private data class PillSeed(
    val x: Float,
    val delay: Float,
    val size: Float,
    val spread: Float,
    val spin: Float
)

private data class PasswordResetFlowResult(
    val ok: Boolean,
    val message: String,
    val devCode: String? = null
)

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: (username: String, password: String) -> Unit,
    onOpenRegister: () -> Unit,
    onRequestPasswordCode: suspend (username: String, email: String) -> PasswordResetFlowResult,
    onVerifyResetCode: suspend (username: String, email: String, code: String) -> PasswordResetFlowResult,
    onCompletePasswordReset: suspend (username: String, email: String, code: String, newPassword: String) -> PasswordResetFlowResult
) {
    val app = LocalMedAppColors.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }
    val gradient = app.heroGradient
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }
    val pulse = rememberInfiniteTransition(label = "login-btn-pulse")
    val loginButtonScale by pulse.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "login-btn-scale"
    )
    val readableFieldColors = readableOutlinedFieldColors()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Помощник по лечению",
                style = MaterialTheme.typography.headlineSmall,
                color = app.onHero
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Войдите в аккаунт, чтобы продолжить",
                style = MaterialTheme.typography.bodyMedium,
                color = app.onHeroMuted
            )
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500), initialOffsetY = { it / 3 }),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Логин (рус)") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onLogin(username.trim(), password.trim()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(loginButtonScale),
                        enabled = username.isNotBlank() && password.isNotBlank()
                    ) {
                        Text("Войти")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onOpenRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Регистрация")
                    }
                    TextButton(
                        onClick = { showForgotDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Забыли пароль?")
                    }
                }
            }
            }
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onRequestPasswordCode = onRequestPasswordCode,
            onVerifyResetCode = onVerifyResetCode,
            onCompletePasswordReset = onCompletePasswordReset
        )
    }
}

@Composable
private fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegister: (username: String, password: String, email: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val app = LocalMedAppColors.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val gradient = app.heroGradient
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }
    val readableFieldColors = readableOutlinedFieldColors()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Регистрация",
                style = MaterialTheme.typography.headlineSmall,
                color = app.onHero
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Создайте аккаунт на русском или английском",
                style = MaterialTheme.typography.bodyMedium,
                color = app.onHeroMuted
            )
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(500)) + slideInHorizontally(tween(500), initialOffsetX = { it / 3 }),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Логин (рус)") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = repeatPassword,
                        onValueChange = { repeatPassword = it },
                        label = { Text("Повторить пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = repeatPassword.isNotBlank() && repeatPassword != password,
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (repeatPassword.isNotBlank() && repeatPassword != password) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Пароли не совпадают",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onRegister(username.trim(), password.trim(), email.trim()) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = username.isNotBlank() &&
                            password.isNotBlank() &&
                            repeatPassword.isNotBlank() &&
                            password == repeatPassword &&
                            email.isNotBlank()
                    ) {
                        Text("Создать аккаунт")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onBackToLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Назад ко входу")
                    }
                }
            }
            }
        }
    }
}

private enum class ForgotPasswordFlowStep {
    EnterCredentials,
    EnterOtp,
    NewPassword
}

@Composable
private fun SixDigitOtpField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val app = LocalMedAppColors.current
    val frameOutline = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, frameOutline),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = enabled) { focusRequester.requestFocus() }
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(6) { i ->
                    val active = enabled && i == value.length && value.length < 6
                    val has = i < value.length
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (active) 2.dp else 1.dp,
                                color = when {
                                    active -> app.accentRing
                                    has -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                },
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.getOrNull(i)?.toString() ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.filter { c -> c.isDigit() }.take(6)) },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent)
            )
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onRequestPasswordCode: suspend (username: String, email: String) -> PasswordResetFlowResult,
    onVerifyResetCode: suspend (username: String, email: String, code: String) -> PasswordResetFlowResult,
    onCompletePasswordReset: suspend (username: String, email: String, code: String, newPassword: String) -> PasswordResetFlowResult
) {
    var step by remember { mutableStateOf(ForgotPasswordFlowStep.EnterCredentials) }
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var statusOk by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val readableFieldColors = readableOutlinedFieldColors()

    fun resetAll() {
        step = ForgotPasswordFlowStep.EnterCredentials
        login = ""
        email = ""
        otp = ""
        newPassword = ""
        repeatPassword = ""
        status = ""
        statusOk = false
        loading = false
    }

    val close: () -> Unit = {
        resetAll()
        onDismiss()
    }

    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = LocalMedAppColors.current.cardOnHero),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (step == ForgotPasswordFlowStep.EnterOtp) 16.dp else 20.dp,
                        vertical = if (step == ForgotPasswordFlowStep.EnterOtp) 14.dp else 20.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    if (step == ForgotPasswordFlowStep.EnterOtp) 8.dp else 12.dp
                )
            ) {
                Text(
                    when (step) {
                        ForgotPasswordFlowStep.EnterCredentials -> "Сброс пароля"
                        ForgotPasswordFlowStep.EnterOtp -> "Код из письма"
                        ForgotPasswordFlowStep.NewPassword -> "Новый пароль"
                    },
                    style = if (step == ForgotPasswordFlowStep.EnterOtp) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.titleLarge
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
                when (step) {
                    ForgotPasswordFlowStep.EnterCredentials -> {
                        Text(
                            "Укажите логин и email",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = login,
                            onValueChange = { login = it },
                            label = { Text("Логин") },
                            colors = readableFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = readableFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    status = ""
                                    val un = login.trim()
                                    val em = email.trim().lowercase()
                                    if (!isRussianEnglishLogin(un)) {
                                        statusOk = false
                                        status = "Проверьте формат логина."
                                        return@launch
                                    }
                                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                                        statusOk = false
                                        status = "Введите корректный email."
                                        return@launch
                                    }
                                    loading = true
                                    val r = onRequestPasswordCode(un, em)
                                    loading = false
                                    statusOk = r.ok
                                    status = r.message
                                    if (r.ok) {
                                        // Не закрываем автоматически — пользователь читает текст и жмёт «Закрыть».
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !loading && login.isNotBlank() && email.isNotBlank()
                        ) {
                            Text(if (loading) "Отправка..." else "Отправить")
                        }
                    }
                    ForgotPasswordFlowStep.EnterOtp -> {
                        Text(
                            "Введите 6 цифр из письма для «${login.trim()}». После проверки откроется экран нового пароля.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            email.trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        SixDigitOtpField(
                            value = otp,
                            onValueChange = { otp = it },
                            enabled = !loading
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    step = ForgotPasswordFlowStep.EnterCredentials
                                    otp = ""
                                    status = ""
                                },
                                enabled = !loading
                            ) { Text("Назад") }
                            Button(
                                onClick = {
                                    scope.launch {
                                        loading = true
                                        status = ""
                                        val r = onVerifyResetCode(login.trim(), email.trim(), otp.trim())
                                        loading = false
                                        statusOk = r.ok
                                        if (r.message.isNotBlank()) status = r.message
                                        if (r.ok) {
                                            status = ""
                                            step = ForgotPasswordFlowStep.NewPassword
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !loading && otp.length == 6
                            ) { Text(if (loading) "Проверка..." else "Далее") }
                        }
                    }
                    ForgotPasswordFlowStep.NewPassword -> {
                        Text(
                            "Задайте новый пароль для входа.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Новый пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = readableFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = repeatPassword,
                            onValueChange = { repeatPassword = it },
                            label = { Text("Повторить пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = repeatPassword.isNotBlank() && repeatPassword != newPassword,
                            colors = readableFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    step = ForgotPasswordFlowStep.EnterOtp
                                    status = ""
                                },
                                enabled = !loading
                            ) { Text("Назад") }
                            Button(
                                onClick = {
                                    scope.launch {
                                        loading = true
                                        val r = onCompletePasswordReset(
                                            login.trim(),
                                            email.trim(),
                                            otp.trim(),
                                            newPassword.trim()
                                        )
                                        loading = false
                                        statusOk = r.ok
                                        status = r.message
                                        if (r.ok) {
                                            delay(600)
                                            close()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !loading &&
                                    newPassword.length >= 6 &&
                                    newPassword == repeatPassword
                            ) {
                                Text(if (loading) "Сохранение..." else "Сменить пароль")
                            }
                        }
                    }
                }
                if (status.isNotBlank()) {
                    Text(
                        status,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (statusOk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                TextButton(
                    onClick = close,
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Закрыть") }
            }
            }
        }
    }
}

private fun persistPickedProfilePhoto(context: Context, source: Uri, username: String): String? {
    return runCatching {
        val dir = File(context.filesDir, "profile_photos").apply { mkdirs() }
        val safe = username.trim().ifBlank { "user" }.replace(Regex("[^\\p{L}\\p{N}_-]"), "_")
        val dest = File(dir, "${safe}_profile_photo")
        context.contentResolver.openInputStream(source)?.use { input ->
            FileOutputStream(dest).use { output -> input.copyTo(output) }
        } ?: return@runCatching null
        dest.absolutePath
    }.getOrNull()
}

private fun profilePhotoImageModel(photoPathOrUri: String): Any {
    val t = photoPathOrUri.trim()
    if (t.startsWith("/")) return File(t)
    if (t.startsWith("file://")) {
        val path = runCatching { t.toUri().path }.getOrNull()
        if (!path.isNullOrBlank()) return File(path)
    }
    return t
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    initialProfile: UserProfile?,
    onSave: (UserProfile) -> Unit
) {
    var photoUri by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var birthDate by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf(TextFieldValue("")) }
    var relativeContact by remember { mutableStateOf(TextFieldValue("")) }
    var bloodType by remember { mutableStateOf(TextFieldValue("")) }
    var chronicDiseases by remember { mutableStateOf(TextFieldValue("")) }
    var regularMedications by remember { mutableStateOf(TextFieldValue("")) }
    var weight by remember { mutableStateOf(TextFieldValue("")) }
    var height by remember { mutableStateOf(TextFieldValue("")) }
    var notificationMode by remember { mutableStateOf(TextFieldValue("Системный звук")) }
    var customNotificationSound by remember { mutableStateOf(TextFieldValue("")) }
    var globalSilenceEnabled by remember { mutableStateOf(false) }
    var badHabits by remember { mutableStateOf(TextFieldValue("")) }
    var allergies by remember { mutableStateOf(TextFieldValue("")) }
    var complexOperations by remember { mutableStateOf(TextFieldValue("")) }
    var hasChronicDiseases by remember { mutableStateOf(false) }
    var hasRegularMedications by remember { mutableStateOf(false) }
    var hasDiagnoses by remember { mutableStateOf(false) }
    var hasAllergies by remember { mutableStateOf(false) }
    var hasComplexOperations by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    var bloodExpanded by remember { mutableStateOf(false) }
    var notificationModeExpanded by remember { mutableStateOf(false) }
    var selectedSoundUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSoundName by remember { mutableStateOf("") }
    var customSoundDurationMs by remember { mutableLongStateOf(0L) }
    var pendingSoundPickerLaunch by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val calendar = remember { Calendar.getInstance() }
    val genderOptions = remember { listOf("Мужской", "Женский") }
    val bloodOptions = remember {
        listOf(
            "I (0) Rh+",
            "I (0) Rh-",
            "II (A) Rh+",
            "II (A) Rh-",
            "III (B) Rh+",
            "III (B) Rh-",
            "IV (AB) Rh+",
            "IV (AB) Rh-"
        )
    }
    val notificationOptions = remember { listOf("Системный звук", "Загрузить свой") }
    val readableFieldColors = readableOutlinedFieldColors()
    val scheme = MaterialTheme.colorScheme

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val copied = withContext(Dispatchers.IO) {
                persistPickedProfilePhoto(context, uri, username)
            }
            if (copied != null) {
                photoUri = copied
            } else {
                android.widget.Toast.makeText(
                    context,
                    "Не удалось сохранить фото",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    val customSoundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        val duration = runCatching {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            } finally {
                retriever.release()
            }
        }.getOrDefault(0L)
        val firstChunkMs = duration.coerceAtMost(10_000L)
        selectedSoundUri = uri
        selectedSoundName = uri.lastPathSegment.orEmpty().substringAfterLast('/').ifBlank { "Выбранный звук" }
        customSoundDurationMs = firstChunkMs
        customNotificationSound = TextFieldValue(uri.toString())
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingSoundPickerLaunch) {
            customSoundLauncher.launch(arrayOf("audio/*"))
            pendingSoundPickerLaunch = false
        } else if (!granted) {
            android.widget.Toast.makeText(
                context,
                "Разрешение на доступ к аудио отклонено",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            pendingSoundPickerLaunch = false
        }
    }

    LaunchedEffect(initialProfile) {
        photoUri = initialProfile?.photoUri.orEmpty()
        fullName = TextFieldValue(initialProfile?.fullName.orEmpty())
        birthDate = TextFieldValue(initialProfile?.birthDate.orEmpty())
        gender = TextFieldValue(initialProfile?.gender.orEmpty())
        relativeContact = TextFieldValue(initialProfile?.relativeContact.orEmpty())
        bloodType = TextFieldValue(initialProfile?.bloodType.orEmpty())
        chronicDiseases = TextFieldValue(initialProfile?.chronicDiseases.orEmpty())
        regularMedications = TextFieldValue(initialProfile?.regularMedications.orEmpty())
        weight = TextFieldValue(initialProfile?.weight.orEmpty())
        height = TextFieldValue(initialProfile?.height.orEmpty())
        notificationMode = TextFieldValue(initialProfile?.notificationMode ?: "Системный звук")
        customNotificationSound = TextFieldValue(initialProfile?.customNotificationSound.orEmpty())
        if (initialProfile?.customNotificationSound.orEmpty().isNotBlank()) {
            selectedSoundUri = initialProfile?.customNotificationSound.orEmpty().toUri()
            selectedSoundName = selectedSoundUri?.lastPathSegment.orEmpty().substringAfterLast('/')
        } else {
            selectedSoundUri = null
            selectedSoundName = ""
        }
        customSoundDurationMs = 0L
        globalSilenceEnabled = initialProfile?.globalSilenceEnabled ?: false
        badHabits = TextFieldValue(initialProfile?.badHabits.orEmpty())
        allergies = TextFieldValue(initialProfile?.allergies.orEmpty())
        complexOperations = TextFieldValue(initialProfile?.complexOperations.orEmpty())
        hasChronicDiseases = chronicDiseases.text.isNotBlank() && !chronicDiseases.text.equals("Нет", ignoreCase = true)
        hasRegularMedications = regularMedications.text.isNotBlank() && !regularMedications.text.equals("Нет", ignoreCase = true)
        hasDiagnoses = badHabits.text.isNotBlank() && !badHabits.text.equals("Нет", ignoreCase = true)
        hasAllergies = allergies.text.isNotBlank() && !allergies.text.equals("Нет", ignoreCase = true)
        hasComplexOperations = complexOperations.text.isNotBlank() && !complexOperations.text.equals("Нет", ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val profileSectionLabelColor = scheme.primary
        Text("Личный профиль: $username", style = MaterialTheme.typography.headlineSmall)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = scheme.surfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (photoUri.isNotBlank()) {
                    AsyncImage(
                        model = profilePhotoImageModel(photoUri),
                        contentDescription = "Фото профиля",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = {
                        pickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Загрузить фото с устройства")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ФИО (необязательно)") },
                    colors = readableFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },
                    label = { Text("Дата рождения") },
                    readOnly = true,
                    colors = readableFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                birthDate = TextFieldValue(
                                    String.format(
                                        Locale.getDefault(),
                                        "%02d.%02d.%04d",
                                        dayOfMonth,
                                        month + 1,
                                        year
                                    )
                                )
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выбрать дату рождения")
                }
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Пол") },
                        colors = readableFieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = TextFieldValue(option)
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = relativeContact,
                    onValueChange = {
                        val digitsOnly = it.text.filter(Char::isDigit)
                        relativeContact = TextFieldValue(digitsOnly, selection = TextRange(digitsOnly.length))
                    },
                    label = { Text("Контакт близкого родственника") },
                    colors = readableFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = bloodExpanded,
                    onExpandedChange = { bloodExpanded = !bloodExpanded }
                ) {
                    OutlinedTextField(
                        value = bloodType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Группа крови") },
                        colors = readableFieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = bloodExpanded,
                        onDismissRequest = { bloodExpanded = false }
                    ) {
                        bloodOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    bloodType = TextFieldValue(option)
                                    bloodExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Хронические заболевания",
                    color = profileSectionLabelColor,
                    fontWeight = FontWeight.SemiBold
                )
                YesNoSegmentedSelector(
                    selectedYes = hasChronicDiseases,
                    onYes = {
                        hasChronicDiseases = true
                        if (chronicDiseases.text.equals("Нет", ignoreCase = true)) {
                            chronicDiseases = TextFieldValue("")
                        }
                    },
                    onNo = {
                        hasChronicDiseases = false
                        chronicDiseases = TextFieldValue("Нет")
                    }
                )
                if (hasChronicDiseases) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = chronicDiseases,
                        onValueChange = { chronicDiseases = it },
                        label = { Text("Укажите хронические заболевания") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Регулярные препараты",
                    color = profileSectionLabelColor,
                    fontWeight = FontWeight.SemiBold
                )
                YesNoSegmentedSelector(
                    selectedYes = hasRegularMedications,
                    onYes = {
                        hasRegularMedications = true
                        if (regularMedications.text.equals("Нет", ignoreCase = true)) {
                            regularMedications = TextFieldValue("")
                        }
                    },
                    onNo = {
                        hasRegularMedications = false
                        regularMedications = TextFieldValue("Нет")
                    }
                )
                if (hasRegularMedications) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = regularMedications,
                        onValueChange = { regularMedications = it },
                        label = { Text("Укажите регулярные препараты") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        val digitsOnly = it.text.filter(Char::isDigit)
                        weight = TextFieldValue(digitsOnly, selection = TextRange(digitsOnly.length))
                    },
                    label = { Text("Вес") },
                    suffix = { Text("кг") },
                    colors = readableFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = height,
                    onValueChange = {
                        val digitsOnly = it.text.filter(Char::isDigit)
                        height = TextFieldValue(digitsOnly, selection = TextRange(digitsOnly.length))
                    },
                    label = { Text("Рост") },
                    suffix = { Text("см") },
                    colors = readableFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = notificationModeExpanded,
                    onExpandedChange = { notificationModeExpanded = !notificationModeExpanded }
                ) {
                    OutlinedTextField(
                        value = notificationMode,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Уведомления") },
                        colors = readableFieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = notificationModeExpanded) },
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = notificationModeExpanded,
                        onDismissRequest = { notificationModeExpanded = false }
                    ) {
                        notificationOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    notificationMode = TextFieldValue(option)
                                    notificationModeExpanded = false
                                    if (option == "Системный звук") {
                                        customNotificationSound = TextFieldValue("")
                                        selectedSoundUri = null
                                        selectedSoundName = ""
                                        customSoundDurationMs = 0L
                                    }
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (notificationMode.text == "Загрузить свой") {
                    OutlinedTextField(
                        value = TextFieldValue(
                            selectedSoundName.ifBlank { "Звук не выбран" }
                        ),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Свой звук уведомлений") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_AUDIO
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                                pendingSoundPickerLaunch = true
                                audioPermissionLauncher.launch(permission)
                            } else {
                                customSoundLauncher.launch(arrayOf("audio/*"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Выбрать звук из папки аудио")
                    }
                    if (customSoundDurationMs > 0L) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Для уведомления используется первый фрагмент: ${customSoundDurationMs / 1000} сек.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val uri = selectedSoundUri ?: return@Button
                                runCatching {
                                    val player = MediaPlayer().apply {
                                        setDataSource(context, uri)
                                        prepare()
                                        start()
                                    }
                                    val previewDuration = customSoundDurationMs.coerceAtMost(10_000L)
                                    android.os.Handler(context.mainLooper).postDelayed({
                                        runCatching {
                                            if (player.isPlaying) player.stop()
                                            player.release()
                                        }
                                    }, previewDuration)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Прослушать первый фрагмент")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Глобальная тишина (Не беспокоить)",
                        color = profileSectionLabelColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(checked = globalSilenceEnabled, onCheckedChange = { globalSilenceEnabled = it })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Диагнозы",
                    color = profileSectionLabelColor,
                    fontWeight = FontWeight.SemiBold
                )
                YesNoSegmentedSelector(
                    selectedYes = hasDiagnoses,
                    onYes = {
                        hasDiagnoses = true
                        if (badHabits.text.equals("Нет", ignoreCase = true)) {
                            badHabits = TextFieldValue("")
                        }
                    },
                    onNo = {
                        hasDiagnoses = false
                        badHabits = TextFieldValue("Нет")
                    }
                )
                if (hasDiagnoses) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = badHabits,
                        onValueChange = { badHabits = it },
                        label = { Text("Укажите диагнозы / особенности") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Аллергии",
                    color = profileSectionLabelColor,
                    fontWeight = FontWeight.SemiBold
                )
                YesNoSegmentedSelector(
                    selectedYes = hasAllergies,
                    onYes = {
                        hasAllergies = true
                        if (allergies.text.equals("Нет", ignoreCase = true)) {
                            allergies = TextFieldValue("")
                        }
                    },
                    onNo = {
                        hasAllergies = false
                        allergies = TextFieldValue("Нет")
                    }
                )
                if (hasAllergies) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Укажите аллергии") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сложные операции",
                    color = profileSectionLabelColor,
                    fontWeight = FontWeight.SemiBold
                )
                YesNoSegmentedSelector(
                    selectedYes = hasComplexOperations,
                    onYes = {
                        hasComplexOperations = true
                        if (complexOperations.text.equals("Нет", ignoreCase = true)) {
                            complexOperations = TextFieldValue("")
                        }
                    },
                    onNo = {
                        hasComplexOperations = false
                        complexOperations = TextFieldValue("Нет")
                    }
                )
                if (hasComplexOperations) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = complexOperations,
                        onValueChange = { complexOperations = it },
                        label = { Text("Укажите сложные операции") },
                        colors = readableFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        scope.launch {
                            var resolvedPhoto = photoUri
                            if (resolvedPhoto.isNotBlank() && resolvedPhoto.startsWith("content://")) {
                                val copied = withContext(Dispatchers.IO) {
                                    persistPickedProfilePhoto(context, resolvedPhoto.toUri(), username)
                                }
                                if (copied == null) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Не удалось сохранить фото на устройство. Попробуйте выбрать другое изображение.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                    return@launch
                                }
                                resolvedPhoto = copied
                                photoUri = copied
                            }
                            onSave(
                                UserProfile(
                                    username = username,
                                    photoUri = resolvedPhoto,
                                    fullName = fullName.text.trim(),
                                    birthDate = birthDate.text.trim(),
                                    gender = gender.text.trim(),
                                    relativeContact = relativeContact.text.trim(),
                                    bloodType = bloodType.text.trim(),
                                    chronicDiseases = if (hasChronicDiseases) chronicDiseases.text.trim().ifBlank { "Да" } else "Нет",
                                    regularMedications = if (hasRegularMedications) regularMedications.text.trim().ifBlank { "Да" } else "Нет",
                                    weight = weight.text.trim(),
                                    height = height.text.trim(),
                                    notificationMode = notificationMode.text.trim().ifBlank { "Системный звук" },
                                    customNotificationSound = customNotificationSound.text.trim(),
                                    globalSilenceEnabled = globalSilenceEnabled,
                                    badHabits = if (hasDiagnoses) badHabits.text.trim().ifBlank { "Да" } else "Нет",
                                    allergies = if (hasAllergies) allergies.text.trim().ifBlank { "Да" } else "Нет",
                                    complexOperations = if (hasComplexOperations) complexOperations.text.trim().ifBlank { "Да" } else "Нет"
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить профиль и открыть главный экран")
                }
            }
        }
    }
}

@Composable
private fun YesNoSegmentedSelector(
    selectedYes: Boolean,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selectedYes,
            onClick = onYes,
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = scheme.primary,
                activeContentColor = scheme.onPrimary,
                inactiveContainerColor = scheme.surfaceVariant,
                inactiveContentColor = scheme.onSurfaceVariant
            )
        ) {
            Text("Да")
        }
        SegmentedButton(
            selected = !selectedYes,
            onClick = onNo,
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = scheme.secondary,
                activeContentColor = scheme.onSecondary,
                inactiveContainerColor = scheme.surfaceVariant,
                inactiveContentColor = scheme.onSurfaceVariant
            )
        ) {
            Text("Нет")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreen(
    modifier: Modifier = Modifier,
    username: String,
    profile: UserProfile?,
    plans: List<TreatmentPlan>,
    wellbeingEntries: Map<String, WellbeingEntry>,
    isReportReady: Boolean,
    isAdmin: Boolean,
    patientAssignmentId: Long?,
    tokenProvider: () -> String,
    onOpenAdminPanel: () -> Unit,
    onStartTreatment: () -> Unit,
    onStopTreatment: () -> Unit,
    onOpenDiary: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAssistant: () -> Unit,
    assistantTourActive: Boolean,
    assistantSpotlight: AssistantTourSpotlight,
    onAssistantSpotlightBounds: (Rect?) -> Unit,
    persistedScannedMedicines: List<ScannedMedicineCandidate>,
    onRefreshHomeMenu: () -> Unit,
    onPersistScannedMedicine: (ScannedMedicineCandidate) -> Boolean,
    onDeleteScannedMedicine: (ScannedMedicineCandidate) -> Unit,
    onRenameScannedMedicine: (ScannedMedicineCandidate, String) -> Boolean,
    onExportPdf: (Uri) -> Unit,
    onDeleteSavedMedicine: (SavedMedicine) -> Unit
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val uiMetrics = rememberUiMetrics()

    val context = LocalContext.current
    val conflicts = remember(profile) { emptyList<String>() }
    val pulse = rememberInfiniteTransition(label = "warning-pulse")
    val animatedWarningColor by pulse.animateColor(
        initialValue = scheme.errorContainer.copy(alpha = 0.85f),
        targetValue = scheme.error.copy(alpha = 0.35f),
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "warning-color"
    )
    var lastConflictFingerprint by remember { mutableStateOf("") }
    var dangerPulseActive by remember { mutableStateOf(false) }
    var showNextPlanDialog by remember { mutableStateOf(false) }
    var nextPlanPreview by remember { mutableStateOf<TreatmentPlan?>(null) }
    var showReportBlockedDialog by remember { mutableStateOf(false) }
    var showReportOptionsDialog by remember { mutableStateOf(false) }
    var showStopConfirmDialog by remember { mutableStateOf(false) }
    var showNoTreatmentDialog by remember { mutableStateOf(false) }
    var selectedBottomTab by remember { mutableStateOf("plan") }
    var cabinetDetail by remember { mutableStateOf<SavedMedicine?>(null) }
    var showScanErrorDialog by remember { mutableStateOf(false) }
    var scanErrorText by remember { mutableStateOf("") }
    var isBarcodeScanInProgress by remember { mutableStateOf(false) }
    var scannedMedicineCode by remember { mutableStateOf<ScannedMedicineCodeInfo?>(null) }
    var scannedCandidate by remember { mutableStateOf<ScannedMedicineCandidate?>(null) }
    var medicineToEdit by remember { mutableStateOf<ScannedMedicineCandidate?>(null) }
    var editableMedicineName by remember { mutableStateOf("") }
    val homeScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var homeSnackbarKind by remember { mutableStateOf(HomeSnackbarKind.Added) }
    var isRefreshingHome by remember { mutableStateOf(false) }
    var scannedSortOption by remember { mutableStateOf(ScannedMedicineSortOption.ByDate) }
    var scannedSortMenuExpanded by remember { mutableStateOf(false) }
    val displayedScannedMedicines = when (scannedSortOption) {
        ScannedMedicineSortOption.ByDate -> {
            persistedScannedMedicines.sortedByDescending { it.scannedAtMillis }
        }
        ScannedMedicineSortOption.ByAlphabet -> {
            persistedScannedMedicines.sortedBy { it.name.lowercase(Locale.getDefault()) }
        }
    }
    val completedCourses = remember(plans) { homeCompletedCourses(plans) }
    val showHomeLibrary = persistedScannedMedicines.isNotEmpty() || completedCourses.isNotEmpty()
    val completedCourseExpanded = remember { mutableStateMapOf<Long, Boolean>() }
    fun showHomeSnackbar(message: String, kind: HomeSnackbarKind) {
        homeScope.launch {
            homeSnackbarKind = kind
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    val barcodeScannerOptions = remember {
        GmsBarcodeScannerOptions.Builder()
            .build()
    }
    val barcodeScanner = remember(context) {
        GmsBarcodeScanning.getClient(context, barcodeScannerOptions)
    }
    fun onBarcodeRawScanned(rawValue: String) {
        if (rawValue.isBlank()) {
            showScanErrorDialog = true
            scanErrorText = "Сканирование выполнено, но данные штрихкода пустые."
        } else {
            val parsed = parseScannedMedicineCode(rawValue)
            val barcodeForLookup = parsed.gtin ?: rawValue.filter(Char::isDigit).takeIf { it.length >= 8 }
            if (barcodeForLookup == null) {
                val fallback = fallbackScannedMedicineCandidate(rawValue)
                if (onPersistScannedMedicine(fallback)) {
                    onRefreshHomeMenu()
                    showHomeSnackbar("Препарат добавлен.", HomeSnackbarKind.Added)
                }
                scannedCandidate = null
                scannedMedicineCode = null
                return
            }
            homeScope.launch {
                val candidate = resolveScannedMedicineCandidate(barcodeForLookup)
                if (candidate != null) {
                    scannedCandidate = candidate
                    scannedMedicineCode = null
                } else {
                    val fallback = fallbackScannedMedicineCandidate(barcodeForLookup)
                    if (onPersistScannedMedicine(fallback)) {
                        onRefreshHomeMenu()
                        showHomeSnackbar("Препарат добавлен.", HomeSnackbarKind.Added)
                    }
                    scannedCandidate = null
                    scannedMedicineCode = null
                }
            }
        }
    }
    val zxingScanLauncher = rememberLauncherForActivityResult(
        ScanContract()
    ) { result ->
        isBarcodeScanInProgress = false
        val rawValue = result.contents.orEmpty()
        if (rawValue.isBlank()) {
            showScanErrorDialog = true
            scanErrorText = "Не удалось распознать штрихкод. Попробуйте при хорошем освещении."
        } else {
            onBarcodeRawScanned(rawValue)
        }
    }
    val startBarcodeScan: () -> Unit = {
        barcodeScanner
            .startScan()
            .addOnSuccessListener { barcode ->
                isBarcodeScanInProgress = false
                onBarcodeRawScanned(barcode.rawValue.orEmpty())
            }
            .addOnFailureListener {
                runCatching {
                    val options = ScanOptions()
                        .setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
                        .setPrompt("Наведите камеру на штрихкод упаковки")
                        .setBeepEnabled(true)
                        .setBarcodeImageEnabled(false)
                        .setOrientationLocked(false)
                    zxingScanLauncher.launch(options)
                }.onFailure { error ->
                    isBarcodeScanInProgress = false
                    showScanErrorDialog = true
                    scanErrorText = error.localizedMessage
                        ?: "Не удалось открыть сканер. Проверьте камеру и сервисы Google Play."
                }
            }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            isBarcodeScanInProgress = false
            showScanErrorDialog = true
            scanErrorText = "Для сканирования нужен доступ к камере."
            return@rememberLauncherForActivityResult
        }
        startBarcodeScan()
    }
    val createPdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) onExportPdf(uri)
    }
    LaunchedEffect(conflicts) {
        val fingerprint = conflicts.joinToString("|")
        if (conflicts.isNotEmpty() && fingerprint != lastConflictFingerprint) {
            triggerMicroVibration(context)
            dangerPulseActive = true
            delay(1600)
            dangerPulseActive = false
        }
        lastConflictFingerprint = fingerprint
    }
    val criticalScale by animateFloatAsState(
        targetValue = if (dangerPulseActive) 1.05f else 1f,
        animationSpec = tween(220),
        label = "critical-scale"
    )
    val homeTourPulse = rememberInfiniteTransition(label = "home-tour-pulse")
    val homeTourScale by homeTourPulse.animateFloat(
        initialValue = 1f,
        targetValue = if (assistantTourActive) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home-tour-scale"
    )
    fun spotlightScale(target: AssistantTourSpotlight) =
        if (assistantTourActive && assistantSpotlight == target) homeTourScale else 1f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                val isAddedState = homeSnackbarKind == HomeSnackbarKind.Added
                val snackbarContainerColor = if (isAddedState) app.snackbarSuccess else app.snackbarInfo
                val snackbarIcon = if (isAddedState) {
                    android.R.drawable.checkbox_on_background
                } else {
                    android.R.drawable.ic_menu_edit
                }
                Snackbar(
                    containerColor = snackbarContainerColor.copy(alpha = 0.95f),
                    contentColor = app.onSnackbar,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = snackbarIcon),
                            contentDescription = null,
                            tint = app.onSnackbar
                        )
                        Text(
                            text = snackbarData.visuals.message,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        bottomBar = {
            HomeBottomNavBar(
                selectedTab = selectedBottomTab,
                onTreatment = {
                    selectedBottomTab = "plan"
                    onStartTreatment()
                },
                onDiary = {
                    selectedBottomTab = "diary"
                    onOpenDiary()
                },
                onNext = {
                    selectedBottomTab = "next"
                    nextPlanPreview = findNextPlan(plans)
                    showNextPlanDialog = true
                },
                planTabModifier = Modifier.assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeNavTreatment,
                    pulseScale = spotlightScale(AssistantTourSpotlight.HomeNavTreatment),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                ),
                diaryTabModifier = Modifier.assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeNavDiary,
                    pulseScale = spotlightScale(AssistantTourSpotlight.HomeNavDiary),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                ),
                nextTabModifier = Modifier.assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeNavNext,
                    pulseScale = spotlightScale(AssistantTourSpotlight.HomeNavNext),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = isRefreshingHome,
                onRefresh = {
                    homeScope.launch {
                        isRefreshingHome = true
                        onRefreshHomeMenu()
                        delay(250)
                        isRefreshingHome = false
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(app.heroGradient)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .padding(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                Text(
                    "Добро пожаловать, $username",
                    style = MaterialTheme.typography.headlineSmall,
                    color = app.onHero
                )
                Text(
                    "Управляйте планами лечения и отслеживайте самочувствие.",
                    color = app.onHeroMuted
                )
            AnimatedVisibility(
                visible = isAdmin,
                enter = fadeIn(tween(380)) + slideInVertically(tween(380)) { it / 8 }
            ) {
                Button(
                    onClick = onOpenAdminPanel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.tertiaryContainer,
                        contentColor = scheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Админ-панель")
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (plans.isEmpty()) {
                                showNoTreatmentDialog = true
                            } else {
                                showStopConfirmDialog = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(uiMetrics.homeActionButtonHeight)
                            .assistantTourTarget(
                                active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeStop,
                                pulseScale = spotlightScale(AssistantTourSpotlight.HomeStop),
                                shape = RoundedCornerShape(16.dp),
                                borderColor = scheme.primary,
                                onBounds = { onAssistantSpotlightBounds(it) }
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.tertiaryContainer,
                            contentColor = scheme.onTertiaryContainer
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.PanTool,
                                contentDescription = "Стоп",
                                modifier = Modifier.size(uiMetrics.homeActionIconSize)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Стоп",
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Button(
                        onClick = {
                            onOpenDiary()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(uiMetrics.homeActionButtonHeight)
                            .assistantTourTarget(
                                active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeDiaryAction,
                                pulseScale = spotlightScale(AssistantTourSpotlight.HomeDiaryAction),
                                shape = RoundedCornerShape(16.dp),
                                borderColor = scheme.primary,
                                onBounds = { onAssistantSpotlightBounds(it) }
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.primaryContainer,
                            contentColor = scheme.onPrimaryContainer
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.EditNote,
                                contentDescription = "Дневник",
                                modifier = Modifier.size(uiMetrics.homeActionIconSize)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Дневник",
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (isReportReady) {
                                showReportOptionsDialog = true
                            } else {
                                showReportBlockedDialog = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(uiMetrics.homeActionButtonHeight)
                            .assistantTourTarget(
                                active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeReport,
                                pulseScale = spotlightScale(AssistantTourSpotlight.HomeReport),
                                shape = RoundedCornerShape(16.dp),
                                borderColor = scheme.primary,
                                onBounds = { onAssistantSpotlightBounds(it) }
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scheme.secondaryContainer,
                            contentColor = scheme.onSecondaryContainer
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = "Отчет",
                                modifier = Modifier.size(uiMetrics.homeActionIconSize)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Отчет",
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = showHomeLibrary,
                enter = fadeIn(tween(400)) + slideInVertically(tween(420), initialOffsetY = { it / 5 })
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Медиатека",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = scheme.onSurface
                    )
                    if (persistedScannedMedicines.isNotEmpty()) {
                        HomeExpandableFolder(
                            title = "Отсканированные упаковки",
                            subtitle = "${persistedScannedMedicines.size} шт.",
                            icon = Icons.Filled.QrCode2,
                            accent = scheme.primary,
                            onAccent = scheme.onPrimaryContainer,
                            surfaceColor = scheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box {
                                    IconButton(onClick = { scannedSortMenuExpanded = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.Sort,
                                            contentDescription = "Сортировка",
                                            tint = scheme.onPrimaryContainer
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = scannedSortMenuExpanded,
                                        onDismissRequest = { scannedSortMenuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("По дате сканирования") },
                                            onClick = {
                                                scannedSortOption = ScannedMedicineSortOption.ByDate
                                                scannedSortMenuExpanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("По алфавиту") },
                                            onClick = {
                                                scannedSortOption = ScannedMedicineSortOption.ByAlphabet
                                                scannedSortMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            displayedScannedMedicines.forEachIndexed { index, confirmed ->
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(confirmed.name, color = scheme.onPrimaryContainer, fontWeight = FontWeight.Medium)
                                    Text(
                                        "Штрихкод: ${confirmed.barcode}",
                                        color = scheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        "Дата: ${formatScannedMedicineDateTime(confirmed.scannedAtMillis)}",
                                        color = scheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(confirmed.infoUrl))
                                                runCatching { context.startActivity(intent) }
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("Перейти") }
                                        Button(
                                            onClick = {
                                                medicineToEdit = confirmed
                                                editableMedicineName = confirmed.name
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = scheme.surface,
                                                contentColor = scheme.primary
                                            )
                                        ) { Text("Изменить") }
                                    }
                                    OutlinedButton(
                                        onClick = { onDeleteScannedMedicine(confirmed) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = scheme.error)
                                    ) { Text("Удалить") }
                                }
                                if (index < displayedScannedMedicines.lastIndex) {
                                    HorizontalDivider(color = scheme.outlineVariant)
                                }
                            }
                        }
                    }
                    if (completedCourses.isNotEmpty()) {
                        HomeExpandableFolder(
                            title = "Завершённое лечение",
                            subtitle = "${completedCourses.size} ${if (completedCourses.size == 1) "курс" else "курса"}",
                            icon = Icons.Filled.TaskAlt,
                            accent = scheme.tertiary,
                            onAccent = scheme.onTertiaryContainer,
                            surfaceColor = scheme.tertiaryContainer
                        ) {
                            completedCourses.forEach { course ->
                                val expanded = completedCourseExpanded[course.folderKey] ?: false
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            completedCourseExpanded[course.folderKey] = !expanded
                                        },
                                    colors = CardDefaults.cardColors(containerColor = scheme.surface.copy(alpha = 0.55f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    course.title,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = scheme.onSurface
                                                )
                                                Text(
                                                    "${course.plans.size} записей",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = scheme.onSurfaceVariant
                                                )
                                            }
                                            Icon(
                                                imageVector = if (expanded) {
                                                    Icons.Filled.KeyboardArrowUp
                                                } else {
                                                    Icons.Filled.KeyboardArrowDown
                                                },
                                                contentDescription = null,
                                                tint = scheme.tertiary
                                            )
                                        }
                                        AnimatedVisibility(visible = expanded) {
                                            Column(
                                                modifier = Modifier.padding(top = 8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                course.plans
                                                    .groupBy { it.startDate }
                                                    .toList()
                                                    .sortedBy { (day, _) -> parseDateTimeMillis(day, "00:00") }
                                                    .forEach { (day, dayPlans) ->
                                                        Text(
                                                            "День $day",
                                                            fontWeight = FontWeight.Medium,
                                                            color = scheme.primary
                                                        )
                                                        dayPlans.forEach { plan ->
                                                            Text(
                                                                "• ${plan.medicineName} — ${plan.dosage}, ${plan.reminderTime}",
                                                                color = scheme.onSurface,
                                                                style = MaterialTheme.typography.bodySmall
                                                            )
                                                            if (plan.notes.isNotBlank()) {
                                                                Text(
                                                                    "  ${plan.notes}",
                                                                    color = scheme.onSurfaceVariant,
                                                                    style = MaterialTheme.typography.labelSmall
                                                                )
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = conflicts.isNotEmpty(),
                enter = fadeIn() + expandVertically()
            ) {
                Card(
                    modifier = Modifier.scale(criticalScale),
                    colors = CardDefaults.cardColors(containerColor = animatedWarningColor.copy(alpha = 0.92f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                                contentDescription = "Предупреждение"
                            )
                            Text(
                                "Это лекарство может быть небезопасно для пациента",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Обнаружены конфликты: ${conflicts.joinToString()}")
                        Text("Перед приемом препарата обязательно проконсультируйтесь с врачом.")
                    }
                }
            }
                }
            }
            AnimatedAssistantIconButton(
                onClick = onOpenAssistant,
                imageAsset = "assistant_character.png",
                size = 54.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, bottom = 16.dp)
                    .assistantTourTarget(
                        active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeUmnik,
                        pulseScale = spotlightScale(AssistantTourSpotlight.HomeUmnik),
                        shape = CircleShape,
                        borderColor = scheme.primary,
                        onBounds = { onAssistantSpotlightBounds(it) }
                    )
            )
            AnimatedAssistantIconButton(
                onClick = onOpenProfile,
                imageAsset = "profile_button_photo.png",
                size = 54.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 16.dp)
                    .assistantTourTarget(
                        active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeProfile,
                        pulseScale = spotlightScale(AssistantTourSpotlight.HomeProfile),
                        shape = CircleShape,
                        borderColor = scheme.primary,
                        onBounds = { onAssistantSpotlightBounds(it) }
                    ),
                contentDescription = "Профиль"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .assistantTourTarget(
                        active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.HomeScanner,
                        pulseScale = spotlightScale(AssistantTourSpotlight.HomeScanner),
                        shape = CircleShape,
                        borderColor = Color.Transparent,
                        onBounds = { onAssistantSpotlightBounds(it) }
                    )
            ) {
                MedScanIconButton(
                    onClick = {
                        if (!isBarcodeScanInProgress) {
                            isBarcodeScanInProgress = true
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                startBarcodeScan()
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    enabled = true,
                    isBusy = isBarcodeScanInProgress
                )
            }
        }
    }

    cabinetDetail?.let { med ->
        AlertDialog(
            onDismissRequest = { cabinetDetail = null },
            title = { Text(med.name, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(
                    Modifier
                        .heightIn(max = 440.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    CabinetDetailBlock("Дозировка и применение", med.dosage)
                    CabinetDetailBlock("Действующее вещество", med.activeSubstance)
                    CabinetDetailBlock("Производитель", med.manufacturer)
                    CabinetDetailBlock("Показания", med.useText)
                    CabinetDetailBlock("Противопоказания", med.contraindications)
                    CabinetDetailBlock("Побочные эффекты", med.sideEffects)
                    CabinetDetailBlock("Предупреждения", med.warnings)
                    Text(
                        "Официальные инструкции: портал ГРЛС Минздрава РФ. Текст в приложении — справочный.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { cabinetDetail = null }) {
                    Text("Закрыть")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteSavedMedicine(med)
                        cabinetDetail = null
                    }
                ) {
                    Text("Удалить из списка", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    if (showNextPlanDialog) {
        AlertDialog(
            onDismissRequest = { showNextPlanDialog = false },
            title = { Text("Следующая таблетка") },
            text = {
                val next = nextPlanPreview
                if (next == null) {
                    Text("Ближайших приемов пока нет.")
                } else {
                    Text(
                        "Ближайший прием: ${next.medicineName} (${next.dosage})\n" +
                            "Дата: ${next.startDate}\n" +
                            "Время: ${next.reminderTime}"
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showNextPlanDialog = false }) {
                    Text("Ок")
                }
            }
        )
    }
    if (showReportBlockedDialog) {
        AlertDialog(
            onDismissRequest = { showReportBlockedDialog = false },
            title = { Text("Отчет лечения") },
            text = { Text("План не был выполнен, отчет не сформирован!") },
            confirmButton = {
                Button(onClick = { showReportBlockedDialog = false }) {
                    Text("Понятно")
                }
            }
        )
    }
    TreatmentReportOptionsDialog(
        visible = showReportOptionsDialog,
        tokenProvider = tokenProvider,
        assignmentId = patientAssignmentId,
        username = username,
        displayName = profile?.fullName,
        birthDate = profile?.birthDate,
        plans = plans,
        wellbeing = wellbeingEntries,
        onDismiss = { showReportOptionsDialog = false },
        onSavePdf = { createPdfLauncher.launch("treatment_report_$username.pdf") },
        onDone = { msg -> showHomeSnackbar(msg, HomeSnackbarKind.Info) }
    )
    if (showNoTreatmentDialog) {
        AlertDialog(
            onDismissRequest = { showNoTreatmentDialog = false },
            title = { Text("Остановка лечения") },
            text = { Text("У вас отсутствует график лечения!") },
            confirmButton = {
                Button(onClick = { showNoTreatmentDialog = false }) {
                    Text("Понятно")
                }
            }
        )
    }
    if (showStopConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showStopConfirmDialog = false },
            title = { Text("Остановка лечения") },
            text = { Text("Вы уверены что хотите прекратить лечение сейчас?") },
            confirmButton = {
                Button(
                    onClick = {
                        showStopConfirmDialog = false
                        onStopTreatment()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Прекратить")
                }
            },
            dismissButton = {
                Button(onClick = { showStopConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
    scannedCandidate?.let { candidate ->
        AlertDialog(
            onDismissRequest = { scannedCandidate = null },
            title = { Text("Подтвердите препарат") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Найдено совпадение по штрихкоду.")
                    Text("Название упаковки: ${candidate.name}")
                    Text("Штрихкод: ${candidate.barcode}")
                    Text("Подтверждаете, что это правильный препарат?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (onPersistScannedMedicine(candidate)) {
                            onRefreshHomeMenu()
                            showHomeSnackbar("Препарат добавлен.", HomeSnackbarKind.Added)
                        }
                        scannedCandidate = null
                    }
                ) {
                    Text("Да, верно")
                }
            },
            dismissButton = {
                Button(onClick = { scannedCandidate = null }) {
                    Text("Нет")
                }
            }
        )
    }
    scannedMedicineCode?.let { info ->
        AlertDialog(
            onDismissRequest = { scannedMedicineCode = null },
            title = { Text("Результат сканирования") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Название препарата обычно не хранится внутри штрихкода. " +
                            "Сканер показывает то, что реально закодировано."
                    )
                    Text("GTIN (код товара): ${info.gtin ?: "Не найден"}")
                    Text("Срок годности: ${info.expiryDate ?: "Не найден"}")
                    Text("Серия (batch): ${info.batch ?: "Не найдена"}")
                    Text("Серийный номер: ${info.serial ?: "Не найден"}")
                    Text("Сырые данные: ${info.rawValue}")
                }
            },
            confirmButton = {
                Button(onClick = { scannedMedicineCode = null }) {
                    Text("Понятно")
                }
            }
        )
    }
    if (showScanErrorDialog) {
        AlertDialog(
            onDismissRequest = { showScanErrorDialog = false },
            title = { Text("Ошибка сканирования") },
            text = { Text(scanErrorText) },
            confirmButton = {
                Button(onClick = { showScanErrorDialog = false }) {
                    Text("Ок")
                }
            }
        )
    }
    medicineToEdit?.let { candidate ->
        AlertDialog(
            onDismissRequest = { medicineToEdit = null },
            title = { Text("Изменить название") },
            text = {
                OutlinedTextField(
                    value = editableMedicineName,
                    onValueChange = { editableMedicineName = it },
                    singleLine = true,
                    label = { Text("Название упаковки") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = onRenameScannedMedicine(candidate, editableMedicineName)
                        if (ok) {
                            medicineToEdit = null
                            showHomeSnackbar("Название переименовано.", HomeSnackbarKind.Renamed)
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { medicineToEdit = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}


@Composable
private fun PersonalProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    profile: UserProfile?,
    plans: List<TreatmentPlan>,
    onEditProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    assistantTourActive: Boolean,
    assistantSpotlight: AssistantTourSpotlight,
    onAssistantSpotlightBounds: (Rect?) -> Unit,
    onBack: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val uiMetrics = rememberUiMetrics()

    var showMedicalPassport by remember { mutableStateOf(false) }
    var showSos by remember { mutableStateOf(false) }
    val lastDayMeds = remember(plans) { recentMedicationsLast24Hours(plans) }
    val profileTourPulse = rememberInfiniteTransition(label = "profile-tour-pulse")
    val profileTourScale by profileTourPulse.animateFloat(
        initialValue = 1f,
        targetValue = if (assistantTourActive) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "profile-tour-scale"
    )
    fun pScale(t: AssistantTourSpotlight) =
        if (assistantTourActive && assistantSpotlight == t) profileTourScale else 1f

    val fullName = profile?.fullName.orEmpty().ifBlank { "Не указано" }
    val birthDate = profile?.birthDate.orEmpty().ifBlank { "Не указана" }
    val gender = profile?.gender.orEmpty().ifBlank { "Не указан" }
    val relativeContact = profile?.relativeContact.orEmpty().ifBlank { "Не указан" }

    MedSubScreenLayout(
        title = "Личный профиль",
        subtitle = "Данные и быстрые действия",
        onBack = onBack,
        modifier = modifier
    ) {
        MedSurfaceCard(
            modifier = Modifier.assistantTourTarget(
                active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.ProfileInfoCard,
                pulseScale = pScale(AssistantTourSpotlight.ProfileInfoCard),
                shape = RoundedCornerShape(18.dp),
                borderColor = scheme.primary,
                onBounds = { onAssistantSpotlightBounds(it) }
            )
        ) {
            MedProfileHeader(
                login = username,
                fullName = fullName,
                scheme = scheme,
                photoContent = profile?.takeIf { it.photoUri.isNotBlank() }?.let { prof ->
                    {
                        AsyncImage(
                            model = profilePhotoImageModel(prof.photoUri),
                            contentDescription = "Фото профиля",
                            modifier = Modifier
                                .size(uiMetrics.profilePhotoSize)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
            HorizontalDivider(color = scheme.outline.copy(alpha = 0.35f))
            MedProfileField("ФИО", fullName, Icons.Filled.Person, scheme)
            MedProfileField("Дата рождения", birthDate, Icons.Filled.CalendarToday, scheme)
            MedProfileField("Пол", gender, Icons.Filled.Wc, scheme)
            MedProfileField(
                "Контакт родственника",
                relativeContact,
                Icons.Filled.ContactPhone,
                scheme,
                showDivider = false
            )
        }

        MedSectionLabel("Разделы")
        MedMenuCard {
            MedMenuRow(
                title = "Медицинский паспорт",
                subtitle = "Группа крови, аллергии, препараты",
                icon = Icons.Filled.MedicalInformation,
                onClick = { showMedicalPassport = true },
                modifier = Modifier.assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.ProfileMedicalPassport,
                    pulseScale = pScale(AssistantTourSpotlight.ProfileMedicalPassport),
                    shape = RoundedCornerShape(14.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                )
            )
            MedMenuRow(
                title = "Настройки",
                subtitle = "Тема, звук, аккаунт",
                icon = Icons.Filled.Settings,
                onClick = onOpenSettings,
                showDividerBelow = true
            )
            MedMenuRow(
                title = "Редактировать профиль",
                subtitle = "Анкета и медицинские данные",
                icon = Icons.Filled.Edit,
                onClick = onEditProfile,
                showDividerBelow = false
            )
        }

        MedSectionLabel("Экстренно")
        MedSosHighlightCard(
            onClick = { showSos = true },
            modifier = Modifier.assistantTourTarget(
                active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.ProfileSos,
                pulseScale = pScale(AssistantTourSpotlight.ProfileSos),
                shape = RoundedCornerShape(20.dp),
                borderColor = scheme.error,
                onBounds = { onAssistantSpotlightBounds(it) }
            )
        )
    }

    fun displayOrMissing(value: String?, missing: String) =
        value.orEmpty().ifBlank { missing }

    if (showMedicalPassport) {
        MedClinicalInfoDialog(
            title = "Медицинский паспорт",
            subtitle = "Сводка для врача и экстренных случаев",
            headerIcon = Icons.Filled.MedicalInformation,
            accentColor = scheme.primary,
            accentContainer = scheme.primaryContainer,
            rows = listOf(
                MedClinicalInfoRow(
                    "Группа крови",
                    displayOrMissing(profile?.bloodType, "Не указана"),
                    Icons.Filled.Bloodtype
                ),
                MedClinicalInfoRow(
                    "Хронические заболевания",
                    displayOrMissing(profile?.chronicDiseases, "Не указаны"),
                    Icons.Filled.LocalHospital
                ),
                MedClinicalInfoRow(
                    "Аллергии",
                    displayOrMissing(profile?.allergies, "Не указаны"),
                    Icons.Filled.Warning,
                    highlight = !profile?.allergies.isNullOrBlank()
                ),
                MedClinicalInfoRow(
                    "Регулярные препараты",
                    displayOrMissing(profile?.regularMedications, "Не указаны"),
                    Icons.Filled.Medication
                ),
                MedClinicalInfoRow(
                    "Вес",
                    displayOrMissing(profile?.weight, "Не указан"),
                    Icons.Filled.MonitorWeight
                ),
                MedClinicalInfoRow(
                    "Рост",
                    displayOrMissing(profile?.height, "Не указан"),
                    Icons.Filled.Height
                )
            ),
            onDismiss = { showMedicalPassport = false }
        )
    }

    if (showSos) {
        MedClinicalInfoDialog(
            title = "SOS-карточка",
            subtitle = "Покажите медперсоналу при экстренной ситуации",
            headerIcon = Icons.Filled.Emergency,
            accentColor = scheme.error,
            accentContainer = scheme.errorContainer,
            rows = listOf(
                MedClinicalInfoRow(
                    "Диагнозы",
                    displayOrMissing(profile?.badHabits, "Не указаны"),
                    Icons.Filled.LocalHospital
                ),
                MedClinicalInfoRow(
                    "Аллергии",
                    displayOrMissing(profile?.allergies, "Не указаны"),
                    Icons.Filled.Warning,
                    highlight = !profile?.allergies.isNullOrBlank()
                ),
                MedClinicalInfoRow(
                    "Лекарства за 24 часа",
                    lastDayMeds.ifBlank { "Нет данных" },
                    Icons.Filled.Medication
                ),
                MedClinicalInfoRow(
                    "Контакт родственника",
                    displayOrMissing(profile?.relativeContact, "Не указан"),
                    Icons.Filled.ContactPhone,
                    highlight = true
                )
            ),
            onDismiss = { showSos = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    profile: UserProfile?,
    recentMedications24h: String,
    isAdmin: Boolean,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    assistantTourActive: Boolean,
    assistantSpotlight: AssistantTourSpotlight,
    onAssistantSpotlightBounds: (Rect?) -> Unit,
    onSaveSettings: (UserProfile) -> Unit,
    currentAdminKey: String,
    onSaveAdminKey: (String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onOpenSupportChat: () -> Unit,
    onOpenSupportAdmin: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    var notificationMode by remember { mutableStateOf(TextFieldValue(profile?.notificationMode ?: "Системный звук")) }
    var modeExpanded by remember { mutableStateOf(false) }
    var customSoundUri by remember { mutableStateOf(profile?.customNotificationSound.orEmpty()) }
    var customSoundName by remember {
        mutableStateOf(
            profile?.customNotificationSound
                .orEmpty()
                .substringAfterLast('/')
                .ifBlank { "Не выбран" }
        )
    }
    var pendingSoundPickerLaunch by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val notificationOptions = remember { listOf("Системный звук", "Загрузить свой") }
    val settingsTourPulse = rememberInfiniteTransition(label = "settings-tour-pulse")
    val settingsTourScale by settingsTourPulse.animateFloat(
        initialValue = 1f,
        targetValue = if (assistantTourActive) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "settings-tour-scale"
    )
    fun sScale(t: AssistantTourSpotlight) =
        if (assistantTourActive && assistantSpotlight == t) settingsTourScale else 1f

    val customSoundLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        customSoundUri = uri.toString()
        customSoundName = uri.lastPathSegment.orEmpty().substringAfterLast('/').ifBlank { "Выбранный звук" }
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingSoundPickerLaunch) {
            customSoundLauncher.launch(arrayOf("audio/*"))
        } else if (!granted) {
            android.widget.Toast.makeText(
                context,
                "Разрешение на доступ к аудио отклонено",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        pendingSoundPickerLaunch = false
    }

    var adminTapCount by remember { mutableIntStateOf(0) }
    var showAdminKeyDialog by remember { mutableStateOf(false) }
    var showSosCard by remember { mutableStateOf(false) }
    var adminKeyDraft by remember { mutableStateOf("") }

    LaunchedEffect(showAdminKeyDialog) {
        if (showAdminKeyDialog) adminKeyDraft = ""
    }

    fun displayOrMissing(value: String?, missing: String) =
        value.orEmpty().ifBlank { missing }

    Box(modifier = modifier.fillMaxSize()) {
        MedSubScreenLayout(
            title = "Настройки",
            subtitle = "Интерфейс, уведомления и аккаунт",
            onBack = onBack,
            scrollBottomPadding = 88.dp,
            titleModifier = Modifier.clickable {
                adminTapCount += 1
                if (adminTapCount >= 7) {
                    adminTapCount = 0
                    adminKeyDraft = ""
                    showAdminKeyDialog = true
                }
            }
        ) {
            MedSectionLabel("Интерфейс")
            MedSurfaceCard {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = scheme.primary.copy(alpha = 0.16f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = null,
                            tint = scheme.primary,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            "Тема оформления",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = scheme.onSurface
                        )
                        Text(
                            "Светлая или тёмная палитра",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = isDarkTheme,
                        onClick = { onDarkThemeChange(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = scheme.primary,
                            activeContentColor = scheme.onPrimary,
                            inactiveContainerColor = scheme.surfaceVariant,
                            inactiveContentColor = scheme.onSurfaceVariant
                        )
                    ) {
                        Text("Тёмная")
                    }
                    SegmentedButton(
                        selected = !isDarkTheme,
                        onClick = { onDarkThemeChange(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = scheme.primary,
                            activeContentColor = scheme.onPrimary,
                            inactiveContainerColor = scheme.surfaceVariant,
                            inactiveContentColor = scheme.onSurfaceVariant
                        )
                    ) {
                        Text("Светлая")
                    }
                }
            }

            MedSectionLabel("Уведомления")
            MedSurfaceCard(
                modifier = Modifier.assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.SettingsSoundCard,
                    pulseScale = sScale(AssistantTourSpotlight.SettingsSoundCard),
                    shape = RoundedCornerShape(18.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = scheme.primary.copy(alpha = 0.16f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = scheme.primary,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(22.dp)
                        )
                    }
                    Text(
                        "Звук напоминаний",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.onSurface
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = modeExpanded,
                    onExpandedChange = { modeExpanded = !modeExpanded }
                ) {
                    OutlinedTextField(
                        value = notificationMode,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Режим звука") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = modeExpanded,
                                modifier = Modifier,
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = scheme.onSurface,
                            unfocusedTextColor = scheme.onSurface,
                            focusedLabelColor = scheme.primary,
                            unfocusedLabelColor = scheme.onSurfaceVariant,
                            focusedBorderColor = scheme.primary,
                            unfocusedBorderColor = scheme.outline,
                            focusedTrailingIconColor = scheme.onSurfaceVariant,
                            unfocusedTrailingIconColor = scheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = modeExpanded,
                        onDismissRequest = { modeExpanded = false }
                    ) {
                        notificationOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    notificationMode = TextFieldValue(option)
                                    modeExpanded = false
                                    if (option == "Системный звук") {
                                        customSoundUri = ""
                                        customSoundName = "Не выбран"
                                    }
                                }
                            )
                        }
                    }
                }
                if (notificationMode.text == "Загрузить свой") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = TextFieldValue(customSoundName),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Выбранный файл") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = scheme.onSurface,
                            unfocusedTextColor = scheme.onSurface,
                            focusedLabelColor = scheme.primary,
                            unfocusedLabelColor = scheme.onSurfaceVariant,
                            focusedBorderColor = scheme.primary,
                            unfocusedBorderColor = scheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_AUDIO
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                                pendingSoundPickerLaunch = true
                                audioPermissionLauncher.launch(permission)
                            } else {
                                customSoundLauncher.launch(arrayOf("audio/*"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Выбрать звук")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    onClick = {
                        val current = profile ?: return@FilledTonalButton
                        onSaveSettings(
                            current.copy(
                                notificationMode = notificationMode.text.ifBlank { "Системный звук" },
                                customNotificationSound = if (notificationMode.text == "Загрузить свой") {
                                    customSoundUri
                                } else {
                                    ""
                                }
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить настройки")
                }
            }

            if (isAdmin) {
                MedSectionLabel("Администрирование")
                MedMenuCard {
                    MedMenuRow(
                        title = "Админ-панель",
                        subtitle = "Создание аккаунтов врачей",
                        icon = Icons.Filled.AdminPanelSettings,
                        onClick = onOpenAdminPanel,
                        showDividerBelow = false
                    )
                }
            }

            MedSectionLabel("Экстренно")
            MedSosHighlightCard(onClick = { showSosCard = true })

            MedSectionLabel("Аккаунт")
            MedMenuCard {
                MedMenuRow(
                    title = "Выйти из аккаунта",
                    subtitle = "Сессия на этом устройстве",
                    icon = Icons.Filled.Logout,
                    onClick = onLogout,
                    showDividerBelow = true
                )
                MedMenuRow(
                    title = "Удалить аккаунт",
                    subtitle = "Профиль, лечение и дневник",
                    icon = Icons.Filled.Delete,
                    onClick = { showDeleteConfirm = true },
                    iconTint = scheme.error,
                    iconContainerColor = scheme.errorContainer.copy(alpha = 0.55f),
                    titleColor = scheme.error,
                    showDividerBelow = false
                )
            }
        }

        Surface(
            onClick = onOpenSupportChat,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .size(70.dp)
        ) {
            AssetImage(
                fileName = "ic_support_agent.png",
                contentDescription = "Написать в поддержку",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .clip(CircleShape)
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Удаление аккаунта") },
            text = { Text("Вы уверены? Будут удалены профиль, лечение и дневник.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = scheme.error, contentColor = scheme.onError)
                ) { Text("Удалить") }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirm = false }) { Text("Отмена") }
            }
        )
    }

    if (showAdminKeyDialog) {
        AlertDialog(
            onDismissRequest = { showAdminKeyDialog = false },
            title = { Text("Режим поддержки") },
            text = {
                OutlinedTextField(
                    value = adminKeyDraft,
                    onValueChange = { adminKeyDraft = it },
                    singleLine = true,
                    label = { Text("Ключ поддержки") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val key = adminKeyDraft.trim()
                        onSaveAdminKey(key)
                        showAdminKeyDialog = false
                        if (key.isNotBlank()) onOpenSupportAdmin()
                    }
                ) { Text("Открыть панель") }
            },
            dismissButton = {
                Button(onClick = { showAdminKeyDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showSosCard) {
        MedClinicalInfoDialog(
            title = "SOS-карточка",
            subtitle = "Покажите медперсоналу при экстренной ситуации",
            headerIcon = Icons.Filled.Emergency,
            accentColor = scheme.error,
            accentContainer = scheme.errorContainer,
            rows = listOf(
                MedClinicalInfoRow(
                    "Диагнозы",
                    displayOrMissing(profile?.badHabits, "Не указаны"),
                    Icons.Filled.LocalHospital
                ),
                MedClinicalInfoRow(
                    "Аллергии",
                    displayOrMissing(profile?.allergies, "Не указаны"),
                    Icons.Filled.Warning,
                    highlight = !profile?.allergies.isNullOrBlank()
                ),
                MedClinicalInfoRow(
                    "Лекарства за 24 часа",
                    recentMedications24h.ifBlank { "Нет данных" },
                    Icons.Filled.Medication
                ),
                MedClinicalInfoRow(
                    "Контакт родственника",
                    displayOrMissing(profile?.relativeContact, "Не указан"),
                    Icons.Filled.ContactPhone,
                    highlight = true
                )
            ),
            onDismiss = { showSosCard = false }
        )
    }
}

@Composable
private fun WellbeingDiaryScreen(
    modifier: Modifier = Modifier,
    plans: List<TreatmentPlan>,
    wellbeingByDate: Map<String, WellbeingEntry>,
    assistantTourActive: Boolean,
    assistantSpotlight: AssistantTourSpotlight,
    onAssistantSpotlightBounds: (Rect?) -> Unit,
    onSaveComment: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val uiMetrics = rememberUiMetrics()

    var diaryNow by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            diaryNow = System.currentTimeMillis()
        }
    }
    val today = remember(diaryNow) {
        java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date(diaryNow))
    }
    val diaryPlans = remember(plans) {
        val withCourse = plans.filter { it.courseId > 0L }
        if (withCourse.isEmpty()) plans else withCourse
    }
    val completedDays = remember(diaryPlans, diaryNow) {
        val grouped = diaryPlans.groupBy { it.startDate }
        grouped.keys
            .filter { day ->
                val dayPlans = grouped[day].orEmpty()
                val hasUpcoming = dayPlans.any { plan ->
                    val millis = parseDateTimeMillis(plan.startDate, plan.reminderTime)
                    millis != Long.MAX_VALUE && millis >= diaryNow
                }
                day < today || (day == today && !hasUpcoming)
            }
            .sortedByDescending { parseDateTimeMillis(it, "00:00") }
    }
    var selectedDay by remember { mutableStateOf<String?>(null) }
    var draftComment by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("") }
    val diaryTourPulse = rememberInfiniteTransition(label = "diary-tour-pulse")
    val diaryTourScale by diaryTourPulse.animateFloat(
        initialValue = 1f,
        targetValue = if (assistantTourActive) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "diary-tour-scale"
    )
    fun dScale(t: AssistantTourSpotlight) =
        if (assistantTourActive && assistantSpotlight == t) diaryTourScale else 1f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(app.heroGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Дневник самочувствия", style = MaterialTheme.typography.headlineSmall, color = app.onHero)
        Text(
            "Выберите прошедший день лечения и добавьте комментарий.",
            style = MaterialTheme.typography.bodyMedium,
            color = app.onHeroMuted
        )
        if (completedDays.isEmpty()) {
            Card(
                modifier = Modifier
                    .assistantTourTarget(
                        active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.DiaryDayStrip,
                        pulseScale = dScale(AssistantTourSpotlight.DiaryDayStrip),
                        shape = RoundedCornerShape(14.dp),
                        borderColor = scheme.primary,
                        onBounds = { onAssistantSpotlightBounds(it) }
                    ),
                colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
            ) {
                Text(
                    "Прошедших дней лечения пока нет.",
                    modifier = Modifier.padding(14.dp),
                    color = scheme.onSurface
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                completedDays.forEach { day ->
                    val wellbeing = wellbeingByDate[day]
                    val hasComment = wellbeing?.comment.orEmpty().isNotBlank()
                    val status = wellbeing?.status.orEmpty()
                    val statusColor = when (status) {
                        "excellent" -> Color(0xFFA8D9B4)
                        "good" -> scheme.tertiaryContainer
                        "bad" -> scheme.errorContainer
                        else -> scheme.surfaceContainerHigh
                    }
                    Card(
                        modifier = Modifier
                            .size(width = uiMetrics.dayCardWidth, height = 128.dp)
                            .scale(
                                if (assistantTourActive && assistantSpotlight == AssistantTourSpotlight.DiaryDayStrip && day == completedDays.firstOrNull()) {
                                    dScale(AssistantTourSpotlight.DiaryDayStrip)
                                } else {
                                    1f
                                }
                            )
                            .then(
                                if (assistantTourActive && assistantSpotlight == AssistantTourSpotlight.DiaryDayStrip && day == completedDays.firstOrNull()) {
                                    Modifier.border(2.dp, scheme.primary, RoundedCornerShape(14.dp))
                                } else {
                                    Modifier
                                }
                            )
                            .onGloballyPositioned { coords ->
                                if (assistantTourActive && assistantSpotlight == AssistantTourSpotlight.DiaryDayStrip && day == completedDays.firstOrNull()) {
                                    onAssistantSpotlightBounds(coords.boundsInRoot())
                                }
                            }
                            .animateContentSize()
                            .clickable {
                                selectedDay = day
                                draftComment = wellbeing?.comment.orEmpty()
                                selectedStatus = wellbeing?.status.orEmpty()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = statusColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val unifiedFg = wellbeingDiaryCardUnifiedForeground(status)
                            Text(
                                day,
                                fontWeight = FontWeight.SemiBold,
                                color = unifiedFg ?: scheme.onSurface
                            )
                            Text(
                                wellbeingStatusLabel(status),
                                color = unifiedFg ?: scheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                painter = painterResource(id = wellbeingStatusIcon(status)),
                                contentDescription = "Статус самочувствия",
                                tint = unifiedFg ?: scheme.primary
                            )
                            Text(
                                if (hasComment) "Комментарий есть" else "Без комментария",
                                color = unifiedFg ?: scheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = scheme.primary, contentColor = scheme.onPrimary)
        ) {
            Text("Назад на главный экран")
        }
    }

    selectedDay?.let { day ->
        AlertDialog(
            onDismissRequest = { selectedDay = null },
            title = { Text("Самочувствие за $day") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("excellent", "Отлично", android.R.drawable.btn_star_big_on),
                            Triple("good", "Хорошо", android.R.drawable.presence_online),
                            Triple("bad", "Плохо", android.R.drawable.ic_delete)
                        ).forEach { (value, label, iconRes) ->
                            val scale by animateFloatAsState(
                                targetValue = if (selectedStatus == value) 1.08f else 1f,
                                animationSpec = tween(220),
                                label = "wellbeing-$value-scale"
                            )
                            Card(
                                modifier = Modifier
                                    .size(width = uiMetrics.statusCardWidth, height = uiMetrics.statusCardHeight)
                                    .scale(scale)
                                    .clickable { selectedStatus = value },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedStatus == value) {
                                        scheme.primaryContainer
                                    } else {
                                        scheme.surfaceContainerLow
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(iconRes),
                                        contentDescription = label,
                                        modifier = Modifier.size(uiMetrics.statusIconSize),
                                        tint = scheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(label, color = scheme.onSurface)
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = draftComment,
                        onValueChange = { draftComment = it },
                        label = { Text("Комментарий о состоянии") },
                        colors = readableOutlinedFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveComment(day, selectedStatus, draftComment)
                        selectedDay = null
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(onClick = { selectedDay = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun readableOutlinedFieldColors(): TextFieldColors {
    val cs = MaterialTheme.colorScheme
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = cs.onSurface,
        unfocusedTextColor = cs.onSurface,
        focusedLabelColor = cs.primary,
        unfocusedLabelColor = cs.onSurfaceVariant,
        focusedBorderColor = cs.primary,
        unfocusedBorderColor = cs.outline,
        cursorColor = cs.primary,
        errorBorderColor = cs.error,
        errorLabelColor = cs.error,
        errorCursorColor = cs.error
    )
}

private fun wellbeingStatusLabel(status: String): String = when (status) {
    "excellent" -> "Отлично"
    "good" -> "Хорошо"
    "bad" -> "Плохо"
    else -> "Статус не выбран"
}

private fun wellbeingStatusIcon(status: String): Int = when (status) {
    "excellent" -> android.R.drawable.btn_star_big_on
    "good" -> android.R.drawable.presence_online
    "bad" -> android.R.drawable.ic_delete
    else -> android.R.drawable.presence_invisible
}

/** Для карточки дня в дневнике: один цвет для всего текста и иконки; null — тема по умолчанию. */
private fun wellbeingDiaryCardUnifiedForeground(status: String): Color? = when (status) {
    "excellent" -> Color.Black
    "good" -> Color.White
    "bad" -> Color.White
    else -> null
}

/** Активный курс = максимальный ненулевой course_id; 0 — только если в базе ещё нет курсов с id. */
private fun activeCourseId(plans: List<TreatmentPlan>): Long {
    val withCourse = plans.filter { it.courseId > 0L }
    return if (withCourse.isEmpty()) 0L else withCourse.maxOf { it.courseId }
}

/** Планы только активного курса; без смешивания с course_id = 0 после появления нумерованных курсов. */
private fun plansForActiveCourseOnly(plans: List<TreatmentPlan>): List<TreatmentPlan> {
    val withCourse = plans.filter { it.courseId > 0L }
    return if (withCourse.isEmpty()) plans else plans.filter { it.courseId == withCourse.maxOf { it.courseId } }
}

private fun homeCompletedCourses(plans: List<TreatmentPlan>): List<HomeCompletedCourse> {
    val nowMillis = System.currentTimeMillis()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(nowMillis))
    val latestCourseId = activeCourseId(plans)
    val useCourseFilter = plans.any { it.courseId > 0L }
    val dayHasUpcoming = plans.groupBy { it.startDate }.mapValues { (_, dayPlans) ->
        dayPlans.any { planReminderUpcoming(it, nowMillis) }
    }
    val isCompletedPlan: (TreatmentPlan) -> Boolean = { plan ->
        plan.startDate < today || (plan.startDate == today && dayHasUpcoming[plan.startDate] != true)
    }

    val result = mutableListOf<HomeCompletedCourse>()
    if (useCourseFilter && latestCourseId > 0L) {
        plans.filter { it.courseId in 1 until latestCourseId }
            .groupBy { it.courseId }
            .entries
            .sortedByDescending { it.key }
            .forEach { (courseId, coursePlans) ->
                result.add(
                    HomeCompletedCourse(
                        folderKey = courseId,
                        title = extractCoursePeriodTitle(coursePlans),
                        plans = coursePlans
                    )
                )
            }
        val currentCompleted = plans.filter { it.courseId == latestCourseId && isCompletedPlan(it) }
        if (currentCompleted.isNotEmpty()) {
            result.add(
                0,
                HomeCompletedCourse(
                    folderKey = latestCourseId + 1_000_000L,
                    title = "Текущий курс — завершённые дни",
                    plans = currentCompleted
                )
            )
        }
    } else {
        val legacyCompleted = plans.filter(isCompletedPlan)
        if (legacyCompleted.isNotEmpty()) {
            result.add(
                HomeCompletedCourse(
                    folderKey = 0L,
                    title = extractCoursePeriodTitle(legacyCompleted),
                    plans = legacyCompleted
                )
            )
        }
    }
    return result
}

private fun extractCoursePeriodTitle(coursePlans: List<TreatmentPlan>): String {
    if (coursePlans.isEmpty()) return "Курс лечения"
    val sample = coursePlans.first().notes
    val m = Regex("Период:\\s*(\\d{4}-\\d{2}-\\d{2})\\s*-\\s*(\\d{4}-\\d{2}-\\d{2})").find(sample)
    if (m != null) {
        val a = m.groupValues[1]
        val b = m.groupValues[2]
        return "$a — $b"
    }
    val days = coursePlans.map { it.startDate }.filter { it.isNotBlank() }.sorted()
    return if (days.isEmpty()) "Курс лечения" else "${days.first()} — ${days.last()}"
}

private fun planReminderUpcoming(plan: TreatmentPlan, nowMillis: Long): Boolean {
    val millis = parseDateTimeMillis(plan.startDate, plan.reminderTime)
    return millis != Long.MAX_VALUE && millis >= nowMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TreatmentPlanDayStrip(
    plansByDay: List<Pair<String, List<TreatmentPlan>>>,
    today: String,
    dayHasUpcoming: Map<String, Boolean>,
    forceCompletedHistorical: Boolean,
    app: MedAppColors,
    scheme: ColorScheme,
    uiMetrics: UiMetrics,
    completedComposition: LottieComposition?,
    onRequestDelete: (TreatmentPlan) -> Unit,
    showDayHeadingWhenSingle: Boolean
) {
    plansByDay.forEach { (dayDate, dayPlans) ->
        key(dayDate) {
            var pillIndex by remember { mutableIntStateOf(0) }
            LaunchedEffect(dayPlans.size, dayPlans.map { it.id }) {
                pillIndex = pillIndex.coerceIn(0, (dayPlans.size - 1).coerceAtLeast(0))
            }
            val plan = dayPlans[pillIndex.coerceIn(0, dayPlans.lastIndex)]
            val now = Calendar.getInstance()
            val reminderMillis = parseDateTimeMillis(plan.startDate, plan.reminderTime)
            val minutesLeft = ((reminderMillis - now.timeInMillis) / 60000L).toInt()
            val isDoneDay = if (forceCompletedHistorical) {
                true
            } else {
                dayHasUpcoming[plan.startDate] == false && plan.startDate <= today
            }
            val accentBorderWidth = when {
                isDoneDay -> 0.dp
                minutesLeft in 0..30 -> 2.dp
                minutesLeft in 31..120 -> 1.dp
                else -> 0.dp
            }
            val accentBorderColor = when {
                isDoneDay -> Color.Transparent
                minutesLeft in 0..30 -> scheme.primary
                minutesLeft in 31..120 -> app.accentRingSoft
                else -> Color.Transparent
            }
            val planTextColor = scheme.onSurface
            val donePulse = rememberInfiniteTransition(label = "done-day-pulse-${plan.id}")
            val doneColor by donePulse.animateColor(
                initialValue = scheme.primary.copy(alpha = 0.85f),
                targetValue = scheme.primary,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "done-day-color"
            )
            val doneScale by animateFloatAsState(
                targetValue = if (isDoneDay) 1f else 0.96f,
                animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
                label = "done-scale"
            )

            val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                positionalThreshold = { totalDistance -> totalDistance * 0.35f },
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                        onRequestDelete(plan)
                    }
                    false
                }
            )

            if (showDayHeadingWhenSingle || plansByDay.size > 1) {
                Text(
                    "День: $dayDate",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = app.onHeroMuted
                )
            }

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = scheme.error)
                    ) {
                        Text(
                            "Свайп для удаления",
                            modifier = Modifier.padding(16.dp),
                            color = scheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .then(
                            if (accentBorderWidth > 0.dp) {
                                Modifier.border(
                                    accentBorderWidth,
                                    accentBorderColor,
                                    RoundedCornerShape(16.dp)
                                )
                            } else {
                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = app.cardOnHero,
                        contentColor = planTextColor
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            plan.medicineName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = planTextColor
                        )
                        Text("Дозировка: ${plan.dosage}", color = planTextColor)
                        Text("Напоминание: ${plan.reminderTime}", color = planTextColor)
                        Text("Дата приема: ${plan.startDate}", color = planTextColor)
                        if (minutesLeft in 0..120 && !isDoneDay) {
                            Text(
                                "До приема: $minutesLeft мин",
                                color = if (minutesLeft <= 30) scheme.primary else scheme.tertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        AnimatedVisibility(visible = isDoneDay) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = doneColor)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (completedComposition != null) {
                                        LottieAnimation(
                                            composition = completedComposition,
                                            iterations = LottieConstants.IterateForever,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(uiMetrics.completedAnimHeight)
                                        )
                                    }
                                    Text(
                                        "День лечения завершен и выполнен",
                                        color = scheme.onPrimary,
                                        fontSize = (15 * doneScale).sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        if (plan.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Заметки: ${plan.notes}", color = planTextColor)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    pillIndex =
                                        (pillIndex - 1 + dayPlans.size) % dayPlans.size
                                },
                                enabled = dayPlans.size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronLeft,
                                    contentDescription = "Предыдущий прием",
                                    tint = if (dayPlans.size > 1) scheme.primary else scheme.outline.copy(alpha = 0.4f)
                                )
                            }
                            Text(
                                "${pillIndex + 1} из ${dayPlans.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = scheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = {
                                    pillIndex = (pillIndex + 1) % dayPlans.size
                                },
                                enabled = dayPlans.size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Следующий прием",
                                    tint = if (dayPlans.size > 1) scheme.primary else scheme.outline.copy(alpha = 0.4f)
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { onRequestDelete(plan) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = scheme.error
                            )
                        ) {
                            Text("Удалить запись лечения")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TreatmentScreen(
    modifier: Modifier = Modifier,
    username: String,
    profile: UserProfile?,
    plans: List<TreatmentPlan>,
    wellbeingEntries: Map<String, WellbeingEntry>,
    tokenProvider: () -> String,
    patientMilestoneSeenIndex: Int,
    onPatientMilestoneSeen: (Int) -> Unit,
    onAssignmentChanged: (DoctorAssignment?) -> Unit,
    onAutoPlansCreated: (List<TreatmentPlan>) -> Unit,
    onOpenDoctorChat: (assignmentId: Long, doctorName: String) -> Unit,
    assistantTourActive: Boolean,
    assistantSpotlight: AssistantTourSpotlight,
    onAssistantSpotlightBounds: (Rect?) -> Unit,
    onSaveDayPlans: (List<TreatmentPlan>) -> Unit,
    onDeletePlan: (TreatmentPlan) -> Unit,
    onBack: () -> Unit
) {
    val app = LocalMedAppColors.current
    val scheme = MaterialTheme.colorScheme
    val uiMetrics = rememberUiMetrics()

    var startDate by remember { mutableStateOf("Выберите дату") }
    var endDate by remember { mutableStateOf("Выберите дату") }
    var isDayFlowStarted by remember { mutableStateOf(false) }
    var dayDates by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentDayIndex by remember { mutableIntStateOf(0) }
    val dayEntries = remember { mutableStateListOf(DayMedicationEntry()) }
    val context = LocalContext.current
    var selectedTreatmentTab by remember { mutableIntStateOf(0) }
    var courseSessionId by remember { mutableLongStateOf(0L) }
    val archivedFolderExpanded = remember { mutableStateMapOf<Long, Boolean>() }
    var pendingDeletePlan by remember { mutableStateOf<TreatmentPlan?>(null) }
    var showStopTreatmentDialog by remember { mutableStateOf(false) }
    var showSavedAnim by remember { mutableStateOf(false) }
    var showCourseCompletedDialog by remember { mutableStateOf(false) }
    val savedComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_saved))
    val savedProgress by animateLottieCompositionAsState(
        composition = savedComposition,
        isPlaying = showSavedAnim,
        iterations = 1
    )
    val contentScrollState = rememberScrollState()
    var clockNow by remember { mutableStateOf(System.currentTimeMillis()) }
    val readableFieldColors = readableOutlinedFieldColors()
    var notificationPermissionAsked by remember { mutableStateOf(false) }
    val treatmentTourPulse = rememberInfiniteTransition(label = "treatment-tour-pulse")
    val treatmentTourScale by treatmentTourPulse.animateFloat(
        initialValue = 1f,
        targetValue = if (assistantTourActive) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(680, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "treatment-tour-scale"
    )
    fun trScale(t: AssistantTourSpotlight) =
        if (assistantTourActive && assistantSpotlight == t) treatmentTourScale else 1f
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    val completedComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.day_completed))

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !notificationPermissionAsked
        ) {
            notificationPermissionAsked = true
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            clockNow = System.currentTimeMillis()
        }
    }

    BackHandler {
        when {
            showCourseCompletedDialog -> {
                showCourseCompletedDialog = false
                isDayFlowStarted = false
                dayDates = emptyList()
                currentDayIndex = 0
                dayEntries.clear()
                dayEntries.add(DayMedicationEntry())
            }
            isDayFlowStarted -> {
                showStopTreatmentDialog = true
            }
            else -> onBack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(app.heroGradient)
            .verticalScroll(contentScrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(380)) + slideInVertically(tween(380), initialOffsetY = { it / 8 })
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Планировщик лечения",
                    style = MaterialTheme.typography.headlineSmall,
                    color = app.onHero
                )
                Text(
                    "Сначала выберите период, затем заполните лечение по дням.",
                    color = app.onHeroMuted
                )
            }
        }
        DoctorTreatmentSection(
            username = username,
            tokenProvider = tokenProvider,
            userProfile = profile,
            treatmentPlans = plans,
            wellbeingEntries = wellbeingEntries,
            patientMilestoneSeenIndex = patientMilestoneSeenIndex,
            onPatientMilestoneSeen = onPatientMilestoneSeen,
            onOpenChat = onOpenDoctorChat,
            onAssignmentChanged = onAssignmentChanged,
            onAutoPlansCreated = onAutoPlansCreated
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(tween(320, easing = FastOutSlowInEasing))
                .assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.TreatmentPeriodCard,
                    pulseScale = trScale(AssistantTourSpotlight.TreatmentPeriodCard),
                    shape = RoundedCornerShape(12.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                ),
            colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Button(
                    onClick = { openDatePicker(context) { startDate = it } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Дата начала: $startDate") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { openDatePicker(context) { endDate = it } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Дата окончания: $endDate") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val range = generateDateRange(startDate, endDate)
                        if (range.isNotEmpty()) {
                            courseSessionId = System.currentTimeMillis()
                            dayDates = range
                            currentDayIndex = 0
                            isDayFlowStarted = true
                            dayEntries.clear()
                            dayEntries.add(DayMedicationEntry())
                        }
                    },
                    enabled = startDate != "Выберите дату" &&
                        endDate != "Выберите дату",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Перейти к заполнению дней лечения")
                }
            }
        }

        if (isDayFlowStarted && dayDates.isNotEmpty()) {
            val currentDate = dayDates[currentDayIndex]
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Заполнение лечения", style = MaterialTheme.typography.titleLarge)
                        Text("День ${currentDayIndex + 1} из ${dayDates.size}: $currentDate")
                        Spacer(modifier = Modifier.height(8.dp))
                        dayEntries.forEachIndexed { index, entry ->
                            OutlinedTextField(
                                value = entry.medicineName,
                                onValueChange = { value ->
                                    dayEntries[index] = entry.copy(medicineName = value)
                                },
                                label = { Text("Таблетка ${index + 1}: название") },
                                colors = readableFieldColors,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = entry.dosage,
                                onValueChange = { value ->
                                    dayEntries[index] = entry.copy(dosage = value)
                                },
                                label = { Text("Дозировка") },
                                colors = readableFieldColors,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    openTimePicker(context, entry.reminderTime) { selected ->
                                        dayEntries[index] = entry.copy(reminderTime = selected)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Время приема: ${entry.reminderTime}")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = entry.notes,
                                onValueChange = { value ->
                                    dayEntries[index] = entry.copy(notes = value)
                                },
                                label = { Text("Заметки") },
                                colors = readableFieldColors,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Button(
                            onClick = { dayEntries.add(DayMedicationEntry()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("+ Добавить таблетку в этот день")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val plansForDay = dayEntries
                                    .filter {
                                        it.medicineName.isNotBlank() &&
                                            it.dosage.isNotBlank() &&
                                            it.reminderTime.matches(Regex("\\d{2}:\\d{2}"))
                                    }
                                    .map {
                                        val combinedNotes = buildString {
                                            append("Период: $startDate - $endDate.")
                                            if (it.notes.isNotBlank()) {
                                                append(" Заметка: ${it.notes.trim()}.")
                                            }
                                        }
                                        TreatmentPlan(
                                            username = username,
                                            medicineName = it.medicineName.trim(),
                                            dosage = it.dosage.trim(),
                                            timesPerDay = 1,
                                            reminderTime = it.reminderTime.trim(),
                                            startDate = currentDate,
                                            endDate = currentDate,
                                            notes = combinedNotes,
                                            courseId = courseSessionId
                                        )
                                    }
                                onSaveDayPlans(plansForDay)
                                showSavedAnim = true
                                if (currentDayIndex < dayDates.lastIndex) {
                                    val nextIndex = (currentDayIndex + 1).coerceAtMost(dayDates.lastIndex)
                                    currentDayIndex = nextIndex
                                    dayEntries.clear()
                                    dayEntries.add(DayMedicationEntry())
                                } else {
                                    showCourseCompletedDialog = true
                                    dayEntries.clear()
                                    dayEntries.add(DayMedicationEntry())
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = dayEntries.any {
                                it.medicineName.isNotBlank() &&
                                    it.dosage.isNotBlank() &&
                                    it.reminderTime.matches(Regex("\\d{2}:\\d{2}"))
                            }
                        ) {
                            Text(
                                if (currentDayIndex < dayDates.lastIndex) {
                                    "Сохранить день и перейти к следующему"
                                } else {
                                    "Сохранить последний день лечения"
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                showStopTreatmentDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Закрыть окно заполнения")
                        }
                        AnimatedVisibility(
                            visible = showSavedAnim,
                            enter = fadeIn(tween(250)),
                            exit = fadeOut(tween(350))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LottieAnimation(
                                    composition = savedComposition,
                                    progress = { savedProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(uiMetrics.savedAnimHeight)
                                )
                                Text(
                                    "Успешно сохранено",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = scheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
        LaunchedEffect(showSavedAnim) {
            if (showSavedAnim) {
                delay(1200)
                showSavedAnim = false
            }
        }
        Text(
            "Сохраненное расписание лечения",
            style = MaterialTheme.typography.titleMedium,
            color = app.onHero
        )
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .assistantTourTarget(
                    active = assistantTourActive && assistantSpotlight == AssistantTourSpotlight.TreatmentTabs,
                    pulseScale = trScale(AssistantTourSpotlight.TreatmentTabs),
                    shape = RoundedCornerShape(12.dp),
                    borderColor = scheme.primary,
                    onBounds = { onAssistantSpotlightBounds(it) }
                ),
            selectedTabIndex = selectedTreatmentTab,
            containerColor = Color.Transparent,
            contentColor = app.onHero
        ) {
            Tab(
                selected = selectedTreatmentTab == 0,
                onClick = { selectedTreatmentTab = 0 },
                text = { Text("Сегодня") }
            )
            Tab(
                selected = selectedTreatmentTab == 1,
                onClick = { selectedTreatmentTab = 1 },
                text = { Text("Скоро") }
            )
            Tab(
                selected = selectedTreatmentTab == 2,
                onClick = { selectedTreatmentTab = 2 },
                text = { Text("Завершено") }
            )
        }

        val nowMillis = clockNow
        val today = remember(plans.size, selectedTreatmentTab, clockNow) {
            java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date(nowMillis))
        }
        val latestCourseId = activeCourseId(plans)
        val useCourseFilter = plans.any { it.courseId > 0L }
        val dayHasUpcoming = remember(plans.size, clockNow) {
            plans.groupBy { it.startDate }.mapValues { (_, dayPlans) ->
                dayPlans.any { plan ->
                    val reminderMillis = parseDateTimeMillis(plan.startDate, plan.reminderTime)
                    reminderMillis != Long.MAX_VALUE && reminderMillis >= nowMillis
                }
            }
        }
        val filteredPlans = remember(
            plans,
            selectedTreatmentTab,
            today,
            nowMillis,
            dayHasUpcoming,
            latestCourseId,
            useCourseFilter
        ) {
            if (!useCourseFilter || latestCourseId <= 0) {
                plans.filter { plan ->
                    val hasUpcomingForDay = dayHasUpcoming[plan.startDate] == true
                    when (selectedTreatmentTab) {
                        0 -> plan.startDate == today && hasUpcomingForDay
                        1 -> plan.startDate > today && hasUpcomingForDay
                        else -> plan.startDate < today || (plan.startDate == today && !hasUpcomingForDay)
                    }
                }.sortedBy { parseDateTimeMillis(it.startDate, it.reminderTime) }
            } else {
                when (selectedTreatmentTab) {
                    0 -> plans.filter {
                        it.courseId == latestCourseId &&
                            it.startDate == today &&
                            planReminderUpcoming(it, nowMillis)
                    }
                    1 -> plans.filter {
                        it.courseId == latestCourseId &&
                            it.startDate > today
                    }
                    else -> plans.filter { plan ->
                        when {
                            plan.courseId > 0 && plan.courseId < latestCourseId -> false
                            plan.courseId == 0L -> {
                                val hasUpcomingForDay = dayHasUpcoming[plan.startDate] == true
                                plan.startDate < today || (plan.startDate == today && !hasUpcomingForDay)
                            }
                            plan.courseId == latestCourseId ->
                                plan.startDate < today ||
                                    (plan.startDate == today && !planReminderUpcoming(plan, nowMillis))
                            else -> false
                        }
                    }
                }.sortedBy { parseDateTimeMillis(it.startDate, it.reminderTime) }
            }
        }
        val archivedCourseGroups = remember(plans, selectedTreatmentTab, latestCourseId, useCourseFilter) {
            if (selectedTreatmentTab != 2 || !useCourseFilter || latestCourseId <= 0) {
                emptyList()
            } else {
                plans.filter { it.courseId > 0 && it.courseId < latestCourseId }
                    .groupBy { it.courseId }
                    .entries
                    .sortedByDescending { it.key }
            }
        }
        LaunchedEffect(archivedCourseGroups.map { it.key }) {
            archivedCourseGroups.forEach { (cid, _) ->
                if (!archivedFolderExpanded.containsKey(cid)) {
                    archivedFolderExpanded[cid] = false
                }
            }
        }
        val plansByDay = filteredPlans
            .groupBy { it.startDate }
            .toList()
            .sortedBy { (day, _) -> parseDateTimeMillis(day, "00:00") }
        val scheduleEmpty =
            filteredPlans.isEmpty() && (selectedTreatmentTab != 2 || archivedCourseGroups.isEmpty())
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(350)) + slideInHorizontally(tween(350), initialOffsetX = { it / 6 }),
            exit = fadeOut(tween(250))
        ) {
            if (scheduleEmpty) {
                Text("Планы лечения пока отсутствуют.", color = app.onHero)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (selectedTreatmentTab == 2 && archivedCourseGroups.isNotEmpty()) {
                        archivedCourseGroups.forEach { (courseId, coursePlans) ->
                            val title = extractCoursePeriodTitle(coursePlans)
                            val expanded = archivedFolderExpanded[courseId] ?: false
                            val groupedArchived = coursePlans
                                .groupBy { it.startDate }
                                .toList()
                                .sortedBy { (day, _) -> parseDateTimeMillis(day, "00:00") }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = app.cardOnHero)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val cur = archivedFolderExpanded[courseId] ?: false
                                                archivedFolderExpanded[courseId] = !cur
                                            },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = scheme.onSurface
                                            )
                                            Text(
                                                "Завершённый курс (${coursePlans.size} записей)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = scheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(
                                            imageVector = if (expanded) {
                                                Icons.Filled.KeyboardArrowUp
                                            } else {
                                                Icons.Filled.KeyboardArrowDown
                                            },
                                            contentDescription = null,
                                            tint = scheme.primary
                                        )
                                    }
                                    AnimatedVisibility(visible = expanded) {
                                        Column(
                                            modifier = Modifier.padding(top = 10.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            TreatmentPlanDayStrip(
                                                plansByDay = groupedArchived,
                                                today = today,
                                                dayHasUpcoming = dayHasUpcoming,
                                                forceCompletedHistorical = true,
                                                app = app,
                                                scheme = scheme,
                                                uiMetrics = uiMetrics,
                                                completedComposition = completedComposition,
                                                onRequestDelete = { pendingDeletePlan = it },
                                                showDayHeadingWhenSingle = true
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (filteredPlans.isNotEmpty()) {
                        TreatmentPlanDayStrip(
                            plansByDay = plansByDay,
                            today = today,
                            dayHasUpcoming = dayHasUpcoming,
                            forceCompletedHistorical = false,
                            app = app,
                            scheme = scheme,
                            uiMetrics = uiMetrics,
                            completedComposition = completedComposition,
                            onRequestDelete = { pendingDeletePlan = it },
                            showDayHeadingWhenSingle = plansByDay.size > 1
                        )
                    }
                }
            }
        }

        pendingDeletePlan?.let { planToDelete ->
            AlertDialog(
                onDismissRequest = { pendingDeletePlan = null },
                title = { Text("Вы уверены?") },
                text = { Text("Удалить запись лечения: ${planToDelete.medicineName}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeletePlan(planToDelete)
                            pendingDeletePlan = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = scheme.error, contentColor = scheme.onError)
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    Button(onClick = { pendingDeletePlan = null }) {
                        Text("Отмена")
                    }
                }
            )
        }

        if (showCourseCompletedDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCourseCompletedDialog = false
                    isDayFlowStarted = false
                    dayDates = emptyList()
                    currentDayIndex = 0
                    dayEntries.clear()
                    dayEntries.add(DayMedicationEntry())
                },
                title = { Text("Курс заполнен") },
                text = { Text("Последний день лечения сохранен. Приложение остается открытым.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showCourseCompletedDialog = false
                            isDayFlowStarted = false
                            dayDates = emptyList()
                            currentDayIndex = 0
                            dayEntries.clear()
                            dayEntries.add(DayMedicationEntry())
                        }
                    ) {
                        Text("Закрыть окно заполнения")
                    }
                }
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = scheme.primary, contentColor = scheme.onPrimary)
        ) {
            Text("Назад на главный экран")
        }
    }
    if (showStopTreatmentDialog) {
        AlertDialog(
            onDismissRequest = { showStopTreatmentDialog = false },
            title = { Text("Остановка лечения") },
            text = { Text("Уверены что хотите прекратить лечение сейчас?") },
            confirmButton = {
                Button(
                    onClick = {
                        showStopTreatmentDialog = false
                        isDayFlowStarted = false
                        dayDates = emptyList()
                        currentDayIndex = 0
                        dayEntries.clear()
                        dayEntries.add(DayMedicationEntry())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scheme.error,
                        contentColor = scheme.onError
                    )
                ) {
                    Text("Прекратить")
                }
            },
            dismissButton = {
                Button(onClick = { showStopTreatmentDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun CabinetDetailBlock(title: String, body: String) {
    if (body.isBlank()) return
    val scheme = MaterialTheme.colorScheme
    Text(title, fontWeight = FontWeight.SemiBold, color = scheme.primary)
    Text(body, color = scheme.onSurface, modifier = Modifier.padding(bottom = 10.dp))
}

private fun findNextPlan(plans: List<TreatmentPlan>): TreatmentPlan? {
    val now = System.currentTimeMillis()
    val scoped = plansForActiveCourseOnly(plans)
    return scoped
        .asSequence()
        .map { it to parseDateTimeMillis(it.startDate, it.reminderTime) }
        .filter { (_, millis) -> millis >= now && millis != Long.MAX_VALUE }
        .minByOrNull { (_, millis) -> millis }
        ?.first
}

private suspend fun resolveScannedMedicineCandidate(barcode: String): ScannedMedicineCandidate? {
    val normalized = barcode.filter(Char::isDigit)
    if (normalized.length < 8) return null
    return withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("https://world.openfoodfacts.org/api/v2/product/$normalized.json")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
            }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val json = JSONObject(body)
            if (json.optInt("status") != 1) return@runCatching null
            val product = json.optJSONObject("product") ?: return@runCatching null
            val name = product.optString("product_name").trim()
            if (name.isBlank()) return@runCatching null
            val productUrl = product.optString("url").trim()
            val fallback = "https://www.google.com/search?q=" +
                URLEncoder.encode("$name таблетки", "UTF-8")
            ScannedMedicineCandidate(
                name = name,
                barcode = normalized,
                infoUrl = if (productUrl.isNotBlank()) productUrl else fallback
            )
        }.getOrNull()
    }
}

private fun fallbackScannedMedicineCandidate(rawBarcode: String): ScannedMedicineCandidate {
    val normalized = rawBarcode.filter(Char::isDigit).ifBlank { rawBarcode.trim() }
    val name = if (normalized.length >= 8) {
        "Препарат по штрихкоду $normalized"
    } else {
        "Сканированный препарат"
    }
    val searchUrl = "https://www.google.com/search?q=" +
        URLEncoder.encode("$normalized таблетки инструкция", "UTF-8")
    return ScannedMedicineCandidate(
        name = name,
        barcode = normalized,
        infoUrl = searchUrl
    )
}

private fun parseScannedMedicineCode(raw: String): ScannedMedicineCodeInfo {
    val normalized = raw
        .removePrefix("]d2")
        .replace("\u001D", "|")
        .trim()

    val bracketed = Regex("\\((\\d{2})\\)([^()]+)")
        .findAll(normalized)
        .associate { it.groupValues[1] to it.groupValues[2] }

    val gtinFromBrackets = bracketed["01"]?.take(14)
    val expiryFromBrackets = formatGs1Date(bracketed["17"])
    val batchFromBrackets = bracketed["10"]?.trim()?.ifBlank { null }
    val serialFromBrackets = bracketed["21"]?.trim()?.ifBlank { null }

    if (gtinFromBrackets != null || expiryFromBrackets != null || batchFromBrackets != null || serialFromBrackets != null) {
        return ScannedMedicineCodeInfo(
            rawValue = raw,
            gtin = gtinFromBrackets,
            expiryDate = expiryFromBrackets,
            batch = batchFromBrackets,
            serial = serialFromBrackets
        )
    }

    val gs = '\u001D'
    val compactRaw = raw.removePrefix("]d2")
    var idx = 0
    var gtin: String? = null
    var expiry: String? = null
    var batch: String? = null
    var serial: String? = null

    fun readVariable(start: Int, maxLen: Int): Pair<String, Int> {
        val sb = StringBuilder()
        var i = start
        while (i < compactRaw.length && compactRaw[i] != gs && sb.length < maxLen) {
            sb.append(compactRaw[i])
            i++
        }
        if (i < compactRaw.length && compactRaw[i] == gs) i++
        return sb.toString() to i
    }

    while (idx + 2 <= compactRaw.length) {
        when (compactRaw.substring(idx, idx + 2)) {
            "01" -> {
                if (idx + 16 <= compactRaw.length) {
                    gtin = compactRaw.substring(idx + 2, idx + 16)
                    idx += 16
                } else break
            }
            "17" -> {
                if (idx + 8 <= compactRaw.length) {
                    expiry = formatGs1Date(compactRaw.substring(idx + 2, idx + 8))
                    idx += 8
                } else break
            }
            "10" -> {
                val (value, newIdx) = readVariable(idx + 2, maxLen = 20)
                batch = value.ifBlank { null }
                idx = newIdx
            }
            "21" -> {
                val (value, newIdx) = readVariable(idx + 2, maxLen = 20)
                serial = value.ifBlank { null }
                idx = newIdx
            }
            else -> idx++
        }
    }

    return ScannedMedicineCodeInfo(
        rawValue = raw,
        gtin = gtin,
        expiryDate = expiry,
        batch = batch,
        serial = serial
    )
}

private fun formatGs1Date(value: String?): String? {
    if (value.isNullOrBlank() || value.length != 6 || !value.all(Char::isDigit)) return null
    val yy = value.substring(0, 2).toIntOrNull() ?: return null
    val mm = value.substring(2, 4).toIntOrNull() ?: return null
    val dd = value.substring(4, 6).toIntOrNull() ?: return null
    if (mm !in 1..12 || dd !in 1..31) return null
    val fullYear = if (yy >= 80) 1900 + yy else 2000 + yy
    return String.format("%02d.%02d.%04d", dd, mm, fullYear)
}

private fun isRussianEnglishLogin(login: String): Boolean {
    val trimmed = login.trim()
    return trimmed.matches(Regex("^[\\p{L}\\d._-]{3,30}$"))
}

private fun openDatePicker(context: Context, onSelected: (String) -> Unit) {
    val now = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val result = "%04d-%02d-%02d".format(year, month + 1, day)
            onSelected(result)
        },
        now.get(Calendar.YEAR),
        now.get(Calendar.MONTH),
        now.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun openTimePicker(
    context: Context,
    currentTime: String,
    onSelected: (String) -> Unit
) {
    val parts = currentTime.split(":")
    val currentHour = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val currentMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    TimePickerDialog(
        context,
        { _, hour, minute ->
            onSelected(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
        },
        currentHour,
        currentMinute,
        true
    ).show()
}

private fun parseDateTimeMillis(date: String, time: String): Long {
    return try {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        formatter.isLenient = false
        formatter.parse("$date $time")?.time ?: Long.MAX_VALUE
    } catch (_: Exception) {
        Long.MAX_VALUE
    }
}

private fun isDayCompleted(date: String): Boolean {
    return try {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.isLenient = false
        val day = formatter.parse(date) ?: return false
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        day.before(today)
    } catch (_: Exception) {
        false
    }
}

private fun isReportReadyForExport(
    plans: List<TreatmentPlan>,
    wellbeingByDate: Map<String, WellbeingEntry>,
    nowMillis: Long = System.currentTimeMillis()
): Boolean {
    if (plans.isEmpty()) return false

    val hasUpcomingReminders = plans.any { plan ->
        val reminderMillis = parseDateTimeMillis(plan.startDate, plan.reminderTime)
        reminderMillis != Long.MAX_VALUE && reminderMillis >= nowMillis
    }
    if (hasUpcomingReminders) return false

    val requiredDays = plans.map { it.startDate }.toSet()
    val wellbeingDays = wellbeingByDate
        .filterValues { it.status.isNotBlank() || it.comment.isNotBlank() }
        .keys
        .toSet()

    return requiredDays.all { it in wellbeingDays }
}

private fun triggerMicroVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
            vibrator?.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 45, 30, 45), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(70)
            }
        }
    } catch (_: Exception) {
    }
}

private fun generateDateRange(startDate: String, endDate: String): List<String> {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    formatter.isLenient = false
    return try {
        val start = formatter.parse(startDate) ?: return emptyList()
        val end = formatter.parse(endDate) ?: return emptyList()
        if (start.after(end)) return emptyList()
        val result = mutableListOf<String>()
        val cursor = Calendar.getInstance().apply { time = start }
        val endCalendar = Calendar.getInstance().apply { time = end }
        while (!cursor.after(endCalendar)) {
            result += formatter.format(cursor.time)
            cursor.add(Calendar.DAY_OF_MONTH, 1)
        }
        result
    } catch (_: Exception) {
        emptyList()
    }
}

private fun scheduleTreatmentReminder(
    context: Context,
    alarmManager: AlarmManager?,
    plan: TreatmentPlan,
    profile: UserProfile?
) {
    if (alarmManager == null) return
    val timeParts = plan.reminderTime.split(":")
    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
    val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    dateFormatter.isLenient = false
    val planDate = try {
        dateFormatter.parse(plan.startDate)
    } catch (_: Exception) {
        null
    } ?: return

    val calendar = Calendar.getInstance().apply {
        time = planDate
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
    val triggerAtMillis = calendar.timeInMillis
    if (triggerAtMillis < System.currentTimeMillis()) return

    val intent = Intent(context, TreatmentReminderReceiver::class.java).apply {
        putExtra(TreatmentReminderReceiver.EXTRA_TITLE, "Время приема лекарства")
        putExtra(
            TreatmentReminderReceiver.EXTRA_BODY,
            "Примите ${plan.medicineName} (${plan.dosage})."
        )
        val customSoundUri = profile?.customNotificationSound.orEmpty()
        val useCustomSound = profile?.notificationMode == "Загрузить свой" && customSoundUri.isNotBlank()
        if (useCustomSound) {
            putExtra(TreatmentReminderReceiver.EXTRA_CUSTOM_SOUND_URI, customSoundUri)
            putExtra(TreatmentReminderReceiver.EXTRA_CUSTOM_SOUND_TRIM_MS, 10_000L)
        }
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        (plan.medicineName.hashCode() + plan.startDate.hashCode() + plan.reminderTime.hashCode()).absoluteValue,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    } catch (_: SecurityException) {
        // Keep exact trigger even when exact alarm policy is strict.
        val showIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
            pendingIntent
        )
    }
}

private fun recentMedicationsLast24Hours(plans: List<TreatmentPlan>): String {
    val now = System.currentTimeMillis()
    return plans
        .mapNotNull { plan ->
            val millis = parseDateTimeMillis(plan.startDate, plan.reminderTime)
            if (millis in (now - 24L * 60L * 60L * 1000L)..now) {
                "${plan.medicineName} (${plan.dosage}) в ${plan.reminderTime}"
            } else {
                null
            }
        }
        .ifEmpty { emptyList() }
        .joinToString(separator = "\n")
}

private enum class Screen {
    Onboarding,
    Login,
    Register,
    Profile,
    Home,
    PersonalProfile,
    Settings,
    SupportChat,
    SupportAdmin,
    AdminPanel,
    DoctorPanel,
    DoctorProfileForm,
    DoctorPatientChat,
    DoctorPeerChat,
    Treatment,
    WellbeingDiary
}

@Preview(showBackground = true)
@Composable
private fun AuthPreview() {
    Medtest1Theme {
        LoginScreen(
            onLogin = { _, _ -> },
            onOpenRegister = { },
            onRequestPasswordCode = { _, _ -> PasswordResetFlowResult(true, "") },
            onVerifyResetCode = { _, _, _ -> PasswordResetFlowResult(true, "") },
            onCompletePasswordReset = { _, _, _, _ -> PasswordResetFlowResult(true, "") }
        )
    }
}