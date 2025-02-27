package com.assolink.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assolink.R
import com.assolink.ui.viewmodels.AssociationDetailsViewModel
import com.assolink.ui.fragments.AssociationDetailsFragment

class AssociationDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: AssociationDetailsViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_association_details)

        progressBar = findViewById(R.id.progressBar)

        viewModel = AssociationDetailsViewModel()

        // Récupérer l'ID de l'association depuis l'intent
        val associationId = intent.getStringExtra("ASSOCIATION_ID")
            ?: throw IllegalArgumentException("Association ID est requis")

        // Observer pour le chargement
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observer pour les erreurs
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // Charger les détails de l'association
        viewModel.loadAssociationDetails(associationId)

        // Ajouter le fragment de détails
        if (savedInstanceState == null) {
            val fragment = AssociationDetailsFragment.newInstance(associationId)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}