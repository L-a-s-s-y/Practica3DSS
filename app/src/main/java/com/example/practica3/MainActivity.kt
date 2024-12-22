package com.example.practica3

import android.view.View
import androidx.activity.ComponentActivity
import org.osmdroid.config.Configuration

import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.osmdroid.views.MapView
import androidx.preference.PreferenceManager
import com.example.practica3.adapters.CartAdapter
import com.example.practica3.adapters.CheckoutAdapter
import com.example.practica3.adapters.ProductAdapter
import com.example.practica3.api.ApiClient
import com.example.practica3.api.ApiService
import com.example.practica3.managers.CartManager
import com.example.practica3.models.ProductModel



class MainActivity : ComponentActivity() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkoutAdapter: CheckoutAdapter


    private lateinit var priceTotalTextView: TextView
    //private lateinit var checkoutTotalPrice: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var recyclerViewCheckout: RecyclerView
    //private lateinit var viewMaps: MapView


    private lateinit var buttonCarrito: Button
    private lateinit var buttonAddProducts: Button
    private lateinit var buttonCheckout: Button
    private lateinit var buttonPayment: Button
    private lateinit var buttonMaps: Button
    private lateinit var buttonLogout: Button
    private lateinit var buttonProducts: Button

    private var isMaps = false
    private var isCheckout = false
    private var isAdmin = false

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)


        val context = applicationContext
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = "com.example.practica3"
        super.onCreate(savedInstanceState)
        ApiClient.initialize(applicationContext)
        if (token == null) {
            // Redirigir a la pantalla de login si no hay token
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            //setContentView(R.layout.activity_main)
            // Resto de la lógica

            isAdmin = token.contains("admin")

            Log.d("IS_ADMIN", ":${isAdmin}")

            setContentView(R.layout.activity_main)

            // configurar RecyclerView
            priceTotalTextView = findViewById(R.id.priceTotalTextView)
            //checkoutTotalPrice = findViewById(R.id.checkoutTotalPrice)

            recyclerView = findViewById(R.id.recyclerViewProducts)
            recyclerViewCart = findViewById(R.id.recyclerViewCart)
            recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout)
            //viewMaps = findViewById(R.id.viewMap)

            buttonCarrito = findViewById(R.id.buttonGoToCart)
            buttonAddProducts = findViewById(R.id.buttonAddProduct)
            buttonCheckout = findViewById(R.id.buttonCheckout)
            buttonPayment = findViewById(R.id.buttonPayment)
            buttonMaps = findViewById(R.id.buttonMaps)
            buttonLogout = findViewById(R.id.buttonLogout)
            buttonProducts = findViewById(R.id.buttonGoToProducts)



            addProductLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Si el producto se añadió con éxito, actualiza los productos
                    fetchProducts()
                }
            }


            recyclerViewCart.layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerViewCheckout.layoutManager = LinearLayoutManager(this)

            fetchProducts()

            //viewMaps.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerViewCart.visibility = View.GONE
            recyclerViewCheckout.visibility = View.GONE

            priceTotalTextView.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
            buttonMaps.visibility = View.VISIBLE
            buttonPayment.visibility = View.GONE
            buttonProducts.visibility = View.GONE

            if (isAdmin)
                buttonAddProducts.visibility = View.VISIBLE
            else
                buttonAddProducts.visibility = View.GONE

            buttonCarrito.setOnClickListener {
                goToCart()
            }

            buttonProducts.setOnClickListener {
                goToProducts()
            }

            buttonCheckout.setOnClickListener {
                showCheckout()
            }

            buttonPayment.setOnClickListener {
                makePayment()
            }

            buttonMaps.setOnClickListener{
                goToMap()
            }

            buttonLogout.setOnClickListener{
                clearLocalData()
            }

            buttonAddProducts.setOnClickListener{
                addProductAdmin()
            }
        }

    }

    private fun goToProducts(){
        priceTotalTextView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerViewCart.visibility = View.GONE
        buttonPayment.visibility = View.GONE
        if (isAdmin)
            buttonAddProducts.visibility = View.VISIBLE
        else
            buttonAddProducts.visibility = View.GONE
        buttonCheckout.visibility = View.GONE
        buttonCarrito.visibility = View.VISIBLE
        buttonProducts.visibility = View.GONE

        isCheckout = true

        recyclerViewCart.visibility = View.GONE
        recyclerViewCheckout.visibility = View.GONE

        buttonCheckout.visibility = View.GONE
        buttonCarrito.visibility = View.VISIBLE
        buttonProducts.visibility = View.GONE

        buttonPayment.visibility = View.GONE

    }

    private fun resetToProductsView() {
        // Reiniciar los estados de las vistas
        isCheckout = false
        priceTotalTextView.visibility = View.GONE

        recyclerView.visibility = View.VISIBLE
        recyclerViewCart.visibility = View.GONE
        recyclerViewCheckout.visibility = View.GONE

        buttonPayment.visibility = View.GONE
        buttonCheckout.visibility = View.GONE
        buttonCarrito.visibility = View.VISIBLE
        buttonProducts.visibility = View.GONE
        if (isAdmin)
            buttonAddProducts.visibility = View.VISIBLE
        else
            buttonAddProducts.visibility = View.GONE
    }

    private fun goToCart() {
        buttonCarrito.visibility = View.GONE
        buttonCheckout.visibility = View.VISIBLE
        buttonAddProducts.visibility = View.GONE
        recyclerView.visibility = View.GONE
        recyclerViewCart.visibility = View.VISIBLE
        priceTotalTextView.visibility = View.VISIBLE
        buttonProducts.visibility = View.VISIBLE
        loadCartItems()
    }

    private fun showCheckout() {
        isCheckout = true

        recyclerViewCheckout.visibility = View.VISIBLE
        recyclerViewCart.visibility = View.GONE
        recyclerView.visibility = View.GONE

        buttonCheckout.visibility = View.GONE
        buttonCarrito.visibility = View.GONE
        buttonProducts.visibility = View.VISIBLE

        buttonPayment.visibility = View.VISIBLE
        loadCheckoutItems()
    }

    private fun goToMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    private fun fetchProducts() {

        apiService.getAllProducts().enqueue(object : Callback<List<ProductModel>> {
            override fun onResponse(
                call: Call<List<ProductModel>>,
                response: Response<List<ProductModel>>
            ) {
                Log.d("FETCH_PRODUCTS", "llegó a onResponse")
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("FETCH_PRODUCTS", "Productos: $productList")
                    productList?.let {
                        // Initialize the adapter with the product list
                        productAdapter = ProductAdapter(
                            productModelList = it,
                            onAddToCart = { productId -> addToCart(productId) },
                            isAdmin = isAdmin,
                            deleteProduct = { productId -> deleteProduct(productId) }
                        )
                        recyclerView.adapter = productAdapter
                    }
                } else {
                    Log.e("FETCH_PRODUCTS", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                Log.e("FETCH_PRODUCTS", "llegó a onFailure")
                Log.e("FETCH_PRODUCTS", "Failure: ${t.message}")
                Log.e("FETCH_PRODUCTS", "Failure: ${t.message}")
            }
        })
    }

    private fun addToCart(productId: Long) {
        apiService.addToCart(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    //Toast.makeText(this@MainActivity, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                    Log.d("ADD_TO_CART", "Producto añadido al carrito: $productId")
                } else {
                    Log.e("ADD_TO_CART", "Error al añadir al carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ADD_TO_CART", "Error: ${t.message}")
            }
        })
    }

    private fun deleteProduct(productID: Long) {
        apiService.deleteProduct(productID).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadCartItems()
                    //Toast.makeText(this@MainActivity, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                    Log.d("DELETE_PRODUCT", "Producto eliminado al carrito")
                    fetchProducts()
                } else {
                    Log.e("DELETE_PRODUCT", "Error al eliminado al carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DELETE_PRODUCT", "Error: ${t.message}")
            }
        })
    }

    private fun remove(productModel: ProductModel) {
        apiService.remove(productModel.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadCartItems()
                    //Toast.makeText(this@MainActivity, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                    Log.d("REMOVE", "Producto eliminado al carrito")
                } else {
                    Log.e("REMOVE", "Error al eliminado al carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("REMOVE", "Error: ${t.message}")
            }
        })
    }

    private fun loadCartItems() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getFullCart().enqueue(object : Callback<List<ProductModel>> {
            override fun onResponse(call: Call<List<ProductModel>>, response: Response<List<ProductModel>>) {
                if (response.isSuccessful) {
                    val cartItems = response.body() ?: emptyList()
                    cartAdapter = CartAdapter(cartItems){
                        productId -> remove(productId)
                        loadCartItems() // Recargar la lista después de eliminar
                    }
                    recyclerViewCart.adapter = cartAdapter
                    updateTotalPrice(cartItems)
                } else {
                    Log.e("LOAD_CART_ITEMS", "Error al cargar productos del carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                Log.e("LOAD_CART_ITEMS", "Error de red: ${t.message}")
            }
        })
    }

    private fun updateTotalPrice(cartItems: List<ProductModel>) {
        val totalPrice = cartItems.sumOf { it.price }
        priceTotalTextView.text = "Total: $%.2f".format(totalPrice)
    }

    private fun loadCheckoutItems() {

        var cartItems = CartManager.getCartItems()

        //print(cartItems.toString())
        val apiService = ApiClient.retrofit.create(ApiService::class.java)
        apiService.getFullCart().enqueue(object : Callback<List<ProductModel>> {
            override fun onResponse(call: Call<List<ProductModel>>, response: Response<List<ProductModel>>) {
                if (response.isSuccessful) {
                    cartItems = response.body() ?: emptyList()
                    /*cartAdapter = CartAdapter(cartItems) { product ->
                        CartManager.removeProduct(product)
                        loadCartItems() // Recargar la lista después de eliminar
                    }*/
                    checkoutAdapter = CheckoutAdapter(cartItems)
                    recyclerViewCheckout.adapter = checkoutAdapter
                    updateTotalPrice(cartItems)
                    Log.d("CHECKOUT", "EXITO: $cartItems")
                } else {
                    Log.e("CHECKOUT", "Error al cargar productos del carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProductModel>>, t: Throwable) {
                Log.e("CHECKOUT", "Error de red: ${t.message}")
            }
        })
    }

    private fun makePayment() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.processPayment().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("PAYMENT", "Pago procesado con éxito")
                    Toast.makeText(
                        this@MainActivity,
                        "The payment has been made successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    resetToProductsView() // Volver a la vista inicial
                } else {
                    Log.e("PAYMENT", "Error al procesar el pago: ${response.code()}")
                    showPaymentError()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("PAYMENT", "Error de red: ${t.message}")
                showPaymentError()
            }
        })
    }

    private fun showPaymentError() {
        Toast
            .makeText(this, "Error while processing the payment. Please try again.", Toast.LENGTH_LONG)
            .show()
    }

    private fun clearLocalData() {
        // Eliminar cookies
        val cookieManager = android.webkit.CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        // Eliminar auth_token de SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPref.edit().remove("auth_token").commit()

        Log.d("CLEAR_LOCAL_DATA","Datos locales eliminados: auth_token y cookies")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addProductAdmin(){
        val intent = Intent(this, AddProductActivity::class.java)
        addProductLauncher.launch(intent)
        Log.e("DEPURANDO","Por que no funsiona")
    }
}
