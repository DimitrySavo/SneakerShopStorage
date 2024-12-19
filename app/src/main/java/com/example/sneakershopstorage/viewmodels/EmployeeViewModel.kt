package com.example.sneakershopstorage.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.utils.ScanData
import com.example.sneakershopstorage.utils.ScanDataBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeViewModel(
    scanDataBus: ScanDataBus
): ViewModel() {
    init {
        Log.d("Employee viewModel", "create an employee viewModel")
        viewModelScope.launch {
            scanDataBus.scanResult.collect { scanData ->
                when(scanData) {
                    is ScanData.EmployeeData -> {
                        _employee.value = scanData.employee
                    }
                    else -> Log.i("EmployeeViewModel", "Get not employee type")
                }
            }
        }
    }

    private val _employee = MutableStateFlow<Employee?>(null)
    val employee = _employee.asStateFlow()

    fun setEmployee(employee: Employee) {
        _employee.value = employee
        Log.i("Employee", "Employee in viewModel ${_employee.value}")
    }
}