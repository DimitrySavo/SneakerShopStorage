package com.example.sneakershopstorage.composables

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sneakershopstorage.model.Order

@Composable
fun UserOrder(modifier: Modifier = Modifier, order: Order) {
    Log.i("UserOrderCard", "$order")

    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        Text(
         text = order.id
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.End)
        ) {
            order.order?.let { orderItems ->
                orderItems.forEach {
                    Text(
                        text = it.shoeRef + " : " + it.quantity
                    )
                }
            }
        }
    }
}