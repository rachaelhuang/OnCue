package com.ait.oncue.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ait.oncue.MainActivity
import com.ait.oncue.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class OnCueFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        // TODO: Send token to your server to send push notifications
        // You can store this in Firestore under the user's document
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "Message received from: ${message.from}")

        // Check if message contains a notification payload
        message.notification?.let { notification ->
            val title = notification.title ?: "OnCue"
            val body = notification.body ?: "New prompt available!"

            sendNotification(title, body)
        }

        // Handle data payload
        message.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${message.data}")

            // You can handle custom data here
            val promptId = message.data["promptId"]
            val promptType = message.data["promptType"]

            // Send notification with custom data
            if (promptId != null) {
                sendNotification(
                    "Today's Prompt is Ready! 🎯",
                    "Tap to see what your friends are sharing today"
                )
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "oncue_notifications"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to add this drawable
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "OnCue Daily Prompts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for daily prompts and friend activity"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}