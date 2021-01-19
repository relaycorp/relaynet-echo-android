package tech.relaycorp.sdk.messaging

import tech.relaycorp.sdk.Client
import java.time.ZonedDateTime

data class Message(
    val id: MessageId = MessageId.generate(),
    val message: ByteArray,
    val channel: Client.Channel,
    val creationDate: ZonedDateTime = ZonedDateTime.now(),
    val expirationDate: ZonedDateTime = maxExpirationDate()
) {
    companion object {
        private fun maxExpirationDate() = ZonedDateTime.now().plusDays(30)
    }
}
