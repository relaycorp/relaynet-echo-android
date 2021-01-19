package tech.relaycorp.sdk.endpoints

// Future work:
// - Parcel delivery de-authorisation
// - List authorizations
class ExternalEndpoints {

    suspend fun issueAuthorization(
        request: ParcelDeliveryAuthorizationRequest
    ): ParcelDeliveryAuthorization = ParcelDeliveryAuthorization()

    class ParcelDeliveryAuthorization
    class ParcelDeliveryAuthorizationRequest

    suspend fun exportPublicKey(): ByteArray = ByteArray(0)

}
