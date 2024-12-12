package com.example.sneakershopstorage

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.screens.HelloScreen
import com.example.sneakershopstorage.ui.theme.SneakerShopStorageTheme
import com.example.sneakershopstorage.utils.CryptoManager
import com.example.sneakershopstorage.utils.Routes
import com.example.sneakershopstorage.viewmodels.ViewModel
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


class MainActivity : ComponentActivity() {
    private val cryptoManager = CryptoManager()
    private val viewModel by viewModels<ViewModel>()
    private lateinit var navController: NavHostController

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Result is null", Toast.LENGTH_SHORT).show()
        } else {
            val cipherText = result.contents
            Log.i("Scan", result.contents)
            val decryptedText = cryptoManager.decrypt(cipherText)
            decryptedText.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                val employee: Employee = Gson().fromJson(decryptedText, Employee::class.java)
                viewModel.setEmployee(employee)
                navController.navigate(Routes.HELLO)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navController = rememberNavController()
            val employee by viewModel.employee.collectAsState()

            SneakerShopStorageTheme {
                LaunchedEffect(Unit) {
                    scan()
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HELLO
                    ) {
                        composable(route = Routes.HELLO) {
                            Log.i(Routes.HELLO, "Go to hello screen")
                            HelloScreen(employee = employee!!)
                        }
                        composable(route = Routes.QRGENERATE) {
                            Log.i(Routes.QRGENERATE, "Go to qr gen window")
                            val jsonString = Gson().toJson(
                                Employee(
                                    "1",
                                    "NewName",
                                    "NewSurname",
                                    "storage 1")
                            )

                            Log.i("Before qr generated",  jsonString)

                            QRCodeScreen(
                                content = cryptoManager.encrypt(jsonString)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun scan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        scanLauncher.launch(options)
    }
}

fun generateQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}

@Composable
fun QRCodeScreen(content: String) {
    val qrBitmap = generateQRCode(content)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

