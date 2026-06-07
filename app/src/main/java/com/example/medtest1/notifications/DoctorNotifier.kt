package com.example.medtest1.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medtest1.MainActivity
import com.example.medtest1.R

object DoctorNotifier {
    private const val CHANNEL_ID = "doctor_events"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val mgr = context.getSystemService(NotificationManager::class.java) ?: return
        if (mgr.getNotificationChannel(CHANNEL_ID) != null) return
        mgr.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Врач и лечение",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Сообщения врача, рецепты и отчёты"
            }
        )
    }

    fun showPeer(
        context: Context,
        senderName: String,
        body: String,
        peerDoctorId: Long,
        peerSpecialty: String = ""
    ) {
        ensureChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_PEER_CHAT, true)
            putExtra(MainActivity.EXTRA_PEER_DOCTOR_ID, peerDoctorId)
            putExtra(MainActivity.EXTRA_PEER_DOCTOR_NAME, senderName)
            putExtra(MainActivity.EXTRA_PEER_DOCTOR_SPECIALTY, peerSpecialty)
            putExtra(MainActivity.EXTRA_OPEN_DOCTOR_PANEL, true)
        }
        val pending = PendingIntent.getActivity(
            context,
            (peerDoctorId % Int.MAX_VALUE).toInt() + 50_000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Коллега: $senderName")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        val mgr = context.getSystemService(NotificationManager::class.java) ?: return
        mgr.notify((peerDoctorId % Int.MAX_VALUE).toInt() + 50_000, notification)
    }

    fun show(
        context: Context,
        title: String,
        body: String,
        assignmentId: Long = 0L,
        openDoctorPanel: Boolean = false
    ) {
        ensureChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_DOCTOR_CHAT, assignmentId > 0L)
            putExtra(MainActivity.EXTRA_ASSIGNMENT_ID, assignmentId)
            putExtra(MainActivity.EXTRA_OPEN_DOCTOR_PANEL, openDoctorPanel)
        }
        val pending = PendingIntent.getActivity(
            context,
            (assignmentId % Int.MAX_VALUE).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        val mgr = context.getSystemService(NotificationManager::class.java) ?: return
        mgr.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }
}
