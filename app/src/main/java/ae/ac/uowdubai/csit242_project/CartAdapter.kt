package ae.ac.uowdubai.csit242_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.Locale

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onUpdate: (CartItem, Int) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImg: ImageView = view.findViewById(R.id.ivCartItemImg)
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val tvQty: TextView = view.findViewById(R.id.tvCartQty)
        val tvTotal: TextView = view.findViewById(R.id.tvCartItemTotal)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = item.price
        holder.tvQty.text = String.format(Locale.getDefault(), "%02d", item.quantity)

        val priceVal = item.price.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        val total = priceVal * item.quantity
        holder.tvTotal.text = String.format(Locale.getDefault(), "%.2f AED", total)

        if (item.image.isNotEmpty()) {
            Picasso.get().load(item.image).placeholder(R.drawable.placeholder_plant).into(holder.ivImg)
        }

        holder.btnPlus.setOnClickListener { onUpdate(item, item.quantity + 1) }
        holder.btnMinus.setOnClickListener { if (item.quantity > 1) onUpdate(item, item.quantity - 1) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = cartItems.size
}
