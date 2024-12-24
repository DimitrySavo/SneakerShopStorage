package com.example.sneakershopstorage.modules

import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.model.services.FirebaseService
import com.example.sneakershopstorage.utils.ScanDataBus
import com.example.sneakershopstorage.viewmodels.ShoeViewModel
import com.example.sneakershopstorage.viewmodels.UserOrdersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class ReturningState(var isReturning: Boolean = false)
data class CurrentEmployee(var employee: Employee?)

val module = module {
    single { CurrentEmployee(null) }
    single { FirebaseService(get()) }
    single { ScanDataBus() }
    single { ReturningState() }
    viewModel { ShoeViewModel(get(), get(), get()) }
    viewModel { (scan: () -> Unit) ->
        UserOrdersViewModel(get(), get(), get(), scan)
    }
}