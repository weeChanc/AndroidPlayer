package com.cwc.vplayer.ui.feed

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
import com.afollestad.materialdialogs.list.listItems
import com.blankj.utilcode.util.ToastUtils
import com.cwc.vplayer.R
import com.cwc.vplayer.base.AbsFragment
import com.cwc.vplayer.utils.observe
import com.cwc.vplayer.controller.VideoManager
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.entity.db.AppDataBase
import com.cwc.vplayer.ui.main.MainViewModel
import com.cwc.vplayer.utils.FileUtils
import com.ss.android.buzz.feed.live.AutoPreviewCoordinator
import kotlinx.android.synthetic.main.activity_feed_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

open class VideoListFragment<VM : VideoListViewModel> : AbsFragment<VM>() {

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
        adapter.register(VideoFile::class.java, VideoFileBinder { videoFile ->
            MaterialDialog(context!!).show {
                listItems(
                        items = arrayListOf("删除", "重命名", if (videoFile.isLike) "取消收藏 " else "收藏"),
                        selection = { dialog, index, text ->
                            when (index) {
                                0 -> {
                                    mainViewModel.deleteVideoFile(videoFile)
                                    ToastUtils.showShort("删除成功")
                                }

                                1 -> {
                                    MaterialDialog(context).show {
                                        input(hint = "请输入新的名字") { materialDialog, charSequence ->
                                            if (mainViewModel.updateVideo(videoFile)) {
                                                ToastUtils.showShort("修改成功")
                                            } else {
                                                ToastUtils.showShort("修改失败")
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    mainViewModel.updateVideo(videoFile.apply {
                                        this.isLike = !this.isLike
                                    })
                                }
                            }
                        })
            }
        })
        adapter.register(String::class.java, FeedCategoryBinder())
        feed_recycler.adapter = adapter
        feed_recycler.layoutManager = LinearLayoutManager(context)
        val dir: String? = arguments?.getString(DATA_DIR)
        if (dir != null) {
            mainViewModel.mainTitle.value = File(dir).name
        }
        mViewModel.init(mainViewModel, dir, this)
        val autoPreview = AutoPreviewCoordinator

        mViewModel.videoFiles.observe(this) {
            adapter.items = it.sortedWith(object : Comparator<VideoFile> {
                override fun compare(o1: VideoFile, o2: VideoFile): Int {
                    return -o1?.lastModify?.compareTo(o2?.lastModify)
                }

            })
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
                input(hint = "请输入URL") { dialog, text ->
                    input = text.toString()
                }
                positiveButton(text = "添加", click = {
                    mainViewModel.addVideoFile(
                            VideoFile(
                                    input,
                                    dir,
                                    input,
                                    0,
                                    0,
                                    System.currentTimeMillis()
                            )
                    )
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

    override fun createViewModel(): VM {
        return ViewModelProvider(this).get(VideoListViewModel::class.java) as VM
    }
}