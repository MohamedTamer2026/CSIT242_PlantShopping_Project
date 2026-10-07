package ae.ac.uowdubai.csit242_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class Cart : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var adapter: CartAdapter
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotalAmount: TextView
    private var finalTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        database = FirebaseDatabase.getInstance("https://project-d4347-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Carts")
            .child(userId)

        cartItems = mutableListOf()
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        // Setup RecyclerView
        val rvCartItems = findViewById<RecyclerView>(R.id.rvCartItems)
        rvCartItems.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(cartItems, 
            onUpdate = { item, newQty -> updateQuantity(item, newQty) },
            onDelete = { item -> deleteItem(item) }
        )
        rvCartItems.adapter = adapter

        fetchCartItems()

        // Navigation & Buttons
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnPlaceOrder).setOnClickListener {
            if (cartItems.isNotEmpty()) {
                val intent = Intent(this, Payment::class.java)
                intent.putExtra("totalPrice", finalTotal)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_cart
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
                R.id.navigation_cart -> true
                R.id.navigation_products -> {
                    startActivity(Intent(this, Products::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchCartItems() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                var total = 0.0
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(CartItem::class.java)
                    if (item != null) {
                        cartItems.add(item)
                        val priceVal = item.price.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
                        total += priceVal * item.quantity
                    }
                }
                adapter.notifyDataSetChanged()
                finalTotal = total
                updateSummary(total)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Cart, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateQuantity(item: CartItem, newQty: Int) {
        database.child(item.id).child("quantity").setValue(newQty)
    }

    private fun deleteItem(item: CartItem) {
        database.child(item.id).removeValue()
    }

    private fun updateSummary(total: Double) {
        val totalStr = String.format(Locale.getDefault(), "%.2f AED", total)
        tvSubtotal.text = totalStr
        tvTotalAmount.text = totalStr
    }
}
