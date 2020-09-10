package tech.relaycorp.echo

import android.content.Context
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
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {

    var endpointPrivateKey: PrivateKey?
        get() = sharedPreferences.getString("private_key", null)?.toPrivateKey()
        set(value) {
            sharedPreferences.edit().putString("private_key", value?.toEncodedString()).apply()
        }

    var endpointKeyPair: KeyPair
        get() = endpointPrivateKey?.toKeyPair()
            ?: generateRSAKeyPair().also { endpointKeyPair = it }
        set(value) {
            endpointPrivateKey = value.private
        }

    var endpointCertificate
        get() = sharedPreferences.getString("certificate", null)?.toCertificate()
        set(value) {
            sharedPreferences.edit().putString("certificate", value?.toEncodedString()).apply()
        }

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
