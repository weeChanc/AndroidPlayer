package com.cwc.vplayer.ui.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.blankj.utilcode.util.ToastUtils
import com.cwc.vplayer.R
import com.cwc.vplayer.base.AbsFragment
import com.cwc.vplayer.utils.observe
import com.cwc.vplayer.entity.VideoCategory
import com.cwc.vplayer.entity.db.AppDataBase
import com.cwc.vplayer.ui.feed.VideoListFragment
import com.cwc.vplayer.ui.feed.VideoListViewModel
import com.cwc.vplayer.ui.history.HisotryItem
import com.cwc.vplayer.ui.history.HistoryItemBinder
import com.cwc.vplayer.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class CategoryFragment : AbsFragment<CategoryViewModel>() {

    private val REQUEST_CODE_SELECT_DIR = 100

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java);
        category_recyclerview.adapter = adapter
        category_recyclerview.layoutManager = GridLayoutManager(context, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position == 0 || position == 1) return 3
                    return 1
                }

            }
        }

        add_btn.setOnClickListener {
            MaterialDialog(context!!).show {
                folderChooser(context,
                        filter = {
                            it.isDirectory && !it.name.startsWith(".")
                        },
                        allowFolderCreation = true,
                        initialDirectory = File("/storage/emulated/0")) { dialog, file ->

                    GlobalScope.launch {
                        val categoriesInfo = VideoCategory(file.absolutePath, file.name, 0, null,
                                emptyList())
                        mainViewModel.categories.value?.forEach {
                            if (it.path.equals(file.absolutePath)) {
                                ToastUtils.showShort("不能重复创建")
                                return@launch
                            }
                        }
                        AppDataBase.INSTANCE.appDao().insertCategory(categoriesInfo)
                        mainViewModel.categories.postValue(mainViewModel.categories.value?.toMutableList()?.apply { add(categoriesInfo) })
                    }

                }
            }
        }

        adapter.register(VideoCategory::class.java, CategoryFileBinder {
            mainViewModel.displayFragment.value =
                    VideoListFragment<VideoListViewModel>().apply {
                        arguments = Bundle().apply {
                            putString(
                                    VideoListFragment.DATA_DIR,
                                    it.path
                            )
                        }
                    }
        })
        adapter.register(String::class.java, CategoryTitleBinder())
        adapter.register(HisotryItem::class.java,
                HistoryItemBinder()
        )

        mainViewModel.mainTitle.value = "文件夹"
        mainViewModel.categories.observe(this) {
            mViewModel.categoryList.value = it
        }

        mViewModel.categoryList.observe(this) {
            val displayItem = mutableListOf<Any>();
            val historyVideo = mainViewModel.videos?.value?.filter { it.lastPlayTimeStamp != 0L } ?: emptyList();
            val historyItem =
                    HisotryItem(historyVideo)
            displayItem.add(historyItem)
            displayItem.add("Category")
            displayItem.addAll(it)

            adapter.items = displayItem
            adapter.notifyDataSetChanged()
        }
    }


}