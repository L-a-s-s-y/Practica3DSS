package com.example.practica3.managers

import com.example.practica3.models.ProductModel

object CartManager {
    private val cartItems = mutableListOf<ProductModel>()

    fun addProduct(productModel: ProductModel) {
        cartItems.add(productModel)
    }

    fun removeProduct(productModel: ProductModel) {
        cartItems.remove(productModel)
    }

    fun getCartItems(): List<ProductModel> {
        return cartItems
    }

    fun clearCart() {
        cartItems.clear()
    }
}