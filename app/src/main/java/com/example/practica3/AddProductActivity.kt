package com.example.practica3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica3.api.ApiClient
import com.example.practica3.api.ApiService
import com.example.practica3.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductPrice: EditText
    private lateinit var buttonSubmitProduct: Button

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        editTextProductName = findViewById(R.id.editTextProductName)
        editTextProductPrice = findViewById(R.id.editTextProductPrice)
        buttonSubmitProduct = findViewById(R.id.buttonSubmitProduct)

        buttonSubmitProduct.setOnClickListener {
            val name = editTextProductName.text.toString()
            val price = editTextProductPrice.text.toString().toDoubleOrNull()

            if (name.isBlank() || price == null) {
                Toast.makeText(this, "Please enter valid product details", Toast.LENGTH_SHORT).show()
            } else {
                addProduct(name, price)
            }
        }
    }

    private fun addProduct(name: String, price: Double) {
        apiService.addProductAdmin(name, price).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("ADD_PRODUCT_ADMIN", "añadido")
                    Toast.makeText(this@AddProductActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish() // Cierra la actividad después de añadir el producto
                } else {
                    Toast.makeText(this@AddProductActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                    Log.e("ADD_PRODUCT_ADMIN", "Failed")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AddProductActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ADD_PRODUCT_ADMIN", "onFailure")
            }
        })
    }
}