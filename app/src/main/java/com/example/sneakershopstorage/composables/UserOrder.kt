package com.example.sneakershopstorage.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sneakershopstorage.model.Order

@Composable
fun UserOrder(
    modifier: Modifier = Modifier,
    order: Order,
    returnItem: (Order.OrderItem) -> Unit
) {
    // Состояние, отвечающее за показ диалога
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth(0.95f)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = {
            showDialog = true
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Order: " + order.id,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            order.order?.forEach { item ->
                Text(
                    text = "${item.modelName ?: "Unknown model"} ${item.size ?: "Unknown size"} : ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Если showDialog == true, показываем AlertDialog
    if (showDialog) {
        OrderDetailsDialog(
            order = order,
            onDismiss = { showDialog = false },
            onItemClicked = { item ->
                returnItem(item)
                Log.i("OrderDialog", "Clicked on ${item.modelName}")
                showDialog = false
            }
        )
    }
}


@Composable
fun OrderDetailsDialog(
    order: Order,
    onDismiss: () -> Unit,
    onItemClicked: (Order.OrderItem) -> Unit
) {
    // Собираем все «штучные» элементы заказа в единый список,
    // чтобы, скажем, если quantity = 2, этот элемент отобразился 2 раза
    val itemsList = remember(order) {
        order.order
            ?.flatMap { item ->
                val qty = item.quantity ?: 0
                List(qty.toInt()) { item }
            }
            .orEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Детали заказа")
        },
        text = {
            LazyColumn {
                items(items = itemsList) { item ->
                    // Каждая «строка»
                    // Можно обернуть в Row, если хотите что-то более сложное
                    if(item.status != OrderItemStatuses.RETURNED){
                        Text(
                            text = item.modelName ?: "Unknown model",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemClicked(item)
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}


object OrderItemStatuses {
    const val RETURNED = "returned"
}