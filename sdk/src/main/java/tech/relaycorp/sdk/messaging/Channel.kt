package tech.relaycorp.sdk.messaging

import tech.relaycorp.sdk.Client

data class Channel(
    val internalEndpoint: Client.InternalEndpoint,
    val externalEndpoint: Client.ExternalEndpoint
)
