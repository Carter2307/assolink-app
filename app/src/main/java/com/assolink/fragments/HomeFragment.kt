package com.assolink.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assolink.R
import com.assolink.views.assoCard.Asso
import com.assolink.views.assoCard.AssoCardAdapter

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.assolink.db.DBInstance
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var assos: ArrayList<Asso>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Mettre à jour le header
        val tvFirstName = view.findViewById<TextView>(R.id.tvFirstName)
        val tvLastName = view.findViewById<TextView>(R.id.tvLastName)

        val userEmail = sharedPref.getString("user_email", null)
        if (userEmail != null) {
            lifecycleScope.launch {
                val user = DBInstance.getDatabase(requireContext()).userDao().getUserByEmail(userEmail)
                user?.let {
                    tvFirstName.text = it.firstName
                    tvLastName.text = it.lastName
                }
            }
        }


        assos = ArrayList(
            listOf(
                Asso("https://www.centrerosaparks.paris/wp-content/uploads/2019/10/Echecs-Activit%C3%A9s.jpg", "Club d'échec"),
                Asso("https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2234500/capsule_616x353.jpg?t=1717078366", "Mangaka"),
                Asso("https://www.monequipementsport.fr/media/wysiwyg/landing/joueur-foot-tenue-maillot-bleu.jpg", "FootLock"),
            )
        )

        val rvAssoCard: RecyclerView = view.findViewById(R.id.rv_home_fragment_asso_cards)
        val rvAssoCardAdapter = AssoCardAdapter(assos)

        rvAssoCard.adapter = rvAssoCardAdapter
        rvAssoCard.layoutManager =  LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        return view
    }


}