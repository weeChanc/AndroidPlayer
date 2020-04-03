package com.cwc.vplayer.feed

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cwc.vplayer.R
import com.cwc.vplayer.controller.GSYVideoManager
import com.cwc.vplayer.utils.MediaRepository
import kotlinx.android.synthetic.main.activity_feed_main.*

class FeedMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_main)

        Log.e("FeedMainActivity", "good")
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

        val adapter = FeedListAdapter()
        feed_recycler.adapter = adapter
        feed_recycler.layoutManager = LinearLayoutManager(this)
        adapter.items = MediaRepository.getVideo()
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }


}