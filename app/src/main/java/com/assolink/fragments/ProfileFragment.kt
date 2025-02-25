package com.assolink.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.assolink.R

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.assolink.db.DBInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPref = requireContext().getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )

        val tvFullName = view.findViewById<TextView>(R.id.tvFullName)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvAddress = view.findViewById<TextView>(R.id.tvAddress)

        lifecycleScope.launch {
            // Accès sécurisé à la base de données
            val db = DBInstance.getDatabase(requireContext())
            val userEmail = sharedPref.getString("user_email", null)

            userEmail?.let { email ->
                val user = withContext(Dispatchers.IO) {
                    db.userDao().getUserByEmail(email)
                }

                activity?.runOnUiThread {
                    user?.let {
                        tvFullName.text = "${it.firstName} ${it.lastName}"
                        tvEmail.text = it.email
                        tvAddress.text = it.address
                    }
                }
            }
        }

        return view

    }
}