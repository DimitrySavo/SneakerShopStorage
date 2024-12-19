package com.example.sneakershopstorage.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.sneakershopstorage.viewmodels.EmployeeViewModel
import com.example.sneakershopstorage.viewmodels.ShoeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    employeeViewModel: EmployeeViewModel = koinViewModel(),
    shoeViewModel: ShoeViewModel = koinViewModel(),
    scanFunc: () -> Unit
) {
    val employee by employeeViewModel.employee.collectAsState()
    val shoe by shoeViewModel.shoe.collectAsState()
    Log.i("MainScreen", "Recompose with new employee = $employee")

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Text(
                text = employee?.name ?: ""
            )

            Button(
                onClick = {

                }
            ) {
                Text(
                    text = "Возврат"
                )
            }

            Button(
                onClick = {

                }
            ) {
                Text(
                    text = "Проверить наличие"
                )
            }
        }
    }
}