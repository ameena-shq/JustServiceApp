package com.uni.justservices.ui.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.uni.justservices.R
import com.uni.justservices.data.NavigationDirectionEnum
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.ActivitySplashBinding
import com.uni.justservices.ui.MainActivity
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appLogo.root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                runBlocking {
                    val localDB = UserLocalDataSource(this@SplashActivity)
                    val user = localDB.getUserData()
                    user?.let {
                        intent.putExtra(OPEN_SCREEN, NavigationDirectionEnum.HOME)
                    }?:let {
                        intent.putExtra(OPEN_SCREEN, NavigationDirectionEnum.LOGIN)
                    }
                    startActivity(intent)
                    finish()
                }

            }
        ,600)
    }

    companion object{
        const val OPEN_SCREEN="OPEN_SCREEN"
    }
}