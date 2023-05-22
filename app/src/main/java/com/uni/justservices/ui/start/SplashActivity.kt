package com.uni.justservices.ui.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.uni.justservices.data.NavigationDirectionEnum
import com.uni.justservices.data.local.UserLocalDataSource
import com.uni.justservices.databinding.ActivitySplashBinding
import com.uni.justservices.ui.MainActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                runBlocking {
                    val localDB = UserLocalDataSource(this@SplashActivity)
                    val user = localDB.getUserData()
                    user?.let {
                        intent.putExtra(OPEN_SCREEN, NavigationDirectionEnum.HOME)
                        /*it.details?.let {
                            intent.putExtra(OPEN_SCREEN, NavigationDirectionEnum.HOME)
                        }?: intent.putExtra(OPEN_SCREEN, NavigationDirectionEnum.CREATE_PROFILE)*/
                    }?:let {
                        intent.putExtra(OPEN_SCREEN, NavigationDirectionEnum.LOGIN)
                    }
                    startActivity(intent)
                    finish()
                }

            }
        ,300)
    }

    companion object{
        const val OPEN_SCREEN="OPEN_SCREEN"
    }
}