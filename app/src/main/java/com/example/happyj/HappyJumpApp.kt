package com.example.happyj

import android.app.Application
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.SessionRepository
import com.example.happyj.data.TokenHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HappyJumpApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        NetworkModule.init(this)
        appScope.launch(Dispatchers.IO) {
            val s = SessionRepository(this@HappyJumpApp).currentSession()
            TokenHolder.token = s?.token
        }
    }
}
