package tech.relaycorp.sdk

import tech.relaycorp.sdk.endpoints.ExternalEndpoints
import tech.relaycorp.sdk.endpoints.InternalEndpoints
import tech.relaycorp.sdk.gateway.Gateway
import tech.relaycorp.sdk.messaging.Messaging

object RelaynetClient {

    var Gateway = Gateway()
        private set

    var InternalEndpoints = InternalEndpoints()
        private set

    var ExternalEndpoints = ExternalEndpoints()
        private set

    var Messaging = Messaging()
        private set
}

