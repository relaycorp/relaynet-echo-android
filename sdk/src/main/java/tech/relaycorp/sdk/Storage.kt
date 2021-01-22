package tech.relaycorp.sdk

import android.content.SharedPreferences
import android.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import tech.relaycorp.relaynet.wrappers.x509.Certificate
import java.security.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.RSAPrivateCrtKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import javax.inject.Inject

class Storage
@Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private fun getPrivateKey(endpoint: String) =
        sharedPreferences.getString("private_key_$endpoint", null)
            ?.toPrivateKey()

    private fun setPrivatePair(endpoint: String, privateKey: PrivateKey?) {
        sharedPreferences.edit()
            .putString("private_key_$endpoint", privateKey?.toEncodedString())
            .apply()
    }

    fun getKeyPair(endpoint: String) =
        getPrivateKey(endpoint)?.toKeyPair()

    fun setKeyPair(endpoint: String, keyPair: KeyPair?) {
        setPrivatePair(endpoint, keyPair?.private)
    }

    fun getCertificate(endpoint: String) =
        sharedPreferences.getString("certificate_$endpoint", null)?.toCertificate()

    fun setCertificate(endpoint: String, certificate: Certificate?) {
        sharedPreferences.edit()
            .putString("certificate_$endpoint", certificate?.toEncodedString())
            .apply()
    }

    fun getGatewayCertificate() =
        sharedPreferences.getString("gateway_certificate", null)?.toCertificate()

    fun setGatewayCertificate(certificate: Certificate?) {
        sharedPreferences.edit()
            .putString("gateway_certificate", certificate?.toEncodedString())
            .apply()
    }

    fun listEndpoints() =
        sharedPreferences.all.keys
            .filter { it.startsWith("certificate_") }
            .map { it.replace(Regex("^certificate_"), "") }

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
