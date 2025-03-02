package com.assolink.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.assolink.databinding.ActivityAuthBinding
import com.assolink.ui.viewmodels.AuthState
import com.assolink.ui.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkIfAlreadyLoggedIn()
    }

    private fun checkIfAlreadyLoggedIn() {
        // Vérifie si un utilisateur est déjà connecté
        authViewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Authenticated -> navigateToHome()
                else -> { /* L'utilisateur n'est pas connecté, rester sur l'écran d'accueil */ }
            }
        }
    }

    private fun setupUI() {
        // Configurer les liens vers login et register
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}