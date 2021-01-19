package tech.relaycorp.sdk.endpoints

abstract class Endpoint
class PrivateEndpoint : Endpoint()
class PublicEndpoint : Endpoint()

data class InternalEndpoint(val endpoint: PrivateEndpoint)
data class ExternalEndpoint(val endpoint: Endpoint)
