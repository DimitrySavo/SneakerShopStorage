package com.example.sneakershopstorage.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.sneakershopstorage.viewmodels.UserOrdersViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserOrdersScreen(modifier: Modifier = Modifier, viewModel: UserOrdersViewModel = koinViewModel()) {
    val userId by viewModel.userId.collectAsState()

    Text(
        text = userId.toString()
    )
}