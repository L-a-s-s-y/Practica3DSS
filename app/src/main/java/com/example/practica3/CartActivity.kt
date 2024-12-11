package com.example.practica3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var buttonCheckout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        buttonCheckout = findViewById(R.id.buttonCheckout)

        // Configurar RecyclerView
        recyclerViewCart.layoutManager = LinearLayoutManager(this)
        loadCartItems()

        // Botón para finalizar la compra
        buttonCheckout.setOnClickListener {
            // Aquí puedes implementar la lógica de checkout
            CartManager.clearCart()
            loadCartItems()
        }
    }

    private fun loadCartItems() {
        val cartItems = CartManager.getCartItems()
        recyclerViewCart.adapter = CartAdapter(cartItems) { product ->
            CartManager.removeProduct(product)
            loadCartItems() // Recargar la lista después de eliminar
        }
    }
}