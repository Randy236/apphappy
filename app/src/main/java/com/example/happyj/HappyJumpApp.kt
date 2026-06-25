package com.example.happyj

import android.app.Application
import com.example.happyj.data.NetworkModule
import com.example.happyj.data.SessionRepository
import com.example.happyj.data.TokenHolder
import com.example.happyj.notifications.CobrarPendienteNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HappyJumpApp : Application() {
    internal var dispatchers: AppDispatchers = defaultAppDispatchers()
        private set

    internal fun setDispatchersForTests(value: AppDispatchers) {
        dispatchers = value
    }

    private val appScope by lazy { CoroutineScope(SupervisorJob() + dispatchers.main) }

    override fun onCreate() {
        super.onCreate()
        CobrarPendienteNotifier.ensureChannel(this)
        NetworkModule.init(this)
        appScope.launch(dispatchers.io) {
            val s = SessionRepository(this@HappyJumpApp).currentSession()
            TokenHolder.token = s?.token
        }
    }
}
