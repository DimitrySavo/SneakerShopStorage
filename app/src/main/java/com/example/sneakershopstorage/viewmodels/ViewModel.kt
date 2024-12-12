package com.example.sneakershopstorage.viewmodels

import androidx.lifecycle.ViewModel
import com.example.sneakershopstorage.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModel(): ViewModel() {
    private val _employee = MutableStateFlow<Employee?>(null)
    val employee = _employee.asStateFlow()

    fun setEmployee(newEmployee: Employee) {
        _employee.value = newEmployee
    }
}

