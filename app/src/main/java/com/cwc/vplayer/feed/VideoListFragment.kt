package com.cwc.vplayer.feed

//import com.cwc.vplayer.utils.MediaRepository
import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.blankj.utilcode.util.ToastUtils
import com.cwc.vplayer.R
import com.cwc.vplayer.base.AbsFragment
import com.cwc.vplayer.base.utils.observe
import com.cwc.vplayer.controller.VideoManager
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.ui.main.MainViewModel
import com.ss.android.buzz.feed.live.AutoPreviewCoordinator
import kotlinx.android.synthetic.main.activity_feed_main.*
import java.io.File

class VideoListFragment : AbsFragment<VideoListViewModel>() {

    companion object {
        const val DATA_DIR = "DATA_DIR"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_feed_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        val adapter = VideoListAdapter()
        feed_recycler.adapter = adapter
        feed_recycler.layoutManager = LinearLayoutManager(context)
        val dir: String? = arguments?.getString(DATA_DIR)
        mainViewModel.mainTitle.value = File(dir).name
        mViewModel.init(mainViewModel,dir,this)
        val autoPreview = AutoPreviewCoordinator

        mViewModel.videoFiles.observe(this){
            adapter.items =it
            adapter.notifyDataSetChanged()
        }

        feed_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    autoPreview.handleLiveAutoPreview(
                        feed_recycler,
                        this@VideoListFragment.activity!!
                    )
                }
            }
        })

        add_btn.setOnClickListener {
            MaterialDialog(context!!).show {
                var input = ""
                input(hint = "请输入URL"){
                    dialog,text->
                    input = text.toString()
                }
                positiveButton(text= "添加",click = {
                    mainViewModel.videos.value = mainViewModel.videos.value?.toMutableList()?.apply {
                        add(VideoFile(input,dir,input,0,""))
                    }
                })
            }
        }
    }


    override fun onPause() {
        super.onPause()
        VideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        VideoManager.onResume(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoManager.releaseAllVideos()
    }

    override fun createViewModel(): VideoListViewModel {
        return ViewModelProvider(this).get(VideoListViewModel::class.java)
    }
}