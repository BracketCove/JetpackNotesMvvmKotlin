package com.wiseassblog.jetpacknotesmvvmkotlin.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<T>(protected val uiContext: CoroutineContext): ViewModel(), CoroutineScope {
    abstract fun handleEvent(event: T)

    protected lateinit var jobTracker: Job

    init {
        jobTracker = Job()
    }

    val error = MutableLiveData<String>()

    val loading = MutableLiveData<Unit>()

    override val coroutineContext: CoroutineContext
        get() = uiContext + jobTracker

}