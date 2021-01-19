package tech.relaycorp.sdk.endpoints

// Automatic:
// - Re-register endpoint when the private gateway broadcasts a message saying that its certificate changed
// - Renewal
class InternalEndpoints {

    suspend fun registerEndpoint(): InternalEndpoint = InternalEndpoint(PrivateEndpoint())
    suspend fun removeEndpoint(endpoint: InternalEndpoint) {}
    suspend fun listEndpoints(): List<InternalEndpoint> = emptyList()

}
