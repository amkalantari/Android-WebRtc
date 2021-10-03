package com.core.utils

import android.util.Base64
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

interface SecurityHelper {
    fun encrypt(textToEncrypt: String): String
    fun decrypt(textToDecrypt: String): String
}


class SecurityHelperImpl : SecurityHelper {

    private val cypherInstance = "AES/CBC/PKCS5Padding"
    private val secretKeyInstance = "PBKDF2WithHmacSHA1"
    private val initializationVector = "8119745113154120"

    override fun encrypt(textToEncrypt: String): String {
        return if (textToEncrypt.isNotEmpty()) {
            val keySpec = SecretKeySpec(getRaw(), "AES")
            val cipher = Cipher.getInstance(cypherInstance)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(initializationVector.toByteArray()))
            val encrypted = cipher.doFinal(textToEncrypt.toByteArray())
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } else
            ""
    }

    override fun decrypt(textToDecrypt: String): String {
        return if (textToDecrypt.isNotEmpty()) {
            val encrytedBytes = Base64.decode(textToDecrypt, Base64.DEFAULT)
            val skeySpec = SecretKeySpec(getRaw(), "AES")
            val cipher = Cipher.getInstance(cypherInstance)
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, IvParameterSpec(initializationVector.toByteArray()))
            val decrypted = cipher.doFinal(encrytedBytes)
            String(decrypted, Charset.forName("UTF-8"))
        } else
            ""
    }

    private fun getRaw(): ByteArray {
        try {
            val factory = SecretKeyFactory.getInstance(secretKeyInstance)
            val spec = PBEKeySpec("sampleText".toCharArray(), "exampleSalt".toByteArray(), 10, 128)
            return factory.generateSecret(spec).encoded
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }


}