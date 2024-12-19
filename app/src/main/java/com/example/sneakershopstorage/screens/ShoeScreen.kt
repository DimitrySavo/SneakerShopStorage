package com.example.sneakershopstorage.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.sneakershopstorage.viewmodels.ShoeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoeScreen(modifier: Modifier = Modifier, viewModel: ShoeViewModel = koinViewModel()) {
    val shoe by viewModel.shoe.collectAsState()

    Text(
        text = shoe.toString()
    )
}