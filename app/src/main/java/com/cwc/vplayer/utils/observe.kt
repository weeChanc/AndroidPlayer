package com.cwc.vplayer.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> MutableLiveData<T>.observe(lifecycleOwner: LifecycleOwner, action: (T) -> Unit) {
    observe(lifecycleOwner, Observer {
        action(it)
    })
}