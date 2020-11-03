package tech.relaycorp.echo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tech.relaycorp.poweb.PoWebClient
import tech.relaycorp.relaynet.bindings.pdc.Signer
import tech.relaycorp.relaynet.bindings.pdc.StreamingMode
import tech.relaycorp.relaynet.messages.Parcel
import javax.inject.Inject

class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var gatewayConnection: GatewayConnection

    @Inject
    lateinit var endpointConfig: EndpointConfig

    @Inject
    lateinit var receiveMessage: ReceiveMessage

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context?, intent: Intent?) {
        (context?.applicationContext as? App)?.component?.inject(this) ?: return

        scope.launch {
            gatewayConnection.connect()

            val poweb = PoWebClient.initLocal(Relaynet.POWEB_PORT)
            val nonceSigner = Signer(
                endpointConfig.endpointCertificate!!, endpointConfig.endpointPrivateKey!!
            )

            poweb
                .collectParcels(arrayOf(nonceSigner), StreamingMode.CloseUponCompletion)
                .collect { parcelCollection ->
                    val parcel = Parcel.Companion.deserialize(parcelCollection.parcelSerialized)
                    receiveMessage.receive(EchoMessage.fromParcel(parcel))
                    parcelCollection.ack()
                }

            poweb.close()

            gatewayConnection.disconnect()
        }
    }
}
