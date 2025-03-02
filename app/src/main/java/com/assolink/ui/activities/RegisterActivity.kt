package com.assolink.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.assolink.R
import com.assolink.databinding.ActivityRegisterBinding
import com.assolink.ui.viewmodels.AuthState
import com.assolink.ui.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupUI()
        observeAuthState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.registerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val firstName = binding.etFirstName.text.toString().trim()
                val lastName = binding.etLastName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                val address = binding.etAddress.text.toString().trim()

                // Créer un username à partir des noms
                val username = "$firstName $lastName"

                binding.progressBar.isVisible = true
                authViewModel.register(email, password, username, address)
            }
        }

        binding.tvLoginLink.setOnClickListener {
            finish() // Retourne à l'écran précédent
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validation du prénom
        if (binding.etFirstName.text.toString().trim().isEmpty()) {
            binding.tilFirstName.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            binding.tilFirstName.error = null
        }

        // Validation du nom
        if (binding.etLastName.text.toString().trim().isEmpty()) {
            binding.tilLastName.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            binding.tilLastName.error = null
        }

        // Validation de l'email
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_required_field)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Validation du mot de passe
        val password = binding.etPassword.text.toString()
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_required_field)
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Validation de l'adresse
        if (binding.etAddress.text.toString().trim().isEmpty()) {
            binding.tilAddress.error = getString(R.string.error_required_field)
            isValid = false
        } else {
            binding.tilAddress.error = null
        }

        return isValid
    }

    private fun observeAuthState() {
        authViewModel.authState.observe(this) { state ->
            binding.progressBar.isVisible = false

            when (state) {
                is AuthState.Authenticated -> {
                    // Rediriger vers MainActivity en attendant OnboardingActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                is AuthState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                else -> { /* Ne rien faire */ }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}