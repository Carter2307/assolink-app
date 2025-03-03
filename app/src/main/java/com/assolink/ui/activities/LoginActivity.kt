package com.assolink.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.assolink.R

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialisation des SharedPreferences
        sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        setupToolbar()
        setupUIComponents()
        setupLoginButton()
        setupRegisterLink()

    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.login_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupUIComponents() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setupLoginButton() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                // Simulation de connexion réussie
                onLoginSuccess(email)
            }
        }
    }

    // Ajouter le gestionnaire de clic pour le lien vers la page d'inscription
    private fun setupRegisterLink() {
        findViewById<TextView>(R.id.tvRegisterLink).setOnClickListener {
            // Démarrer RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showError("L'email est requis")
                false
            }
            password.isEmpty() -> {
                showError("Le mot de passe est requis")
                false
            }
            else -> true
        }
    }

    private fun onLoginSuccess(email: String) {
        // Sauvegarde de l'état de connexion
        sharedPref.edit().apply {
            putBoolean("is_logged_in", true)
            putString("user_email", email)
            apply()
        }

        // Attendre 100ms pour être sûr que les préférences sont sauvegardées
        Handler(Looper.getMainLooper()).postDelayed({
            // Redirection vers l'écran principal
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 100)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Gérer le clic sur le bouton de retour
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
