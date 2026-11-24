package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await

object HijrahNotificationManager {
    const val CHANNEL_FORUM_REPLIES = "hijrah_forum_replies"
    const val CHANNEL_MILESTONE_REMINDERS = "hijrah_milestone_reminders"

    const val TOPIC_FORUM_REPLIES = "forum_replies"
    const val TOPIC_MILESTONE_REMINDERS = "milestone_reminders"

    const val EXTRA_TARGET_ROUTE = "hijrah_target_route"

    private const val TAG = "HijrahFCM"

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val forumChannel = NotificationChannel(
            CHANNEL_FORUM_REPLIES,
            "Forumreacties",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Meldingen bij nieuwe antwoorden op jouw vragen en communityberichten."
        }

        val milestoneChannel = NotificationChannel(
            CHANNEL_MILESTONE_REMINDERS,
            "Mijlpaal-herinneringen",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Herinneringen voor juridische documenten, woningzoektocht en culturele voorbereiding."
        }

        manager.createNotificationChannel(forumChannel)
        manager.createNotificationChannel(milestoneChannel)
    }

    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        targetRoute: String? = null,
        notificationId: Int = (System.currentTimeMillis() and 0x7FFFFFFF).toInt()
    ) {
        ensureChannels(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification skipped: POST_NOTIFICATIONS permission not granted.")
            return
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (!targetRoute.isNullOrBlank()) {
                putExtra(EXTRA_TARGET_ROUTE, targetRoute)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_hijrah_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (securityException: SecurityException) {
            Log.w(TAG, "Missing notification permission: ${securityException.localizedMessage}")
        } catch (error: Exception) {
            Log.w(TAG, "Unable to post notification: ${error.localizedMessage}")
        }
    }

    suspend fun fetchTokenAndSyncTopics(
        forumRepliesEnabled: Boolean,
        milestoneRemindersEnabled: Boolean
    ): String {
        val messaging = Firebase.messaging
        runCatching {
            if (forumRepliesEnabled) {
                messaging.subscribeToTopic(TOPIC_FORUM_REPLIES).await()
            } else {
                messaging.unsubscribeFromTopic(TOPIC_FORUM_REPLIES).await()
            }
        }.onFailure { error ->
            Log.w(TAG, "Topic sync ($TOPIC_FORUM_REPLIES) warning: ${error.localizedMessage}")
        }

        runCatching {
            if (milestoneRemindersEnabled) {
                messaging.subscribeToTopic(TOPIC_MILESTONE_REMINDERS).await()
            } else {
                messaging.unsubscribeFromTopic(TOPIC_MILESTONE_REMINDERS).await()
            }
        }.onFailure { error ->
            Log.w(TAG, "Topic sync ($TOPIC_MILESTONE_REMINDERS) warning: ${error.localizedMessage}")
        }

        return runCatching {
            messaging.token.await().orEmpty()
        }.getOrElse { error ->
            Log.w(TAG, "Unable to fetch FCM token yet: ${error.localizedMessage}")
            ""
        }
    }
}
