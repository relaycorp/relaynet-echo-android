package tech.relaycorp.sdk.models

abstract class Endpoint(val address: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Endpoint) return false
        if (address != other.address) return false
        return true
    }

    override fun hashCode() = address.hashCode()
}

class FirstPartyEndpoint internal constructor(address: String) : Endpoint(address)

class ThirdPartyEndpoint(address: String) : Endpoint(address)
class PrivateThirdPartyEndpoint(address: String) : Endpoint(address)
class PublicThirdPartyEndpoint(address: String) : Endpoint(address)
