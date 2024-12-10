package com.example.sneakershopstorage.utils

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.security.KeyStore
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
}