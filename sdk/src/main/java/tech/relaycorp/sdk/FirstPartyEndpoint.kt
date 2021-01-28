package tech.relaycorp.sdk

import tech.relaycorp.relaynet.wrappers.generateRSAKeyPair
import tech.relaycorp.relaynet.wrappers.privateAddress
import tech.relaycorp.relaynet.wrappers.x509.Certificate
import java.security.KeyPair

class FirstPartyEndpoint
private constructor(
    internal val keyPair: KeyPair,
    internal val certificate: Certificate,
    internal val gatewayCertificate: Certificate
) : Endpoint {

    override val address get() = keyPair.public.privateAddress

    suspend fun remove() {
        Storage.deleteKeyPair(address)
        Storage.deleteCertificate(address)
    }

    private suspend fun store() {
        Storage.setKeyPair(address, keyPair)
        Storage.setCertificate(address, certificate)
        Storage.setGatewayCertificate(gatewayCertificate)
    }

    companion object {
        suspend fun register(): FirstPartyEndpoint {
            val keyPair = generateRSAKeyPair()
            val certificates = GatewayClient.registerEndpoint(keyPair)
            val endpoint = FirstPartyEndpoint(keyPair, certificates.first, certificates.second)
            endpoint.store()
            return endpoint
        }

        suspend fun load(address: String): FirstPartyEndpoint? {
            return Storage.getKeyPair(address)?.let { keyPair ->
                Storage.getCertificate(address)?.let { certificate ->
                    Storage.getGatewayCertificate()?.let { gwCertificate ->
                        FirstPartyEndpoint(
                            keyPair,
                            certificate,
                            gwCertificate
                        )
                    }
                }
            }
        }
    }
}