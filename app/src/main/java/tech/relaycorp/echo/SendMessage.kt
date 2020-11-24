package tech.relaycorp.echo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.relaycorp.poweb.PoWebClient
import tech.relaycorp.relaynet.bindings.pdc.Signer
import tech.relaycorp.relaynet.issueParcelDeliveryAuthorization
import tech.relaycorp.relaynet.messages.Parcel
import java.nio.charset.Charset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.hours

class SendMessage
@Inject constructor(
    private val messageRepository: MessageRepository,
    private val endpointConfig: EndpointConfig
) {

    suspend fun send(message: String) {
        withContext(Dispatchers.IO) {
            val recipientCertificate = endpointConfig.endpointCertificate!!

            val senderCertificate = issueParcelDeliveryAuthorization(
                endpointConfig.endpointKeyPair.public,
                endpointConfig.endpointKeyPair.private,
                recipientCertificate.expiryDate,
                recipientCertificate,
                recipientCertificate.startDate
            )

            val parcel = Parcel(
                recipientAddress = endpointConfig.endpointAddress!!,
                payload = message.toByteArray(Charset.forName("UTF-8")),
                senderCertificate = senderCertificate,
                messageId = UUID.randomUUID().toString(),
                creationDate = ZonedDateTime.now(),
                ttl = 1.hours.toInt(TimeUnit.SECONDS),
                senderCertificateChain = setOf(
                    recipientCertificate,
                    endpointConfig.gatewayCertificate!!
                )
            )

            try {
                PoWebClient.initLocal(Relaynet.POWEB_PORT)
                    .deliverParcel(
                        parcel.serialize(endpointConfig.endpointPrivateKey!!),
                        Signer(
                            endpointConfig.endpointCertificate!!,
                            endpointConfig.endpointPrivateKey!!
                        )
                    )
                messageRepository.receive(EchoMessage(System.currentTimeMillis(), message))
            } catch (e: Exception) {
                Log.e("SendMessage", "Error sending message", e)
            }
        }
    }
}
