package com.example.sneakershopstorage.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Order(
    @DocumentId val id: String,
    @PropertyName("Comment") val comment : String? = null,
    @PropertyName("order") val order: List<OrderItem>? = null,
    @PropertyName("OrderDate") val orderDate: Timestamp? = null
) {
    constructor() : this (
        ""
    )

    data class OrderItem(
        val quantity: Long,
        val shoeRef: String,
        val size: String
    )
}
