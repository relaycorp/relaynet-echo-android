package tech.relaycorp.sdk.models

import java.time.ZonedDateTime
import java.util.*

abstract class Message(
    val id: Id,
    val message: ByteArray,
    senderEndpoint: Endpoint,
    receiverEndpoint: Endpoint,
    val creationDate: ZonedDateTime = ZonedDateTime.now(),
    val expirationDate: ZonedDateTime = maxExpirationDate()
) {
    companion object {
        internal fun maxExpirationDate() = ZonedDateTime.now().plusDays(30)
    }

    class Id
    internal constructor(
        val value: String
    ) {
        companion object {
            fun generate() = Id(UUID.randomUUID().toString())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Id) return false
            if (value != other.value) return false
            return true
        }

        override fun hashCode() = value.hashCode()
    }
}

class IncomingMessage internal constructor(
    id: Id,
    message: ByteArray,
    val senderEndpoint: ThirdPartyEndpoint,
    val receiverEndpoint: FirstPartyEndpoint,
    creationDate: ZonedDateTime,
    expiryDate: ZonedDateTime,
    val ack: suspend () -> Unit
) : Message(
    id, message, senderEndpoint, receiverEndpoint, creationDate, expiryDate
)

class OutgoingMessage(
    message: ByteArray,
    val senderEndpoint: FirstPartyEndpoint,
    val receiverEndpoint: ThirdPartyEndpoint,
    creationDate: ZonedDateTime = ZonedDateTime.now(),
    expirationDate: ZonedDateTime = maxExpirationDate(),
    id: Id = Id.generate()
) : Message(
    id, message, senderEndpoint, receiverEndpoint, creationDate, expirationDate
)
