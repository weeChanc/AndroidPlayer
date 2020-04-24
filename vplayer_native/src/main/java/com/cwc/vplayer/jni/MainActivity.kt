package com.cwc.vplayer.jni

import android.graphics.SurfaceTexture
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.cwc.vplayer.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_main
        )

//        Log.d("zhumr", "CPU_ABI = $CPU_ABI.")

//        Log.e("tag", "start ${   File("/storage/emulated/0/temp").createNewFile()}")
//        Build.SUPPORTED_ABIS.forEach {
//            println(it)
//        } ve-lib")

//        AVUtils.videoDecode(
//            "file:///storage/emulated/0/Lark/download/IMG_3712.MOV",
//            "/storage/emulated/0/temp"
//        )

        texture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return false
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {


                thread{
                    AVUtils.videoRender("file:///storage/emulated/0/Lark/download/IMG_3712.MOV", Surface(surface))
                }

            }
        }
        Log.e("tag", "finish")
    }
}