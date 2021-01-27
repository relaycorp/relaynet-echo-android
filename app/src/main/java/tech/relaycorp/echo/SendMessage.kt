package tech.relaycorp.echo

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.relaycorp.sdk.FirstPartyEndpoint
import tech.relaycorp.sdk.GatewayClient
import tech.relaycorp.sdk.OutgoingMessage
import tech.relaycorp.sdk.PrivateThirdPartyEndpoint
import java.nio.charset.Charset
import javax.inject.Inject

class SendMessage
@Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    suspend fun send(message: String): Boolean =
        withContext(Dispatchers.IO) {

            val senderEndpoint =
                FirstPartyEndpoint.load(sharedPreferences.getString("sender", null)!!)!!
            val receiverEndpoint =
                FirstPartyEndpoint.load(sharedPreferences.getString("receiver", null)!!)!!

            return@withContext try {
                GatewayClient.sendMessage(
                    OutgoingMessage(
                        message = message.toByteArray(Charset.forName("UTF-8")),
                        senderEndpoint = senderEndpoint,
                        receiverEndpoint = PrivateThirdPartyEndpoint(receiverEndpoint.address)
                    )
                )
                true
            } catch (e: Exception) {
                Log.e("SendMessage", "Error sending message", e)
                false
            }
        }
}
