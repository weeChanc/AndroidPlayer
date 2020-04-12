package com.cwc.vplayer.base.utils

import android.webkit.MimeTypeMap

object MediaUtils {
    fun isVideo(url: String): Boolean {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        if (mimeType != null && mimeType.contains("video")) {
            return true
        }
        return false;
    }
}