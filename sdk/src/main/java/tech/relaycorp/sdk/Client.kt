package tech.relaycorp.sdk

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal class Client {

    // Gateway

    suspend fun bind() {}
    suspend fun unbind() {}

    // First-Party Endpoints

    suspend fun registerEndpoint(): FirstPartyEndpoint = FirstPartyEndpoint("")
    suspend fun removeEndpoint(endpoint: FirstPartyEndpoint) {}
    suspend fun listEndpoints(): List<FirstPartyEndpoint> = emptyList()

    // Third-Party Endpoints

    suspend fun issueAuthorization() = Unit
    suspend fun exportPublicKey(): ByteArray = ByteArray(0)

    // Messaging

    suspend fun send(message: OutgoingMessage) {}
    fun receive(): Flow<IncomingMessage> = emptyFlow()

}
