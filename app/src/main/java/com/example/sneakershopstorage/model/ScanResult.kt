package com.example.sneakershopstorage.model

data class ScanResult(
    val type: String,
    val content: String
) {
    companion object {
        const val EMPLOYEE_TYPE = "Employee"
        const val SHOE_TYPE = "Shoe"
        const val USER_TYPE = "User"
    }
}