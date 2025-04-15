package com.example.bug_it.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bug_it.MainActivity
import com.example.bug_it.R
import com.example.bug_it.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BugItFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // TODO: Send this token to your server
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")
        
        // Extract notification data
        val title = message.notification?.title ?: "New Bug Report"
        val body = message.notification?.body ?: "A new bug has been reported"
        
        // Show notification using the helper
        notificationHelper.showNotification(title, body)
    }

    companion object {
        private const val TAG = "BugItFirebaseMsgService"
    }
} 