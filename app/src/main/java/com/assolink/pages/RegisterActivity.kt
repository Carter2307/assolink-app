package com.assolink.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.assolink.MainActivity
import com.assolink.R
import com.assolink.db.DBInstance
import com.assolink.db.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        // Configurer la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.register_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        // Activer le bouton de retour dans la barre d'action
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ajouter le gestionnaire de clic pour le lien vers la page de connexion
        findViewById<TextView>(R.id.tvLoginLink).setOnClickListener {
            // Démarrer RegisterActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Optionnel : ferme l'activité actuelle
        }

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {

            Toast.makeText(this, "Register button clicked", Toast.LENGTH_SHORT).show()

            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val address = etAddress.text.toString()

            // Input validation
            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || address.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert user into the database
            val user = UserEntity(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                address = address
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = DBInstance.db ?: DBInstance.init(applicationContext)
                    Log.d("RegisterActivity", "Database instance acquired")
                    db.userDao().insertAll(user)
                    Log.d("RegisterActivity", "User inserted into database")
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                } catch (e: Exception) {
                    Log.e("RegisterActivity", "Error inserting user: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Error registering user: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    // Gérer le clic sur le bouton de retour
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.etFirstName).text.clear()
        findViewById<EditText>(R.id.etLastName).text.clear()
        findViewById<EditText>(R.id.etEmail).text.clear()
        findViewById<EditText>(R.id.etPassword).text.clear()
        findViewById<EditText>(R.id.etAddress).text.clear()
    }
}
