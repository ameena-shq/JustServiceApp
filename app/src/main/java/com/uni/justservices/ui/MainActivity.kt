package com.uni.justservices.ui

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.uni.justservices.BuildConfig
import com.uni.justservices.R
import com.uni.justservices.data.Data
import com.uni.justservices.data.NavigationDirectionEnum
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.ActivityMainBinding
import com.uni.justservices.ui.start.SplashActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),AppListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment:NavHostFragment

    var notificationCount = 0
    var chatCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myID = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        initGraph(intent.extras)
        binding.navigationView.setupWithNavController(navHostFragment.navController)
        binding.navigationView.setOnItemSelectedListener {menuItem->
            if (menuItem.itemId == R.id.notificationFragment){
                //clear badge
                binding.navigationView.removeBadge(menuItem.itemId)
            }
            if (menuItem.itemId == R.id.chatListFragment){
                //clear badge
                binding.navigationView.removeBadge(menuItem.itemId)
            }
            if (menuItem.itemId != navHostFragment.findNavController().currentDestination?.id)
                navHostFragment.findNavController().navigate(menuItem.itemId)

            return@setOnItemSelectedListener true
        }

        navHostFragment.navController.addOnDestinationChangedListener{controller,destination,arguments->
            showHideBottomNavigation(arguments)
        }

        binding.toolbar.userProfileImg.setOnClickListener {
            navHostFragment.navController.navigate(R.id.myProfileFragment)
        }
        //fetchUserProfileImg()
        //notificationLiveData()
        //fetchUserNotification()
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.let {intent->
                if (intent.hasExtra("updateChat")) {
                    val menuItem = binding.navigationView.menu.getItem(1).itemId
                    val badge = binding.navigationView.getOrCreateBadge(menuItem)
                    badge.backgroundColor = getColor(R.color.red)
                }else{
                    val menuItem = binding.navigationView.menu.getItem(2).itemId
                    val badge = binding.navigationView.getOrCreateBadge(menuItem)
                    badge.backgroundColor = getColor(R.color.red)
                }
            }

        }
    }

    override fun fetchUserProfileImg(){
        runBlocking {
            val user = UserLocalDataSource(this@MainActivity).getUserData()
            user?.let { data->
                if (data.img.isNotEmpty())
                    Glide.with(this@MainActivity)
                        .load(data.img)
                        .into(binding.toolbar.userProfileImg)
            }
        }
    }

    /*override fun updateNotificationBadge(count:Int) {
        val menuItem = binding.navigationView.menu.getItem(2).itemId
        val badge = binding.navigationView.getOrCreateBadge(menuItem)
        if (count > 0 && count > notificationCount)
            badge.backgroundColor = getColor(R.color.red)
        else
            binding.navigationView.removeBadge(menuItem)
        notificationCount = count
    }*/

   /* override fun updateChatBadge(count:Int) {
        val menuItem = binding.navigationView.menu.getItem(1).itemId
        val badge = binding.navigationView.getOrCreateBadge(menuItem)
        if (count > 0 && count > chatCount)
            badge.backgroundColor = getColor(R.color.red)
        else
            binding.navigationView.removeBadge(menuItem)
        chatCount = count
    }*/

    private fun initGraph(bundle: Bundle?){
        val startDestination = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            bundle?.getSerializable(OPEN_SCREEN,NavigationDirectionEnum::class.java)
        else
            bundle?.getSerializable(OPEN_SCREEN)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation)
        when(startDestination){
            NavigationDirectionEnum.LOGIN->{
                graph.setStartDestination(R.id.signInFragment)
            }
            NavigationDirectionEnum.HOME->{
                graph.setStartDestination(R.id.homeFragment)
            }
            NavigationDirectionEnum.CREATE_PROFILE->{
                graph.setStartDestination(R.id.createUserProfileFragment)
            }
            else->{
                graph.setStartDestination(R.id.homeFragment)
            }
        }
        navHostFragment.navController.setGraph(graph,bundle)
        if (startDestination == null)
            navHostFragment.navController.navigate(R.id.notificationFragment)
    }



    private fun showHideBottomNavigation(arguments:Bundle?){
        val showBottomNavigation = arguments?.getBoolean("showBottomNavigationBar",false) == true
        binding.navigationView.isVisible = showBottomNavigation
    }

    override fun showToastMsg(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun showHideToolbar(show: Boolean) {
        binding.toolbar.root.isVisible = show
    }

    override fun showHideProgress(show: Boolean) {
        binding.layoutProgress.root.isVisible = show
    }

    override fun relaunchApp() {
        val intent = Intent(this,SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    companion object{
        const val OPEN_SCREEN="OPEN_SCREEN"
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            messageReceiver,
            IntentFilter("MyData")
        )
    }
}