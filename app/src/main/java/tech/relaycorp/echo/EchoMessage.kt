package tech.relaycorp.echo

import tech.relaycorp.relaynet.messages.Parcel
import tech.relaycorp.sdk.models.IncomingMessage
import java.nio.charset.Charset


data class EchoMessage(
    val time: Long,
    val message: String
) {
    companion object {
        fun fromIncomingMessage(message: IncomingMessage) = EchoMessage(
            message.creationDate.toInstant().toEpochMilli(),
            message.message.toString(Charset.forName("UTF-8"))
        )
    }
}
