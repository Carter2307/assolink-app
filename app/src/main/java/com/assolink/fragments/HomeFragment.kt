package com.assolink.fragments

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


class HomeFragment : Fragment() {
    private lateinit var assos: ArrayList<Asso>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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