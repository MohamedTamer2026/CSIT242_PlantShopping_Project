package ae.ac.uowdubai.csit242_project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import java.util.Locale

class Payment : AppCompatActivity() {
    private var isCreditCardSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val cvCreditCard = findViewById<CardView>(R.id.cvCreditCard)
        val llCreditCardContainer = findViewById<LinearLayout>(R.id.llCreditCardContainer)
        val rbCreditCard = findViewById<View>(R.id.rbCreditCard)
        val llCreditCardDetails = findViewById<LinearLayout>(R.id.llCreditCardDetails)

        val cvCash = findViewById<CardView>(R.id.cvCash)
        val llCashContainer = findViewById<LinearLayout>(R.id.llCashContainer)
        val rbCash = findViewById<View>(R.id.rbCash)

        val btnPlaceOrder = findViewById<Button>(R.id.btnPlaceOrder)
        val tvSubTotal = findViewById<TextView>(R.id.tvSubTotal)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)

        // Retrieve the total price from the intent
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val formattedPrice = String.format(Locale.getDefault(), "%.2f AED", totalPrice)
        
        tvSubTotal.text = formattedPrice
        tvTotal.text = formattedPrice

        btnBack.setOnClickListener { finish() }

        // Initial State
        updatePaymentSelection(true, llCreditCardContainer, rbCreditCard, llCreditCardDetails, llCashContainer, rbCash)

        cvCreditCard.setOnClickListener {
            isCreditCardSelected = true
            updatePaymentSelection(true, llCreditCardContainer, rbCreditCard, llCreditCardDetails, llCashContainer, rbCash)
        }

        cvCash.setOnClickListener {
            isCreditCardSelected = false
            updatePaymentSelection(false, llCreditCardContainer, rbCreditCard, llCreditCardDetails, llCashContainer, rbCash)
        }

        btnPlaceOrder.setOnClickListener {
            val intent = Intent(this, OrderConfirmation::class.java)
            startActivity(intent)
        }
    }

    private fun updatePaymentSelection(
        isCC: Boolean,
        llCC: LinearLayout,
        rbCC: View,
        llCCDetails: LinearLayout,
        llCash: LinearLayout,
        rbCash: View
    ) {
        if (isCC) {
            llCC.setBackgroundResource(R.drawable.item_selected_border)
            rbCC.alpha = 1.0f
            llCCDetails.isVisible = true
            
            llCash.setBackgroundResource(R.drawable.input_background)
            rbCash.alpha = 0.2f
        } else {
            llCC.setBackgroundResource(R.drawable.input_background)
            rbCC.alpha = 0.2f
            llCCDetails.isVisible = false
            
            llCash.setBackgroundResource(R.drawable.item_selected_border)
            rbCash.alpha = 1.0f
        }
    }
}
