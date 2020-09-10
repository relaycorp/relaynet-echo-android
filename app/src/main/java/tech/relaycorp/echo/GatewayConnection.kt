package tech.relaycorp.echo

import android.content.Context
import android.content.ServiceConnection
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tech.relaycorp.poweb.PoWebClient
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistrationRequest
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GatewayConnection
@Inject constructor(
    private val config: EndpointConfig,
    private val context: Context
) {

    private var syncConnection: ServiceConnection? = null

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            if (config.endpointCertificate == null) {
                register()
            } else {
                bindSync()
            }
        }
    }

    fun disconnect() {
        unbindSync()
    }

    private suspend fun register() {
        val keyPair = config.endpointKeyPair
        val preAuthSerialized = preRegister()
        val request = PrivateNodeRegistrationRequest(keyPair.public, preAuthSerialized)
        val requestSerialized = request.serialize(keyPair.private)

        bindSync()

        val poweb = PoWebClient.initLocal(port = Relaynet.POWEB_PORT)
        val pnr = poweb.registerNode(requestSerialized)
        config.endpointCertificate = pnr.privateNodeCertificate
        config.gatewayCertificate = pnr.gatewayCertificate
    }

    private suspend fun preRegister(): ByteArray {
        val bindResult = context.suspendBindService(
            Relaynet.GATEWAY_PACKAGE,
            Relaynet.GATEWAY_PRE_REGISTER_COMPONENT
        )
        val serviceConnection = bindResult.first
        val binder = bindResult.second

        return suspendCoroutine { cont ->
            val request = Message.obtain(null, PREREGISTRATION_REQUEST)
            request.replyTo = Messenger(object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    if (msg.what != REGISTRATION_AUTHORIZATION) {
                        cont.resumeWithException(Exception("pre-register failed"))
                    }
                    cont.resume(msg.data.getByteArray("auth")!!)
                    context.unbindService(serviceConnection)
                }
            })
            Messenger(binder).send(request)
        }
    }

    private suspend fun bindSync() {
        val bindResult = context.suspendBindService(
            Relaynet.GATEWAY_PACKAGE,
            Relaynet.GATEWAY_SYNC_COMPONENT
        )
        syncConnection = bindResult.first
        delay(1_000) // Wait for server to start
    }

    private fun unbindSync() {
        syncConnection?.let { context.unbindService(it) }
    }


    companion object {
        private const val PREREGISTRATION_REQUEST = 1
        private const val REGISTRATION_AUTHORIZATION = 2
    }
}
