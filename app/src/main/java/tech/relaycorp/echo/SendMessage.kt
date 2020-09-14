package tech.relaycorp.echo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tech.relaycorp.poweb.PoWebClient
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
            val parcel = Parcel(
                recipientAddress = Relaynet.RECIPIENT,
                payload = message.toByteArray(Charset.forName("UTF-8")),
                senderCertificate = endpointConfig.endpointCertificate!!,
                messageId = UUID.randomUUID().toString(),
                creationDate = ZonedDateTime.now(),
                ttl = 1.hours.toInt(TimeUnit.SECONDS),
                senderCertificateChain = setOf(endpointConfig.gatewayCertificate!!)
            )

            PoWebClient.initLocal(Relaynet.POWEB_PORT)
                .deliverParcel(parcel.serialize(endpointConfig.endpointPrivateKey!!))

            messageRepository.receive(EchoMessage(System.currentTimeMillis(), message))
        }
    }
}
