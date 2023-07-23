package com.masrofy

import com.masrofy.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import javax.inject.Inject

class TestDispatcherProvider @Inject constructor():DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = StandardTestDispatcher()
    @OptIn(ExperimentalCoroutinesApi::class)
    override val io: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val default: CoroutineDispatcher
        get() = StandardTestDispatcher()
}