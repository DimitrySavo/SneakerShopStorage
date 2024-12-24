package com.example.sneakershopstorage.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sneakershopstorage.model.Order
import com.example.sneakershopstorage.model.services.FirebaseService
import com.example.sneakershopstorage.modules.ReturningState
import com.example.sneakershopstorage.utils.FunctionResult
import com.example.sneakershopstorage.utils.ScanData
import com.example.sneakershopstorage.utils.ScanDataBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserOrdersViewModel(
    private val scanDataBus: ScanDataBus,
    private val firebaseService: FirebaseService,
    private val isReturningState: ReturningState,
    private val scanFunction: () -> Unit
) : ViewModel() {
    private val _userOrders = MutableStateFlow<List<Order>>(emptyList())
    val userOrders = _userOrders.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    init {
        Log.d("UserOrders viewModel", "create an userOrders viewModel")
        viewModelScope.launch {
            scanDataBus.scanResult.collect { scanData ->
                when(scanData) {
                    is ScanData.UserData -> {
                        when(val result = firebaseService.getUserOrders(scanData.userId)) {
                            is FunctionResult.Success -> {
                                _userOrders.value = result.data
                                _userId.value = scanData.userId
                                Log.d("ScanData", "User orders = ${result.data}")
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

    fun returnShoe(shoeId: String, orderId: String) = viewModelScope.launch {
        isReturningState.isReturning = true
        scanFunction()
        val firstScan = scanDataBus.scanResult.first()

        if (firstScan is ScanData.ShoeData && firstScan.shoe.shoeId == shoeId) {
            firebaseService.returnShoe(shoeId, orderId, _userId.value ?: "")
        } else {
            Log.i("ReturnShoe", "Wrong shoe scanned. Exiting.")
        }
        isReturningState.isReturning = false
    }
}