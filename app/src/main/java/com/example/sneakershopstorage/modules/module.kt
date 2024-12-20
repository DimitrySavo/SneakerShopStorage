package com.example.sneakershopstorage.modules

import com.example.sneakershopstorage.MainActivity
import com.example.sneakershopstorage.model.services.FirebaseService
import com.example.sneakershopstorage.viewmodels.EmployeeViewModel
import com.example.sneakershopstorage.utils.ScanDataBus
import com.example.sneakershopstorage.viewmodels.ShoeViewModel
import com.example.sneakershopstorage.viewmodels.UserOrdersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val module = module {
    single { FirebaseService() }
    single { ScanDataBus() }
    scope<MainActivity>{
        viewModel { ShoeViewModel(get(), get()) }
        viewModel { EmployeeViewModel(get()) }
        viewModel { UserOrdersViewModel(get()) }
    }
}