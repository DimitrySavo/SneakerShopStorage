package com.example.sneakershopstorage.utils

import android.util.Log
import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.model.ScanResult
import com.example.sneakershopstorage.model.ShoeScanStructure
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


sealed class ScanData() {
    data class EmployeeData(val employee: Employee) : ScanData()
    data class ShoeData(val shoe: ShoeScanStructure) : ScanData()
    data class UserData(val userId: String) : ScanData()
}

class ScanDataBus() {
    private val _scanResult = MutableSharedFlow<ScanData>()
    val scanResult = _scanResult.asSharedFlow()

    fun handleScanResultData(scanResult: ScanResult)= CoroutineScope(Dispatchers.IO).launch {
        Log.i("handleScanResultData", "Get into handleScanResultData function")
        when(scanResult.type) {
            ScanResult.SHOE_TYPE -> {
                Log.i("handleScanResultData", "Get into handleScanResultData function shoe type")
                val shoeScanData = Gson().fromJson(scanResult.content, ShoeScanStructure::class.java)
                _scanResult.emit(ScanData.ShoeData(shoeScanData))
            }
            ScanResult.EMPLOYEE_TYPE -> {
                Log.i("handleScanResultData", "Get into handleScanResultData function employee type")
                val employee = Gson().fromJson(scanResult.content, Employee::class.java)
                _scanResult.emit(ScanData.EmployeeData(employee))
            }
            ScanResult.USER_TYPE -> {
                Log.i("handleScanResultData", "Get into handleScanResultData function user type")
                val userId = scanResult.content
                _scanResult.emit(ScanData.UserData(userId))
            }
            else -> {
                Log.e("Scan result", "Unknown type")
            }
        }
    }
}