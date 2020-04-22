package com.cwc.vplayer.ui.like

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cwc.vplayer.App
import com.cwc.vplayer.R
import com.cwc.vplayer.base.AbsFragment
import com.cwc.vplayer.ui.feed.VideoListFragment
import com.cwc.vplayer.ui.main.MainViewModel

class LikeFragment : VideoListFragment<LikeViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        mainViewModel.mainTitle.value = "我的收藏"
        view.findViewById<View>(R.id.add_btn).visibility = View.GONE
    }

    override fun createViewModel(): LikeViewModel {
        return LikeViewModel(App.app)
    }
}