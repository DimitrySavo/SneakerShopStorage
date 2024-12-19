package com.example.sneakershopstorage.utils

import com.example.sneakershopstorage.model.ScanResult
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanOptions

object ScannerHelper {
    private val cryptoManager: CryptoManager = CryptoManager()

    fun createScanOptions(): ScanOptions {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Просканируйте код для авторизации")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        return options
    }

    fun handleScanResult(cipherText: String?): ScanResult? {
        return cipherText?.let {
            val decryptedText = cryptoManager.decrypt(it)
            Gson().fromJson(decryptedText, ScanResult::class.java)
        }
    }
}