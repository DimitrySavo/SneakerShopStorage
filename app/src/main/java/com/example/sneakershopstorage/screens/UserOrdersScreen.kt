package com.example.sneakershopstorage.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sneakershopstorage.composables.UserOrder
import com.example.sneakershopstorage.viewmodels.UserOrdersViewModel

@Composable
fun UserOrdersScreen(modifier: Modifier = Modifier, viewModel: UserOrdersViewModel) {
    val userOrders by viewModel.userOrders.collectAsState()

    LazyColumn {
        items(items = userOrders) { order ->
            UserOrder(
                order = order
            )
            Spacer(
                modifier = Modifier
                    .width(2.dp)
                    .background(color = Color.Black)
            )
        }
    }

    if (userOrders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No orders",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}