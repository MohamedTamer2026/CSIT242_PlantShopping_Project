package ae.ac.uowdubai.csit242_project

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class Products : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_products)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("Plants")

        // Setup RecyclerView
        val rvProducts = findViewById<RecyclerView>(R.id.rvProducts)
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(productList) { product ->
            // On Item Click
            val intent = Intent(this, ProductDetail::class.java)
            intent.putExtra("name", product.name)
            intent.putExtra("speciesName", product.speciesName)
            intent.putExtra("price", product.price)
            intent.putExtra("description", product.description)
            intent.putExtra("image", product.image)
            startActivity(intent)
        }
        rvProducts.adapter = adapter

        // Fetch Data from Firebase
        fetchProducts()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_products

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, Home::class.java))
                    true
                }
                R.id.navigation_orders -> {
                    startActivity(Intent(this, OrderHistory::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, Cart::class.java))
                    true
                }
                R.id.navigation_products -> true
                else -> false
            }
        }
    }

    private fun fetchProducts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (postSnapshot in snapshot.children) {
                    val product = postSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Products, "Failed to load: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
