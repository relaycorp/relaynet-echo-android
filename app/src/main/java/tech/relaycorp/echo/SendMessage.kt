package tech.relaycorp.echo

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.relaycorp.sdk.RelaynetClient
import tech.relaycorp.sdk.models.OutgoingMessage
import tech.relaycorp.sdk.models.ThirdPartyEndpoint
import java.nio.charset.Charset
import javax.inject.Inject

class SendMessage
@Inject constructor() {

    suspend fun send(message: String): Boolean =
        withContext(Dispatchers.IO) {

            val endpoints = RelaynetClient.listEndpoints()
            val senderEndpoint = endpoints.first()
            val receiverEndpoint = endpoints.last()

            return@withContext try {
                RelaynetClient.sendMessage(
                    OutgoingMessage(
                        message = message.toByteArray(Charset.forName("UTF-8")),
                        senderEndpoint = senderEndpoint,
                        receiverEndpoint = ThirdPartyEndpoint(receiverEndpoint.address)
                    )
                )
                true
            } catch (e: Exception) {
                Log.e("SendMessage", "Error sending message", e)
                false
            }
        }
}
