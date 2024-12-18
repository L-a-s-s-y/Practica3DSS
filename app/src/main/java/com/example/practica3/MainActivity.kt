package com.example.practica3

import android.view.View
import androidx.activity.ComponentActivity
import org.osmdroid.config.Configuration

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.practica3.ui.theme.Practica3Theme
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.osmdroid.views.MapView
import androidx.preference.PreferenceManager



class MainActivity : ComponentActivity() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkoutAdapter: CheckoutAdapter


    private lateinit var priceTotalTextView: TextView
    private lateinit var checkoutTotalPrice: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var recyclerViewCheckout: RecyclerView
    private lateinit var viewMaps: MapView


    private lateinit var buttonCarrito: Button
    private lateinit var buttonAddProducts: Button
    private lateinit var buttonCheckout: Button
    private lateinit var buttonPayment: Button
    private lateinit var buttonMaps: Button


    private var isViewingCart = false
    private var isMaps = false
    private var isCheckout = false

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)



    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)


        val context = applicationContext
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = "com.example.practica3"
        super.onCreate(savedInstanceState)

        if (token == null) {
            // Redirigir a la pantalla de login si no hay token
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            //setContentView(R.layout.activity_main)
            // Resto de la lógica

            setContentView(R.layout.activity_main)

            // configurar RecyclerView
            priceTotalTextView = findViewById(R.id.priceTotalTextView)
            //checkoutTotalPrice = findViewById(R.id.checkoutTotalPrice)

            recyclerView = findViewById(R.id.recyclerViewProducts)
            recyclerViewCart = findViewById(R.id.recyclerViewCart)
            recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout)
            viewMaps = findViewById(R.id.viewMap)

            buttonCarrito = findViewById(R.id.buttonGoToCart)
            buttonAddProducts = findViewById(R.id.buttonAddProduct)
            buttonCheckout = findViewById(R.id.buttonCheckout)
            buttonPayment = findViewById(R.id.buttonPayment)
            buttonMaps = findViewById(R.id.buttonMaps)


            recyclerViewCart.layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerViewCheckout.layoutManager = LinearLayoutManager(this)

            fetchProducts()

            viewMaps.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerViewCart.visibility = View.GONE
            recyclerViewCheckout.visibility = View.GONE

            priceTotalTextView.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
            buttonAddProducts.visibility = View.VISIBLE
            buttonMaps.visibility = View.VISIBLE

            buttonCarrito.setOnClickListener {
                toggleView()
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
        }

    }

    private fun toggleView() {
        isViewingCart = !isViewingCart

        if (isViewingCart) {
            buttonCheckout.visibility = View.VISIBLE
            buttonAddProducts.visibility = View.GONE
            recyclerView.visibility = View.GONE
            recyclerViewCart.visibility = View.VISIBLE
            priceTotalTextView.visibility = View.VISIBLE
            buttonCarrito.text = "Products"
            loadCartItems()
        } else {
            priceTotalTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerViewCart.visibility = View.GONE
            buttonCarrito.text = "View Cart"
            buttonAddProducts.visibility = View.VISIBLE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun goToMap() {
        isMaps = !isMaps
        if (isMaps) {
            buttonCheckout.visibility = View.GONE
            buttonAddProducts.visibility = View.GONE
            buttonMaps.visibility = View.VISIBLE
            buttonMaps.text = "Products"

            recyclerView.visibility = View.GONE
            recyclerViewCart.visibility = View.GONE
            recyclerViewCheckout.visibility = View.GONE
            viewMaps.visibility = View.VISIBLE

            priceTotalTextView.visibility = View.GONE
        } else {

            recyclerView.visibility = View.VISIBLE
            recyclerViewCart.visibility = View.GONE
            recyclerViewCheckout.visibility = View.GONE
            viewMaps.visibility = View.GONE

            buttonMaps.text = "Map"
            buttonCarrito.visibility = View.VISIBLE
            buttonAddProducts.visibility = View.VISIBLE
            buttonCheckout.visibility = View.GONE

            priceTotalTextView.visibility = View.GONE

        }
    }

    private fun fetchProducts() {

        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                Log.e("DEPURACION", "llegó a onResponse")
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos: $productList")
                    productList?.let {
                        // Initialize the adapter with the product list
                        productAdapter = ProductAdapter(it) {
                            productId -> addToCart(productId)
                        }
                        recyclerView.adapter = productAdapter
                    }
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("DEPURACION", "llegó a onFailure")

                Log.e("API_ERROR", "Failure: ${t.message}")
            }
        })
    }

    private fun addToCart(productId: Long) {
        apiService.addToCart(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    //Toast.makeText(this@MainActivity, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                    Log.d("API_RESPONSE", "Producto añadido al carrito: $productId")
                } else {
                    Log.e("API_ERROR", "Error al añadir al carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
            }
        })
    }

    private fun remove(product: Product) {
        apiService.remove(product.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadCartItems()
                    //Toast.makeText(this@MainActivity, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                    Log.d("API_RESPONSE", "Producto eliminado al carrito")
                } else {
                    Log.e("API_ERROR", "Error al eliminado al carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
            }
        })
    }

    private fun loadCartItems() {
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getFullCart().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val cartItems = response.body() ?: emptyList()
                    cartAdapter = CartAdapter(cartItems){
                        productId -> remove(productId)
                        loadCartItems() // Recargar la lista después de eliminar
                    }
                    recyclerViewCart.adapter = cartAdapter
                    updateTotalPrice(cartItems)
                } else {
                    Log.e("API_ERROR", "Error al cargar productos del carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("API_ERROR", "Error de red: ${t.message}")
            }
        })
    }

    private fun updateTotalPrice(cartItems: List<Product>) {
        val totalPrice = cartItems.sumOf { it.price }
        priceTotalTextView.text = "Price: $%.2f".format(totalPrice)
    }

    private fun showCheckout() {
        Log.e("CHECKOUT", "Failure: showCheckout")
        isCheckout = true
        recyclerViewCart.visibility = View.GONE
        buttonCheckout.visibility = View.GONE
        buttonCarrito.visibility = View.GONE
        recyclerViewCheckout.visibility = View.VISIBLE
        buttonPayment.visibility = View.VISIBLE
        loadCheckoutItems()
    }

    private fun loadCheckoutItems() {

        var cartItems = CartManager.getCartItems()
        Log.e("CHECKOUT", "Failure: $cartItems")
        //print(cartItems.toString())
        val apiService = ApiClient.retrofit.create(ApiService::class.java)
        apiService.getFullCart().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    cartItems = response.body() ?: emptyList()
                    /*cartAdapter = CartAdapter(cartItems) { product ->
                        CartManager.removeProduct(product)
                        loadCartItems() // Recargar la lista después de eliminar
                    }*/
                    checkoutAdapter = CheckoutAdapter(cartItems)
                    recyclerViewCheckout.adapter = checkoutAdapter
                    updateTotalPrice(cartItems)
                } else {
                    Log.e("API_ERROR", "Error al cargar productos del carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("API_ERROR", "Error de red: ${t.message}")
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
    private fun resetToProductsView() {
        // Reiniciar los estados de las vistas
        isViewingCart = false
        isCheckout = false

        recyclerView.visibility = View.VISIBLE
        recyclerViewCart.visibility = View.GONE
        recyclerViewCheckout.visibility = View.GONE
        buttonPayment.visibility = View.GONE
        buttonCheckout.visibility = View.GONE
        buttonCarrito.visibility = View.VISIBLE
        buttonAddProducts.visibility = View.VISIBLE
    }

    private fun showPaymentError() {
        Toast
            .makeText(this, "Error while processing the payment. Please try again.", Toast.LENGTH_LONG)
            .show()
    }
}
