package tech.relaycorp.echo

import tech.relaycorp.relaynet.messages.Parcel
import java.nio.charset.Charset


data class EchoMessage(
    val time: Long,
    val message: String
) {
    companion object {
        fun fromParcel(parcel: Parcel) = EchoMessage(
            parcel.creationDate.toInstant().toEpochMilli(),
            parcel.payload.toString(Charset.forName("UTF-8"))
        )
    }
}
