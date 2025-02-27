package com.assolink.pages

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.assolink.views.fragments.EventFragment
import com.assolink.views.fragments.HomeFragment
import com.assolink.views.fragments.MapFragment
import com.assolink.views.fragments.ProfileFragment
import com.assolink.R


class MainActivity : AppCompatActivity() {
    private lateinit var navHome: LinearLayout
    private lateinit var navEvent: LinearLayout
    private lateinit var navMap: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boutons du menu de navigation
        navHome = findViewById(R.id.nav_home)
        navEvent = findViewById(R.id.nav_event)
        navMap = findViewById(R.id.nav_map)
        navProfile = findViewById(R.id.nav_profil)

        // Définir les listeners
        navHome.setOnClickListener { loadFragment(HomeFragment()) }
        navEvent.setOnClickListener { loadFragment(EventFragment()) }
        navMap.setOnClickListener {
            // Naviguer vers l'écran de la carte
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment())
                .commit()
        }
        navProfile.setOnClickListener { loadFragment(ProfileFragment()) }

        // Charger le fragment par défaut
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }


    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getAllBottomNavItems():  MutableList<LinearLayout> {
        val parentLinearLayout = findViewById<LinearLayout>(R.id.bottom_navigation)
        val linearLayouts: MutableList<LinearLayout> = ArrayList()
        val childCount = parentLinearLayout.childCount

        for (i in 0 until childCount) {
            val child = parentLinearLayout.getChildAt(i)

            if (child is LinearLayout) {
                linearLayouts.add(child)
            }
        }

        return linearLayouts
    }

    private fun deselectOtherNavItem(current: LinearLayout, items: MutableList<LinearLayout>) {
        for(view in items) {
            if(view.id != current.id) {
                view.isSelected = false
                val  img  = view.getChildAt(0)
                val text = view.getChildAt(1)

                img.alpha = 0.5f;
                text.alpha = 0.5f;
            }
        }
    }


}