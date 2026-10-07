package ae.ac.uowdubai.csit242_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUser = findViewById<EditText>(R.id.etLogin)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()


        // Login Button Click
        btnLogin.setOnClickListener {
            val email = etUser.text.toString()
            val password = etPass.text.toString()
            loginUser(email, password)
        }

        // Navigate to Registration
        tvToRegister.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }

    // Login function
    private fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Successful login
                        val user = auth.currentUser
                        Toast.makeText(this, "Welcome, ${user?.email}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Home::class.java)
                        intent.putExtra("username", user?.email)
                        startActivity(intent)
                        finish()
                    } else {
                        // Failed login
                        Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
        }
    }
}