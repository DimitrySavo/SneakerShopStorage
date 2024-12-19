package com.example.sneakershopstorage.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sneakershopstorage.utils.ScanData
import com.example.sneakershopstorage.utils.ScanDataBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserOrdersViewModel(
    scanDataBus: ScanDataBus
) : ViewModel() {
    init {
        Log.d("UserOrders viewModel", "create an userOrders viewModel")
        viewModelScope.launch {
            scanDataBus.scanResult.collect { scanData ->
                when(scanData) {
                    is ScanData.UserData -> {
                        _userId.value = scanData.orders
                    }
                    else -> Log.i("UserOrdersViewModel", "Get not user type")
                }
            }
        }
    }

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()
}