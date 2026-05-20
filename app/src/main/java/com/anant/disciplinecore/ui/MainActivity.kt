package com.anant.disciplinecore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anant.disciplinecore.R
import com.anant.disciplinecore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(HomeFragment())

        setupBottomNavigation()

        setupFab()
    }

    private fun setupBottomNavigation() {

        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.nav_stats -> {
                    loadFragment(StatsFragment())
                    true
                }

                R.id.nav_focus -> {
                    loadFragment(FocusFragment())
                    true
                }

                R.id.nav_journal -> {
                    loadFragment(ReflectionFragment())
                    true
                }

                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun setupFab() {

        binding.fab.setOnClickListener {

            val dialog = AddHabitDialog()

            dialog.show(
                supportFragmentManager,
                "AddHabitDialog"
            )
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}