package com.cwc.vplayer.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cwc.vplayer.ui.main.MainViewModel

abstract class AbsActivity<T : ViewModel> : AppCompatActivity() {
    lateinit var mViewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = createViewModel();
    }

    override fun onResume() {
        super.onResume()
        Log.e("AbsActivity",this.javaClass.name +" on Resume")
    }

    abstract fun createViewModel(): T

    override fun onPause() {
        super.onPause()
        Log.e("AbsActivity",this.javaClass.name +" on Pause")
    }
}