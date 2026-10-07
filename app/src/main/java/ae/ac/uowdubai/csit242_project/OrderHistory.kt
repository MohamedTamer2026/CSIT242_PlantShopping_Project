package ae.ac.uowdubai.csit242_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class OrderHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_history)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Setup Placeholder Order
        val rvOrderHistory = findViewById<RecyclerView>(R.id.rvOrderHistory)
        val placeholderOrders = listOf(
            OrderHistoryItem(
                orderId = "12345",
                date = "24 Oct 2023",
                itemsSummary = "Lucky Jade Plant, Basil",
                status = "Delivered",
                totalAmount = "AED 62.00"
            )
        )
        rvOrderHistory.adapter = OrderHistoryAdapter(placeholderOrders)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_orders

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.navigation_orders) return@setOnItemSelectedListener true
            
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, Home::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, Cart::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_products -> {
                    startActivity(Intent(this, Products::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
