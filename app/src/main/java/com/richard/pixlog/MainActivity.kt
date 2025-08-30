package com.richard.pixlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.richard.pixlog.databinding.ActivityMainBinding
import com.richard.pixlog.ui.screen.login.LoginActivity

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)

        val mainViewModel : MainViewModel by viewModels{
            MainViewModelFactory.getInstance(this)
        }

        mainViewModel.checkToken().observe(this) { hastoken ->
            Log.d("MainActivity", "onCreate: $hastoken")
            if(!hastoken){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{

                setContentView(binding.root)

                val navView: BottomNavigationView = binding.navView

                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                    )
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)

                // Hide bottom nav on Dashboard, show elsewhere
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    navView.visibility = if (destination.id == R.id.navigation_dashboard) View.GONE else View.VISIBLE
                }

            }
        }




    }
}