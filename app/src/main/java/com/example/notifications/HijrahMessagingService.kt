package com.example.notifications

import android.util.Log
import com.example.data.HijrahRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HijrahMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM token received (len=${token.length})")
        if (Firebase.auth.currentUser == null || token.isBlank()) return

        serviceScope.launch {
            HijrahRepository(applicationContext).saveFcmToken(token)
                .onFailure { error ->
                    Log.w(TAG, "Failed to persist refreshed FCM token: ${error.localizedMessage}")
                }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val type = data["type"].orEmpty().lowercase()

        val isForumReply = type == "forum_reply" || data["channel"] == HijrahNotificationManager.CHANNEL_FORUM_REPLIES
        val channelId = if (isForumReply) {
            HijrahNotificationManager.CHANNEL_FORUM_REPLIES
        } else {
            HijrahNotificationManager.CHANNEL_MILESTONE_REMINDERS
        }

        val title = data["title"]?.takeIf { it.isNotBlank() }
            ?: message.notification?.title?.takeIf { it.isNotBlank() }
            ?: if (isForumReply) "Nieuwe reactie op het forum" else "Hijrah mijlpaal-herinnering"

        val body = data["body"]?.takeIf { it.isNotBlank() }
            ?: message.notification?.body?.takeIf { it.isNotBlank() }
            ?: if (isForumReply) {
                "Er is een nieuw antwoord geplaatst in de zustercommunity."
            } else {
                "Bekijk je volgende stap in de Migratie Voortgangstracker."
            }

        val targetRoute = data["targetRoute"]?.takeIf { it.isNotBlank() }
            ?: if (isForumReply) "community" else "profile"

        HijrahNotificationManager.showNotification(
            context = applicationContext,
            channelId = channelId,
            title = title,
            body = body,
            targetRoute = targetRoute
        )
    }

    companion object {
        private const val TAG = "HijrahMessagingService"
    }
}
