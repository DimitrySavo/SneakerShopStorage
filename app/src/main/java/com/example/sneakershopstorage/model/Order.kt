package com.example.sneakershopstorage.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Order(
    @DocumentId val id: String,
    @PropertyName("Comment") val comment : String? = null,
    @PropertyName("Order") val order: List<OrderItem>? = null,
    @PropertyName("OrderDate") val orderDate: Timestamp? = null
) {
    constructor() : this (
        ""
    )

    data class OrderItem(
        val shoeRef: String? = null,
        var modelName: String? = null,
        val quantity: Long? = null,
        val size: String? = null,
        var status: String? = null
    )
}
