package com.example.sneakershopstorage.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sneakershopstorage.model.Employee

@Composable
fun HelloScreen(modifier: Modifier = Modifier, employee: Employee) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Hello $employee",
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}