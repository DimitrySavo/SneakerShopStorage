package com.example.sneakershopstorage.model

import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

data class Shoe(
    @DocumentId val id: String,
    @PropertyName("brandName") val brandName: String,
    @PropertyName("color") val color: String,
    @PropertyName("description") val description: String,
    @PropertyName("preImageUrl") var preImageUrl: String,
    @PropertyName("imageCollectionUrl") val imageCollectionUrl: String,
    @PropertyName("modelName") val modelName: String,
    @PropertyName("tags") val tags: List<String>,
    val sizes: Map<String, Size>,
    var price: Double = 0.0,
    var inStock: Boolean = false
){
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        emptyList(),
        emptyMap()
    )

    fun getPriceAndStock() {
        if (this.sizes.values.any { it.inStock.values.any { stock -> stock > 0 } }) {
            val sizeInStock = this.sizes.values.first { it.inStock.values.any { stock -> stock > 0 } }
            this.price = sizeInStock.price
            this.inStock = true
        } else {
            this.price = this.sizes.values.first().price
        }
    }
}

data class Size(
    val usSize: Double,
    val euSize: Double,
    val ruSize: Double,
    var inStock: Map<String, Long>,
    var price: Double
){
    constructor():this(
        0.0,
        0.0,
        0.0,
        emptyMap(),
        0.0
    )
}


suspend fun Shoe.getPreviewUrl(): String {
    Log.i("Image URI", "Start getting image url with preImageUrl: $preImageUrl")
    val storageRef = Firebase.storage.reference.child(this.preImageUrl)
    return try {
        val downloadUrl = storageRef.downloadUrl.await()
        Log.i("Image URI", downloadUrl.toString())
        downloadUrl.toString()
    } catch (e: StorageException) {
        Log.e("Image URI", "Storage Exception: ${e.message}", e)
        "Storage error: ${e.message}" // More specific error message
    } catch (e: Exception) {
        Log.e("Image URI", "General Exception: ${e.message}", e)
        "Error fetching URL"
    }
}

suspend fun Shoe.getImagesUrls(): List<String> {
    val storageRef = Firebase.storage.reference.child(this.imageCollectionUrl)
    val imagesUrls = mutableListOf<String>()
    try {
        val listResult = storageRef.listAll().await()
        listResult.items.forEach { item ->
            val downloadUrl = item.downloadUrl.await()
            imagesUrls.add(downloadUrl.toString())
        }
    } catch (e: StorageException) {
        Log.e("Image URI", "Storage Exception: ${e.message}", e)
    } catch (e: Exception) {
        Log.e("Image URI", "General Exception: ${e.message}", e)
    }
    return imagesUrls
}