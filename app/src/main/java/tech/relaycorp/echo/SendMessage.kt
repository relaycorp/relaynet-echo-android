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

    suspend fun send(message: String): Boolean =
        withContext(Dispatchers.IO) {
            val recipientCertificate = endpointConfig.endpointReceiverCertificate!!

            val senderCertificate = issueParcelDeliveryAuthorization(
                endpointConfig.endpointSenderKeyPair.public,
                endpointConfig.endpointReceiverKeyPair.private,
                recipientCertificate.expiryDate,
                recipientCertificate,
                recipientCertificate.startDate
            )

            val parcel = Parcel(
                recipientAddress = endpointConfig.endpointReceiverAddress!!,
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

            return@withContext try {
                PoWebClient.initLocal(Relaynet.POWEB_PORT)
                    .deliverParcel(
                        parcel.serialize(endpointConfig.endpointSenderPrivateKey!!),
                        Signer(
                            endpointConfig.endpointSenderCertificate!!,
                            endpointConfig.endpointSenderPrivateKey!!
                        )
                    )
                true
            } catch (e: Exception) {
                Log.e("SendMessage", "Error sending message", e)
                false
            }
        }
}
