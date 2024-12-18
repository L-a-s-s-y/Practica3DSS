package com.example.practica3
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
class ProductAdapter(
    private val productList: List<Product>,
    private val onAddToCart: (Long) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Clase ViewHolder para contener las referencias a cada una de las vistas de los items
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonAddToCart: Button = itemView.findViewById(R.id.buttonAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.textViewName.text = product.name
        holder.textViewPrice.text = "$${product.price}"
        holder.buttonAddToCart.setOnClickListener{
            onAddToCart(product.id)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}