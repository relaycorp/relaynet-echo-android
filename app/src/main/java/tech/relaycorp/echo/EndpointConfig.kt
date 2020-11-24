package tech.relaycorp.echo

import android.content.SharedPreferences
import android.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tech.relaycorp.relaynet.wrappers.generateRSAKeyPair
import tech.relaycorp.relaynet.wrappers.x509.Certificate
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import javax.inject.Inject

class EndpointConfig
@Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    var endpointSenderPrivateKey: PrivateKey?
        get() = sharedPreferences.getString("sender_private_key", null)?.toPrivateKey()
        set(value) {
            sharedPreferences.edit().putString("sender_private_key", value?.toEncodedString())
                .apply()
        }

    var endpointSenderKeyPair: KeyPair
        get() = endpointSenderPrivateKey?.toKeyPair()
            ?: generateRSAKeyPair().also { endpointSenderKeyPair = it }
        set(value) {
            endpointSenderPrivateKey = value.private
        }

    var endpointReceiverPrivateKey: PrivateKey?
        get() = sharedPreferences.getString("receiver_private_key", null)?.toPrivateKey()
        set(value) {
            sharedPreferences.edit().putString("receiver_private_key", value?.toEncodedString())
                .apply()
        }

    var endpointReceiverKeyPair: KeyPair
        get() = endpointReceiverPrivateKey?.toKeyPair()
            ?: generateRSAKeyPair().also { endpointReceiverKeyPair = it }
        set(value) {
            endpointReceiverPrivateKey = value.private
        }

    var endpointSenderCertificate
        get() = sharedPreferences.getString("sender_certificate", null)?.toCertificate()
        set(value) {
            sharedPreferences.edit().putString("sender_certificate", value?.toEncodedString()).apply()
        }

    val endpointSenderAddress get() = endpointSenderCertificate?.subjectPrivateAddress

    var endpointReceiverCertificate
        get() = sharedPreferences.getString("receiver_certificate", null)?.toCertificate()
        set(value) {
            sharedPreferences.edit().putString("receiver_certificate", value?.toEncodedString()).apply()
        }

    val endpointReceiverAddress get() = endpointReceiverCertificate?.subjectPrivateAddress

    var gatewayCertificate
        get() = sharedPreferences.getString("gateway_certificate", null)?.toCertificate()
        set(value) {
            sharedPreferences.edit().putString("gateway_certificate", value?.toEncodedString())
                .apply()
        }

    private fun PrivateKey.toEncodedString() =
        Base64.encode(encoded, Base64.DEFAULT).decodeToString()

    private fun String.toPrivateKey(): PrivateKey {
        val privateKeySpec = PKCS8EncodedKeySpec(Base64.decode(encodeToByteArray(), Base64.DEFAULT))
        val generator: KeyFactory = KeyFactory.getInstance(KEY_ALGORITHM, bouncyCastleProvider)
        return generator.generatePrivate(privateKeySpec)
    }

    private fun PrivateKey.toKeyPair(): KeyPair {
        val publicKeySpec =
            (this as RSAPrivateCrtKey).run { RSAPublicKeySpec(modulus, publicExponent) }
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, bouncyCastleProvider)
        val publicKey = keyFactory.generatePublic(publicKeySpec)
        return KeyPair(publicKey, this)
    }

    private fun String.toCertificate() =
        Base64.decode(encodeToByteArray(), Base64.DEFAULT)
            .let { Certificate.deserialize(it) }

    private fun Certificate.toEncodedString() =
        Base64.encode(serialize(), Base64.DEFAULT).decodeToString()

    companion object {
        private const val KEY_ALGORITHM = "RSA"
        private val bouncyCastleProvider = BouncyCastleProvider()
    }
}
