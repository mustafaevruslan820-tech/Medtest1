package com.example.medtest1.doctor

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/** Безопасное преобразование для Compose (HARDWARE bitmap на MIUI/Android ломает asImageBitmap). */
fun Bitmap.toComposeImageBitmap(): ImageBitmap {
    val software = if (config == Bitmap.Config.HARDWARE) {
        copy(Bitmap.Config.ARGB_8888, false)
    } else {
        this
    }
    return software.asImageBitmap()
}
