package tech.relaycorp.echo

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import javax.inject.Inject
import kotlin.random.Random

class ReceiveMessage
@Inject constructor(
    private val messageRepository: MessageRepository,
    private val context: Context
) {

    suspend fun receive(message: EchoMessage) {
        messageRepository.receive(message)
        showNotification(message)
    }

    private fun showNotification(message: EchoMessage) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            Random.nextInt(),
            Notification.Builder(context)
                .setContentText(message.message)
                .build()
        )
    }
}
