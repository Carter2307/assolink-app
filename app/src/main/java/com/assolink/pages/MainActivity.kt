package com.assolink.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.assolink.R
import com.assolink.ui.fragments.events.EventsFragment
import com.assolink.ui.fragments.HomeFragment
import com.assolink.ui.fragments.map.MapFragment
import com.assolink.ui.fragments.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var navHome: LinearLayout
    private lateinit var navEvent: LinearLayout
    private lateinit var navMap: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialisation des SharedPreferences
        sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Vérification de connexion
        if(!isUserLoggedIn()) {
            Log.d("AUTH_FLOW", "Redirection vers AuthActivity")
            redirectToAuth()
            return // IMPORTANT : Bloque l'exécution du reste du code
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des boutons de navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        // Boutons du menu de navigation
        navHome = findViewById(R.id.nav_home)
        navEvent = findViewById(R.id.nav_event)
        navMap = findViewById(R.id.nav_map)
        navProfile = findViewById(R.id.nav_profil)

        // Charge la page par défaut (la home page)
        loadFragment(HomeFragment())
        navHome.isSelected = true
        deselectOtherNavItem(navHome, getAllBottomNavItems())

        val navigationMap = HashMap<LinearLayout, Fragment>().apply {
            put(navHome, HomeFragment())
            put(navEvent, EventsFragment())  // Renamed to match target architecture
            put(navMap, MapFragment())
            put(navProfile, ProfileFragment())
        }

        // Configure listeners for navigation
        for (view in navigationMap.keys) {
            view.setOnClickListener {
                val navItems = getAllBottomNavItems()
                deselectOtherNavItem(view, navItems)
                view.isSelected = true
                val img = view.getChildAt(0)
                val text = view.getChildAt(1)
                img.alpha = 1f
                text.alpha = 0.5f
                navigationMap[view]?.let { frag -> loadFragment(frag) }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getAllBottomNavItems(): MutableList<LinearLayout> {
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
                val img = view.getChildAt(0)
                val text = view.getChildAt(1)
                img.alpha = 0.5f
                text.alpha = 0.5f
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isUserLoggedIn()) {
            redirectToAuth()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun redirectToAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }
}