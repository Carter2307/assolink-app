package com.assolink.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.assolink.R
import com.assolink.databinding.FragmentProfileBinding
import com.assolink.databinding.DialogEditProfileBinding
import com.assolink.ui.activities.AuthActivity
import com.assolink.ui.viewmodels.ProfileViewModel
import com.assolink.ui.viewmodels.UpdateProfileStatus
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.username
            binding.tvEmail.text = user.email
            binding.tvAddress.text = user.address ?: getString(R.string.no_address)
        }

        profileViewModel.isDarkModeEnabled.observe(viewLifecycleOwner) { isDarkMode ->
            binding.switchDarkMode.isChecked = isDarkMode
            applyTheme(isDarkMode)
        }

        profileViewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is UpdateProfileStatus.Success -> {
                    Snackbar.make(binding.root, R.string.profile_updated, Snackbar.LENGTH_SHORT).show()
                }
                is UpdateProfileStatus.Error -> {
                    Snackbar.make(binding.root, status.message, Snackbar.LENGTH_LONG).show()
                }
                is UpdateProfileStatus.Loading -> {
                    // Optionnellement, montrer un loader
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            profileViewModel.toggleDarkMode(isChecked)
        }

        binding.btnLogout.setOnClickListener {
            profileViewModel.signOut()
            navigateToAuth()
        }
    }

    private fun showEditProfileDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(LayoutInflater.from(context))
        val currentUser = profileViewModel.userProfile.value

        // Préremplir avec les données actuelles
        dialogBinding.etUsername.setText(currentUser?.username)
        dialogBinding.etAddress.setText(currentUser?.address)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val newUsername = dialogBinding.etUsername.text.toString().trim()
                val newAddress = dialogBinding.etAddress.text.toString().trim()

                if (newUsername.isNotEmpty()) {
                    profileViewModel.updateProfile(newUsername, newAddress)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun navigateToAuth() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}