package com.example.sneakershopstorage.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sneakershopstorage.model.Order
import com.example.sneakershopstorage.model.services.FirebaseService
import com.example.sneakershopstorage.utils.FunctionResult
import com.example.sneakershopstorage.utils.ScanData
import com.example.sneakershopstorage.utils.ScanDataBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserOrdersViewModel(
    scanDataBus: ScanDataBus,
    firebaseService: FirebaseService
) : ViewModel() {
    init {
        Log.d("UserOrders viewModel", "create an userOrders viewModel")
        viewModelScope.launch {
            scanDataBus.scanResult.collect { scanData ->
                when(scanData) {
                    is ScanData.UserData -> {
                        when(val result = firebaseService.getUserOrders(scanData.userId)) {
                            is FunctionResult.Success -> {
                                _userOrders.value = result.data
                            }
                            is FunctionResult.Error -> {
                                Log.e("Getting order", "Error: ${result.message}")
                            }
                        }
                    }
                    else -> Log.i("UserOrdersViewModel", "Get not user type")
                }
            }
        }
    }

    private val _userOrders = MutableStateFlow<List<Order>>(emptyList())
    val userOrders = _userOrders.asStateFlow()
}