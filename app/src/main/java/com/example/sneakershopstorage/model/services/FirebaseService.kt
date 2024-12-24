package com.example.sneakershopstorage.model.services

import android.util.Log
import com.example.sneakershopstorage.model.Order
import com.example.sneakershopstorage.model.Shoe
import com.example.sneakershopstorage.utils.ErrorsMessages
import com.example.sneakershopstorage.utils.FunctionResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getShoeByIdAndSize(shoeId: String, size: String): FunctionResult<Shoe> {
        val shoeSnapshot = db.collection("Shoes").document(shoeId).get().await()

        Log.i("Firebase service", "Shoe document snapshot is = $shoeSnapshot")

        val shoe = shoeSnapshot.toObject<Shoe>()?.apply {
            getPriceAndStock(size)
        } ?: return FunctionResult.Error(ErrorsMessages.SHOE_NOT_FOUND)

        Log.i("getShoeByIdAndSize", "Get shoe = $shoe")
        return FunctionResult.Success(shoe)
    }

    suspend fun getUserOrders(userId: String): FunctionResult<List<Order>> {
        val trimmedUserId = userId.replace("\"", "")
        Log.i("getUserOrders", "User id is $trimmedUserId")
        val userSnapshot = db.collection("users").document(trimmedUserId).get().await()
        Log.i("getUserOrders", "User snapshot is $userSnapshot")
        return if (userSnapshot.exists()) {
            val ordersSnapshot =
                db.collection("users").document(trimmedUserId).collection("Orders").get().await()
            val orders = ordersSnapshot.toObjects<Order>()
            FunctionResult.Success(orders)
        } else {
            FunctionResult.Error(ErrorsMessages.USER_NOT_FOUND)
        }
    }
}