package com.example.sneakershopstorage.utils

import android.content.Context
import android.util.Base64
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptoManager(private val context: Context) {

    companion object {
        private const val KEY_ALIAS = "my_symmetric_key"
        private const val PREFS_NAME = "crypto_prefs"
        private const val PREFS_KEY = "aes_key"

        private const val AES_KEY_SIZE = 256 // размер ключа в битах, можно 128/192/256
        private const val GCM_TAG_LENGTH = 128 // длина тега аутентичности в битах для GCM
        private const val AES_MODE = "AES/GCM/NoPadding"
    }

    /**
     * Генерирует и сохраняет симметричный ключ AES, если он ещё не существует.
     * Если ключ уже есть, возвращает существующий.
     */
    fun getOrCreateKey(): SecretKey {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedKeyBase64 = prefs.getString(PREFS_KEY, null)

        return if (storedKeyBase64 != null) {
            val decodedKey = Base64.decode(storedKeyBase64, Base64.DEFAULT)
            SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
        } else {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(AES_KEY_SIZE)
            val newKey = keyGenerator.generateKey()
            val encodedKey = newKey.encoded
            val encodedKeyBase64 = Base64.encodeToString(encodedKey, Base64.DEFAULT)

            prefs.edit().putString(PREFS_KEY, encodedKeyBase64).apply()
            newKey
        }
    }

    /**
     * Шифрует строку, возвращая результат в Base64.
     * Формат результата: Base64(IV + ciphertext)
     */
    fun encrypt(plainText: String): String {
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance(AES_MODE)

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val cipherText = cipher.doFinal(plainText.toByteArray(Charset.forName("UTF-8")))

        val combined = iv + cipherText

        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    /**
     * Дешифрует Base64-строку, зашифрованную методом encrypt().
     * Строка должна содержать IV и шифротекст.
     */
    fun decrypt(encryptedBase64: String): String {
        val secretKey = getOrCreateKey()
        val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)

        val ivSize = 12
        val iv = encryptedBytes.copyOfRange(0, ivSize)
        val cipherBytes = encryptedBytes.copyOfRange(ivSize, encryptedBytes.size)

        val cipher = Cipher.getInstance(AES_MODE)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        val plainTextBytes = cipher.doFinal(cipherBytes)
        return String(plainTextBytes, Charset.forName("UTF-8"))
    }
}