package com.uni.justservices.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.uni.justservices.R
import com.uni.justservices.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),AppListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        binding.navigationView.setupWithNavController(navHostFragment.navController)
        binding.navigationView.setOnItemSelectedListener {menuItem->
            if (menuItem.itemId != navHostFragment.findNavController().currentDestination?.id)
                navHostFragment.findNavController().navigate(menuItem.itemId)

            return@setOnItemSelectedListener true
        }

        navHostFragment.navController.addOnDestinationChangedListener{controller,destination,arguments->
            showHideBottomNavigation(arguments)
        }
    }

    private fun showHideBottomNavigation(arguments:Bundle?){
        val showBottomNavigation = arguments?.getBoolean("showBottomNavigationBar",false) == true
        binding.navigationView.isVisible = showBottomNavigation
    }

    override fun showToastMsg(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
}