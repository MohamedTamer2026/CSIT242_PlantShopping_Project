package ae.ac.uowdubai.csit242_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecommendationAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImg: ImageView = view.findViewById(R.id.ivRecImg)
        val tvName: TextView = view.findViewById(R.id.tvRecName)
        val tvSpecies: TextView = view.findViewById(R.id.tvRecSpecies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.name
        holder.tvSpecies.text = "(${product.speciesName})"

        if (product.image.isNotEmpty()) {
            Picasso.get()
                .load(product.image)
                .placeholder(R.drawable.placeholder_plant)
                .into(holder.ivImg)
        }

        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount() = products.size
}
