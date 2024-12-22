package com.example.practica3.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.practica3.models.ProductModel
import com.example.practica3.R

class ProductAdapter(
    private val productModelList: List<ProductModel>,
    private val onAddToCart: (Long) -> Unit,
    private val isAdmin : Boolean,
    private val deleteProduct: (Long) -> Unit,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Clase ViewHolder para contener las referencias a cada una de las vistas de los items
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonAddToCart: Button = itemView.findViewById(R.id.buttonAddToCart)
        val buttonEliminar: Button = itemView.findViewById(R.id.buttonEliminarProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productModelList[position]
        holder.textViewName.text = product.name
        holder.textViewPrice.text = "$${product.price}"
        holder.buttonAddToCart.setOnClickListener{
            onAddToCart(product.id)
        }
        if(isAdmin){
            holder.buttonEliminar.visibility = View.VISIBLE
            holder.buttonEliminar.setOnClickListener{
                deleteProduct(product.id)
            }
        } else {
            holder.buttonEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return productModelList.size
    }
}