package com.example.happyj

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Dispatchers inyectables (tests + Sonar S6310). */
interface AppDispatchers {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class DefaultAppDispatchers : AppDispatchers {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
}

fun defaultAppDispatchers(
    main: CoroutineDispatcher? = null,
    io: CoroutineDispatcher? = null,
): AppDispatchers {
    if (main == null && io == null) return DefaultAppDispatchers()
    return object : AppDispatchers {
        override val main: CoroutineDispatcher = main ?: Dispatchers.Main
        override val io: CoroutineDispatcher = io ?: Dispatchers.IO
    }
}
