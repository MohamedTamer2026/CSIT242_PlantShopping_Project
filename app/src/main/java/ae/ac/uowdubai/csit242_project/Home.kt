package ae.ac.uowdubai.csit242_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class Home : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var productList: MutableList<Product>
    private lateinit var recommendationAdapter: RecommendationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Set bottom padding to 0 to make the nav bar flush with the bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // 1. Welcome Header
        val currentUser = FirebaseAuth.getInstance().currentUser
        val tvWelcomeUser = findViewById<TextView>(R.id.tvWelcomeUser)
        val name = currentUser?.email?.split("@")?.get(0) ?: "User"
        tvWelcomeUser.text = "Hi $name,"

        // 2. Initialize Firebase with the correct region URL from logs
        val dbUrl = "https://project-d4347-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(dbUrl).getReference("Plants")
        productList = mutableListOf()

        // 3. Setup Recommendations RecyclerView (Horizontal)
        val rvRecommendations = findViewById<RecyclerView>(R.id.rvRecommendations)
        rvRecommendations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendationAdapter = RecommendationAdapter(productList) { product ->
            openProductDetail(product)
        }
        rvRecommendations.adapter = recommendationAdapter

        // 4. Fetch Data
        fetchHomeData()

        // 5. Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_orders -> {
                    startActivity(Intent(this, OrderHistory::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, Cart::class.java))
                    true
                }
                R.id.navigation_products -> {
                    startActivity(Intent(this, Products::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchHomeData() {
        Log.d("FirebaseData", "Starting to fetch data from: Plants")
        
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseData", "Data received. Snapshot exists: ${snapshot.exists()}")
                Log.d("FirebaseData", "Number of children: ${snapshot.childrenCount}")
                
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        Log.d("FirebaseData", "Loaded product: ${product.name}")
                        productList.add(product)
                    } else {
                        Log.e("FirebaseData", "Failed to parse product at: ${productSnapshot.key}")
                    }
                }

                if (productList.isNotEmpty()) {
                    setupPlantOfDay(productList[0])
                    recommendationAdapter.notifyDataSetChanged()
                    Log.d("FirebaseData", "Adapter updated with ${productList.size} items")
                } else {
                    Log.w("FirebaseData", "Product list is empty after parsing")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseData", "Database Error: ${error.message}")
                Toast.makeText(this@Home, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupPlantOfDay(product: Product) {
        val tvPodName = findViewById<TextView>(R.id.tvPodName)
        val tvPodSpecies = findViewById<TextView>(R.id.tvPodSpecies)
        val ivPodImg = findViewById<ImageView>(R.id.ivPodImg)
        val cvPlantOfDay = findViewById<CardView>(R.id.cvPlantOfDay)

        tvPodName.text = product.name
        tvPodSpecies.text = "(${product.speciesName})"
        
        if (product.image.isNotEmpty()) {
            Picasso.get()
                .load(product.image)
                .placeholder(R.drawable.placeholder_plant)
                .into(ivPodImg)
        }

        cvPlantOfDay.setOnClickListener { openProductDetail(product) }
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(this, ProductDetail::class.java)
        intent.putExtra("name", product.name)
        intent.putExtra("speciesName", product.speciesName)
        intent.putExtra("price", product.price)
        intent.putExtra("description", product.description)
        intent.putExtra("image", product.image)
        startActivity(intent)
    }
}
