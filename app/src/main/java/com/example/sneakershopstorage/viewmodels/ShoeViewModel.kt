package com.example.sneakershopstorage.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sneakershopstorage.model.Shoe
import com.example.sneakershopstorage.model.services.FirebaseService
import com.example.sneakershopstorage.modules.ReturningState
import com.example.sneakershopstorage.utils.ScanData
import com.example.sneakershopstorage.utils.ScanDataBus
import com.example.sneakershopstorage.utils.FunctionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShoeViewModel(
    scanDataBus: ScanDataBus,
    firebaseService: FirebaseService,
    isReturning: ReturningState
): ViewModel() {
    init {
        Log.d("ShoeViewModel", "create an shoe viewModel")
        viewModelScope.launch {
            scanDataBus.scanResult.collect { scanData ->
                Log.i("ScanDataBus", " Value emmited and collected")
                when(scanData) {
                    is ScanData.ShoeData -> {
                        if(!isReturning.isReturning) {
                            when(val result = firebaseService.getShoeByIdAndSize(scanData.shoe.shoeId, scanData.shoe.shoeSize)) {
                                is FunctionResult.Success -> _shoe.value = result.data
                                is FunctionResult.Error -> Log.e("ShoeViewModel", result.message)
                            }
                            Log.d("ShoeViewModel", "Shoe = ${_shoe.value}")
                        }
                    }
                    else -> Log.i("ShoeViewModel", "Get not shoe type")
                }
            }
        }
    }

    private val _shoe = MutableStateFlow<Shoe?>(null)
    val shoe = _shoe.asStateFlow()

}