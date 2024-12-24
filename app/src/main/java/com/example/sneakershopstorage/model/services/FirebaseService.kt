package com.example.sneakershopstorage.model.services

import android.util.Log
import com.example.sneakershopstorage.model.Employee
import com.example.sneakershopstorage.model.Order
import com.example.sneakershopstorage.model.Shoe
import com.example.sneakershopstorage.modules.CurrentEmployee
import com.example.sneakershopstorage.utils.ErrorsMessages
import com.example.sneakershopstorage.utils.FunctionResult
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import org.koin.java.KoinJavaComponent.inject

class FirebaseService(private val currentEmployee: CurrentEmployee) {
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

            orders.forEach { orderEntry: Order ->
                orderEntry.order?.forEach { model ->
                    model.modelName = getShoeName(model.shoeRef ?: "")
                }
            }

            FunctionResult.Success(orders)
        } else {
            FunctionResult.Error(ErrorsMessages.USER_NOT_FOUND)
        }
    }

    suspend fun returnShoe(shoeId: String, orderId: String, incorrectUserId: String) {
        if(currentEmployee.employee == null) return

        val userId = incorrectUserId.replace("\"", "")

        val orderDocRef = db.collection("users")
            .document(userId)
            .collection("Orders")
            .document(orderId)

        val shoeDocRef = db.collection("Shoes")
            .document(shoeId)

        db.runTransaction { transaction ->
            val orderSnapshot = transaction.get(orderDocRef)
            if (!orderSnapshot.exists()) {
                throw IllegalStateException("Order $orderId not found.")
            }

            val shoeSnapshot = transaction.get(shoeDocRef)
            if (!shoeSnapshot.exists()) {
                throw IllegalStateException("Shoe $shoeId not found.")
            }

            val order = orderSnapshot.toObject(Order::class.java) ?: error("Could not parse Order")
            val matchingItem = order.order?.find { it.shoeRef == shoeId }
                ?: throw IllegalStateException("No matching item in order")

            val updatedItems = order.order.map { item ->
                if (item.shoeRef == shoeId) item.copy(status = "returned") else item
            }

            val quantityToReturn = matchingItem.quantity ?: 0
            val sizeKey = matchingItem.size ?: error("No size in item")

            transaction.update(orderDocRef, "Order", updatedItems)
            transaction.update(shoeDocRef, "sizes.$sizeKey.inStock.${currentEmployee.employee!!.storage}", FieldValue.increment(quantityToReturn))

            null // Возвращаем что-то или null из транзакции
        }.await()

        // После успешной транзакции можете логировать, показывать тосты и т.д.
        Log.i("returnShoe", "Shoe [$shoeId], size=[${shoeId}], returned successfully!")
    }

    suspend fun getShoeName(id: String): String? {
        if(id.isBlank()) return null
        val modelDoc = db
            .collection("Shoes")
            .document(id)
            .get()
            .await()

        return if(modelDoc.exists()) {
            modelDoc.get("modelName") as String
        } else {
            null
        }
    }
}