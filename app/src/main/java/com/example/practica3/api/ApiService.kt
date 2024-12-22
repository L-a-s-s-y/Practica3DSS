package com.example.practica3.api
import com.example.practica3.login.LoginRequest
import com.example.practica3.login.LoginResponse
import com.example.practica3.models.ProductModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/products")
    fun getAllProducts(): Call<List<ProductModel>>

    // Añadir un producto (POST request example)
    @POST("/products/add")
    fun addProduct(
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    @POST("/api/cart/add")
    fun addToCart(
        @Query("productId") productId: Long,
    ): Call<Void>

    @POST("/api/cart/delete")
    fun remove(
        @Query("productId") productId: Long,
    ): Call<Void>

    @POST("/api/cart/payment")
    fun processPayment(): Call<Void>

    @GET("/api/cart")
    fun getFullCart(): Call<List<ProductModel>>

    // Editar un product por su ID
    @POST("/products/edit/{id}")
    fun editProduct(
        @Path("id") id: Long,
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    // Eliminar un producto por su ID
    @POST("/api/products/delete")
    fun deleteProduct(
        @Query("productId") productId: Long,
    ): Call<Void>

    @POST("/api/products/add")
    fun addProductAdmin(
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}