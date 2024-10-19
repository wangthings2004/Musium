package com.jcxdc.musium.screen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        var bottomNav = binding.bottomNavigationView
        bottomNav.setupWithNavController(navController)
        setContentView(binding.root)
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.loginFragment -> hideBottomNavigation()
                R.id.registerFragment -> hideBottomNavigation()
                R.id.splashFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }
    private fun showBottomNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.visibility = View.GONE
    }
}