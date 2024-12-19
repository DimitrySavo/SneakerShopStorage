package com.example.sneakershopstorage.utils

import android.util.Log
import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.model.ScanResult
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


sealed class ScanData() {
    data class EmployeeData(val employee: Employee) : ScanData()
    data class ShoeData(val shoe: String) : ScanData()
    data class UserData(val orders: String) : ScanData()
}

class ScanDataBus() {
    private val _scanResult = MutableSharedFlow<ScanData>()
    val scanResult = _scanResult.asSharedFlow()

    fun handleScanResultData(scanResult: ScanResult)= CoroutineScope(Dispatchers.IO).launch {
        Log.i("handleScanResultData", "Get into handleScanResultData function")
        when(scanResult.type) {
            ScanResult.SHOE_TYPE -> {
                Log.i("handleScanResultData", "Get into handleScanResultData function shoe type")
                val shoeId = scanResult.content
                _scanResult.emit(ScanData.ShoeData(shoeId))
            }
            ScanResult.EMPLOYEE_TYPE -> {
                Log.i("handleScanResultData", "Get into handleScanResultData function employee type")
                val employee = Gson().fromJson(scanResult.content, Employee::class.java)
                _scanResult.emit(ScanData.EmployeeData(employee))
            }
            else -> {
                Log.e("Scan result", "Unknown type")
            }
        }
    }
}