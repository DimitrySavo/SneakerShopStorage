package com.example.sneakershopstorage.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager {
    companion object {
        private const val GCM_TAG_LENGTH = 128
        private const val AES_MODE = "AES/GCM/NoPadding"

        private const val SECRET_PASSWORD = "MySuperSecretPassphrase"
        private val SALT = "FixedSaltValueForAllDevices".toByteArray(Charsets.UTF_8)
    }

    private fun getKey(): SecretKey {
        return deriveKeyFromPassword(SECRET_PASSWORD, SALT)
    }

    fun encrypt(plainText: String): String {
        val secretKey = getKey()
        val cipher = Cipher.getInstance(AES_MODE)

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = iv + cipherText

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(encryptedBase64: String): String {
        val secretKey = getKey()
        val encryptedBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)

        val ivSize = 12
        val iv = encryptedBytes.copyOfRange(0, ivSize)
        val cipherBytes = encryptedBytes.copyOfRange(ivSize, encryptedBytes.size)

        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        val plainTextBytes = cipher.doFinal(cipherBytes)
        return String(plainTextBytes, Charsets.UTF_8)
    }

    private fun deriveKeyFromPassword(password: String, salt: ByteArray, keyLength: Int = 256): SecretKey {
        val factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 100000, keyLength)
        val tmp = factory.generateSecret(spec)
        return javax.crypto.spec.SecretKeySpec(tmp.encoded, "AES")
    }
}