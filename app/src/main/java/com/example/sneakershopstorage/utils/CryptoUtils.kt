package com.example.sneakershopstorage.utils

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class CryptoUtils {

    fun generateSecretKey(alias: String) {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)

        val secretKey: SecretKey = keyGen.generateKey()

        val entry = KeyStore.SecretKeyEntry(secretKey)
        val protectionParameters =
            KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()

        ks.setEntry(alias, entry, protectionParameters)
    }

    fun encrypt(alias: String, plainText: String): String? {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val secretKey = ks.getKey(alias, null)

        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val cipherText = Base64.encodeToString(cipher.doFinal(plainText.toByteArray()), Base64.DEFAULT)
            val iv = Base64.encodeToString(cipher.iv, Base64.DEFAULT)

            return "${cipherText}.$iv"
        } catch (e: Exception) {
            Log.e("Encrypt", "$e")
            null
        }
    }
}