package com.cwc.vplayer.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

abstract class AbsFragment<T : ViewModel> : Fragment() {
    lateinit var mViewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = createViewModel()
    }

    abstract fun createViewModel(): T
}