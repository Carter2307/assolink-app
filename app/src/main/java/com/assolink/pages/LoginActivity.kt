package com.assolink.pages

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.assolink.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Configurer la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.login_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        // Activer le bouton de retour
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Ajouter le gestionnaire de clic pour le lien d'inscription
        findViewById<TextView>(R.id.tvRegisterLink).setOnClickListener {
            // Démarrer RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
            finish() // Optionnel : ferme l'activité actuelle
        }
    }

    // Gérer le clic sur le bouton de retour
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
