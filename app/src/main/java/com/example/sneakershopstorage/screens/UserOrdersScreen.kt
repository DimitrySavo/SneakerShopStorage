package com.example.sneakershopstorage.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sneakershopstorage.composables.UserOrder
import com.example.sneakershopstorage.viewmodels.UserOrdersViewModel
import org.koin.androidx.compose.koinViewModel

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
}