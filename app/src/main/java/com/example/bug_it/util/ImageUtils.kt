package com.example.bug_it.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun getImageFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        return file
    }
} 