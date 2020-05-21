package com.cwc.vplayer.ui.feed

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.cwc.vplayer.R
import com.cwc.vplayer.view.display.StandardVideoPlayer

class StandardPreviewPlayer(context: Context, attributeSet: AttributeSet?) :
        StandardVideoPlayer(context, attributeSet) {

    constructor(context: Context) : this(context, null)

    var previewMode = 0 // 0  not preview , 1 preview
        set(value) {
            field = value
            hideOrShowWidget(value)
        }

    override fun init(context: Context?) {
        super.init(context)
        hideOrShowWidget(previewMode)
    }

    private fun hideOrShowWidget(previewMode: Int) {
        val view = findViewById<View>(R.id.player_widiget_container) ?: return;
        if (previewMode == 0) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}