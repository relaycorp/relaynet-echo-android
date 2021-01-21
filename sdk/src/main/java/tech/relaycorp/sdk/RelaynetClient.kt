package tech.relaycorp.sdk

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import tech.relaycorp.sdk.models.FirstPartyEndpoint
import tech.relaycorp.sdk.models.IncomingMessage
import tech.relaycorp.sdk.models.OutgoingMessage

object RelaynetClient {

    private lateinit var context: Context

    // We need to ensure the instance is always built inside a suspended function or a flow
    // because building the client might involve I/O operations.
    internal var instanceBuilder: (Context) -> Client = { Client(it) }
    private val instance by lazy { instanceBuilder(context) }

    fun setup(context: Context) {
        this.context = context
    }

    // Gateway

    suspend fun bind() = instance.bind()
    fun unbind() = instance.unbind()

    // First-Party Endpoints

    suspend fun registerEndpoint() = instance.registerEndpoint()
    suspend fun removeEndpoint(endpoint: FirstPartyEndpoint) = instance.removeEndpoint(endpoint)
    suspend fun listEndpoints() = instance.listEndpoints()

    // Third-Party Endpoints

    suspend fun issueAuthorization() = instance.issueAuthorization()
    suspend fun exportPublicKey() = instance.exportPublicKey()

    // Messaging

    suspend fun sendMessage(message: OutgoingMessage) = instance.send(message)
    fun receiveMessages(): Flow<IncomingMessage> =
        { instance }.asFlow()
            .flatMapConcat { it.receive() }

    // Internal

    internal suspend fun handleIncomingMessageNotification() =
        instance.handleIncomingMessageNotification()

}

