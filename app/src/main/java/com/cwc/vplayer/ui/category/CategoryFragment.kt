package com.cwc.vplayer.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cwc.vplayer.R
import com.cwc.vplayer.base.AbsFragment
import com.cwc.vplayer.base.utils.observe
import com.cwc.vplayer.entity.VideoCategory
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.feed.VideoListFragment
import com.cwc.vplayer.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_category.*
import java.io.File


class CategoryFragment : AbsFragment<CategoryViewModel>() {

    val adapter = CategoryAdapter()

    override fun createViewModel(): CategoryViewModel {
        return ViewModelProvider(this).get(CategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java);
        category_recyclerview.adapter = adapter
        category_recyclerview.layoutManager = GridLayoutManager(context, 3)

        adapter.register(VideoCategory::class.java, CategoryFileBinder {
            mainViewModel.displayFragment.value =
                VideoListFragment().apply {
                    arguments = Bundle().apply {
                        putString(
                            VideoListFragment.DATA_DIR,
                            it.dir.absolutePath
                        )
                    }
                }
        })

        mainViewModel.mainTitle.value = "文件夹"
        mainViewModel.videos.observe(this) {
            val categories = mViewModel.transfromVideoFileToCategory(it)
            mViewModel.categoryList.value = categories.map {
                File(it).let { file ->
                    val videoList =
                        file.listFiles().map { VideoFile.createFromFile(it) }.filterNotNull()
                    return@let VideoCategory(
                        file.name,
                        videoList.size,
                        file,
                        videoList
                    )
                }
            }
        }

        mViewModel.categoryList.observe(this) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }


}