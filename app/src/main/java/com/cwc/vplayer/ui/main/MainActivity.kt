package com.cwc.vplayer.ui.main

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.cwc.vplayer.R
import com.cwc.vplayer.base.AbsActivity
import com.cwc.vplayer.base.utils.observe
import com.cwc.vplayer.controller.VideoManager
import com.cwc.vplayer.ui.category.CategoryFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AbsActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mViewModel.displayFragment.value = CategoryFragment()
        mViewModel.mainTitle.observe(this) { title: String ->
            main_toolbar.title = title
        }
        Log.e("cwc", mViewModel.hashCode().toString())
        mViewModel.displayFragment.observe(this) {
            if (it is CategoryFragment) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it).commit()
            } else {
                supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, it).commit()
            }

        }
    }

    override fun createViewModel(): MainViewModel {
        return ViewModelProvider(this).get(MainViewModel::class.java)
    }


    override fun onBackPressed() {
        if (VideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }
}

