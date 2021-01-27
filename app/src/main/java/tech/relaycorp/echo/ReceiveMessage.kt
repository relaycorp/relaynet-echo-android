package tech.relaycorp.echo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import tech.relaycorp.sdk.IncomingMessage
import javax.inject.Inject
import kotlin.random.Random

class ReceiveMessage
@Inject constructor(
    private val messageRepository: MessageRepository,
    private val context: Context
) {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    suspend fun receive(message: IncomingMessage) {
        val echoMessage = EchoMessage.fromIncomingMessage(message)
        messageRepository.receive(echoMessage)
        message.ack()
        showNotification(echoMessage)
    }

    private fun showNotification(message: EchoMessage) {
        notificationManager.notify(
            Random.nextInt(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ensureNotificationChannel()
                Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            } else {
                Notification.Builder(context)
            }
                .setContentText(message.message)
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java),
                        0
                    )
                )
                .setAutoCancel(true)
                .build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ensureNotificationChannel() {
        notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
            ?: notificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "general"
    }
}
