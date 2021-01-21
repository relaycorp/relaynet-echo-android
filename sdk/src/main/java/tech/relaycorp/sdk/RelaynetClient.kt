package tech.relaycorp.sdk

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat

object RelaynetClient {

    // We need to ensure the instance is always built inside a suspended function or a flow
    // because building the client might involve I/O operations.
    internal var instanceBuilder: () -> Client = { Client() }
    private val instance by lazy(instanceBuilder)

    // Gateway

    suspend fun bind() = instance.bind()
    suspend fun unbind() = instance.unbind()

    // First-Party Endpoints

    suspend fun registerEndpoint() = instance.registerEndpoint()
    suspend fun removeEndpoint(endpoint: FirstPartyEndpoint) = instance.removeEndpoint(endpoint)
    suspend fun listEndpoints() = instance.listEndpoints()

    // Third-Party Endpoints

    suspend fun issueAuthorization() = instance.issueAuthorization()
    suspend fun exportPublicKey() = instance.exportPublicKey()

    // Messaging

    suspend fun send(message: OutgoingMessage) = instance.send(message)
    fun receive(): Flow<IncomingMessage> = { instance }.asFlow().flatMapConcat { it.receive() }

}

