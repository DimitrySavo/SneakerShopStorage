package com.example.sneakershopstorage

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.model.ScanResult
import com.example.sneakershopstorage.model.ShoeScanStructure
import com.example.sneakershopstorage.modules.CurrentEmployee
import com.example.sneakershopstorage.screens.ShoeScreen
import com.example.sneakershopstorage.screens.UserOrdersScreen
import com.example.sneakershopstorage.ui.theme.SneakerShopStorageTheme
import com.example.sneakershopstorage.utils.CryptoManager
import com.example.sneakershopstorage.utils.Routes
import com.example.sneakershopstorage.utils.ScanData
import com.example.sneakershopstorage.utils.ScannerHelper
import com.example.sneakershopstorage.utils.ScanDataBus
import com.example.sneakershopstorage.viewmodels.ShoeViewModel
import com.example.sneakershopstorage.viewmodels.UserOrdersViewModel
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : ComponentActivity() {
    private val cryptoManager = CryptoManager()
    private val employee : CurrentEmployee by inject()
    private val scanDataBus : ScanDataBus by inject()
    private val shoeViewModel : ShoeViewModel by viewModel()
    private val userViewModel: UserOrdersViewModel by viewModel { parametersOf(::scan) }
    private lateinit var navController: NavHostController

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Result is null", Toast.LENGTH_SHORT).show()
        } else {
            val scanResult = ScannerHelper.handleScanResult(result.contents)
            scanResult?.let {
                Log.i("Scan launcher", "Get into scan launcher")
                scanDataBus.handleScanResultData(it)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            scanDataBus.scanResult.collect { scanData ->
                when(scanData) {
                    is ScanData.EmployeeData -> {
                        employee.employee = scanData.employee
                    }
                    else -> {
                        Log.i("Scan collecting for mainActivity", "Not employee type scanned")
                    }
                }
            }
        }

        setContent {
            navController = rememberNavController()
            val currentEmployee: CurrentEmployee by remember {
                mutableStateOf(employee)
            }


            val init = shoeViewModel.shoe
            val initUser = userViewModel.userOrders


            SneakerShopStorageTheme {
                Column {
                    Surface(
                        modifier = Modifier
                            .weight(10f)
                            .fillMaxWidth()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Routes.AUTHORIZE
                        ) {
                            composable(route = Routes.AUTHORIZE) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Column(
                                       modifier = Modifier
                                           .fillMaxWidth(0.4f)
                                           .padding(bottom = 40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = ""
                                        )
                                        Text(
                                            text = if(currentEmployee.employee == null) {
                                                "You are not authorized"
                                            } else {
                                                currentEmployee.employee?.name ?: ""
                                            }
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            scan()
                                        }
                                    ) {
                                        Text(
                                            text = "Scan QR"
                                        )
                                    }
                                }
                            }

                            composable(route = Routes.SHOE) {
                                ShoeScreen(viewModel= shoeViewModel)
                            }

                            composable(route = Routes.USERORDERS) {
                                UserOrdersScreen(viewModel = userViewModel)
                            }

                            composable(route = Routes.QRGENERATE) {
                                val content: String = Gson().toJson(
                                    ScanResult(
                                        type = ScanResult.SHOE_TYPE,
                                        content = Gson().toJson(
                                            ShoeScanStructure(
                                                shoeId = "ET96Bi8yKUJzOEGHCCCI",
                                                shoeSize = "size-20"
                                            )
                                        )
                                    )
                                )

                                val userWithOrders: String = Gson().toJson(
                                    ScanResult(
                                        type = ScanResult.USER_TYPE,
                                        content = Gson().toJson(
                                            "QZmJXQy5L7cDvv8XlnO29W9eQ2F3"
                                        )
                                    )
                                )

                                val employeeFromStorageOne: String = Gson().toJson(
                                    ScanResult(
                                        type = ScanResult.EMPLOYEE_TYPE,
                                        content = Gson().toJson(
                                            Employee(
                                                "2",
                                                "Edward",
                                                "Elrick",
                                                "storageOne")
                                        )
                                    )
                                )

                                val employeeFromStorageTwo: String = Gson().toJson(
                                    ScanResult(
                                        type = ScanResult.EMPLOYEE_TYPE,
                                        content = Gson().toJson(
                                            Employee(
                                                "3",
                                                "Alfons",
                                                "Elrick",
                                                "storageTwo")
                                        )
                                    )
                                )

                                QRCodeScreen(
                                    content = cryptoManager.encrypt(employeeFromStorageTwo)
                                )
                            }
                        }


                    }
                    BottomAppBar(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                if(currentEmployee.employee != null) {
                                    navController.navigate(Routes.USERORDERS)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = ""
                            )
                        }

                        IconButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                navController.navigate(Routes.AUTHORIZE)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = ""
                            )
                        }

                        IconButton(
                            modifier = Modifier
                                .weight(1f),
                            onClick = {
                                if(currentEmployee.employee != null) {
                                    navController.navigate(Routes.SHOE)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = ""
                            )
                        }
                    }
                }

            }
        }
    }

    private fun scan() {
        val options = ScannerHelper.createScanOptions()
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

