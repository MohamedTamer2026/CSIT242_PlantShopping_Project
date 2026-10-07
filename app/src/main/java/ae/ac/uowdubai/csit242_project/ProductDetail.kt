package ae.ac.uowdubai.csit242_project

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ProductDetail : AppCompatActivity() {
    private var quantity = 1
    private var basePrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main_detail)
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Retrieve data
        val name = intent.getStringExtra("name") ?: ""
        val speciesName = intent.getStringExtra("speciesName") ?: ""
        val priceStr = intent.getStringExtra("price") ?: "0"
        val description = intent.getStringExtra("description") ?: ""
        val image = intent.getStringExtra("image") ?: ""

        // Extract numeric price (e.g., "50 AED" -> 50.0)
        basePrice = priceStr.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

        // Bind Views
        val tvTitle = findViewById<TextView>(R.id.tvProductTitle)
        val tvSpecies = findViewById<TextView>(R.id.tvProductSpecies)
        val tvDescription = findViewById<TextView>(R.id.tvProductDescription)
        val tvPriceDetail = findViewById<TextView>(R.id.tvProductPriceDetail)
        val ivProductImg = findViewById<ImageView>(R.id.ivProductImg)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        
        val tvQuantity = findViewById<TextView>(R.id.tvQuantity)
        val btnPlus = findViewById<ImageButton>(R.id.btnIncreaseQty)
        val btnMinus = findViewById<ImageButton>(R.id.btnDecreaseQty)
        val btnAddToCart = findViewById<Button>(R.id.btnAddToCartDetail)

        // Set Initial Data
        tvTitle.text = name
        tvSpecies.text = "($speciesName)"
        tvDescription.text = description
        updatePriceDisplay(tvPriceDetail)

        if (image.isNotEmpty()) {
            Picasso.get().load(image).placeholder(R.drawable.placeholder_plant).into(ivProductImg)
        }

        // Quantity Logic
        btnPlus.setOnClickListener {
            quantity++
            tvQuantity.text = String.format("%02d", quantity)
            updatePriceDisplay(tvPriceDetail)
        }

        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = String.format("%02d", quantity)
                updatePriceDisplay(tvPriceDetail)
            }
        }

        // Add to Cart Logic
        btnAddToCart.setOnClickListener {
            addToCart(name, priceStr, image)
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun updatePriceDisplay(tvPrice: TextView) {
        val total = basePrice * quantity
        tvPrice.text = String.format("%.2f AED", total)
    }

    private fun addToCart(name: String, price: String, image: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        val cartRef = FirebaseDatabase.getInstance("https://project-d4347-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Carts")
            .child(userId)
            .child(name) // Using name as key to overwrite/update the same plant

        val cartItem = CartItem(
            id = name,
            name = name,
            price = price,
            image = image,
            quantity = quantity
        )

        cartRef.setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "$name added to cart", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
        }
    }
}
