package com.example.sneakershopstorage.model

import com.google.firebase.Timestamp

data class Order(
    val Comment : String? = null,
    val order: List<OrderItem>? = null,
    val OrderDate: Timestamp? = null
) {
    data class OrderItem(
        val quantity: Long,
        val shoeRef: String,
        val size: String
    )
}
