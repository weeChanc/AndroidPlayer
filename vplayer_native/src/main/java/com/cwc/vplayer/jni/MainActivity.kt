package com.cwc.vplayer.jni

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import cn.jx.easyplayer.EasyPlayer
import cn.jx.easyplayer.EasyPlayerEventCallback
import com.cwc.vplayer.R

class MainActivity : AppCompatActivity(),
    SurfaceHolder.Callback {
    private var surfaceView: SurfaceView? = null
    private val easyPlayer: EasyPlayer = EasyPlayer()
    private val mainHandler = Handler(Looper.getMainLooper())

    init {

        System.loadLibrary("native-lib")
    }

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val folderurl =
            Environment.getExternalStorageDirectory().path
        val inputurl = "$folderurl/sintel.mp4"
        easyPlayer.setDataSource("file:///storage/emulated/0/Lark/download/IMG_3712.MOV")
        surfaceView = findViewById(R.id.video_view) as SurfaceView?
        surfaceView!!.holder.addCallback(this)
        easyPlayer.setEventCallback(object : EasyPlayerEventCallback {
            override fun onPrepared() {
                Log.d(TAG, "onPrepared")
                mainHandler.post {
                    val viewWidth = surfaceView!!.width
                    val videoWidth: Int = easyPlayer.getVideoWidth()
                    val videoHeight: Int = easyPlayer.getVideoHeight()
                    val lp = surfaceView!!.layoutParams
                    lp.width = viewWidth
                    lp.height =
                        (videoHeight.toFloat() / videoWidth.toFloat() * viewWidth.toFloat()).toInt()
                    surfaceView!!.layoutParams = lp
                }
                easyPlayer.start()
            }
        })
//        val pause = findViewById(R.id.pause) as Button
//        pause.setOnClickListener { easyPlayer.pause() }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated")
        easyPlayer.setSurface(holder.surface)
        easyPlayer.prepareAsync()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    internal inner class Play : Runnable {
        override fun run() {
            val folderurl =
                Environment.getExternalStorageDirectory().path
            val inputurl = "$folderurl/jack.mp4"
            //            String inputurl = "http://200000291.vod.myqcloud.com/200000291_5bdb30893e5848188f9f8d29c24b1fa6.f0.mp4";
//            String inputurl = "http://106.36.45.36/live.aishang.ctlcdn.com/00000110240001_1/encoder/1/playlist.m3u8";
//            String inputurl = "http://1251659802.vod2.myqcloud.com/vod1251659802/9031868222807497694/f0.mp4";
//            String inputurl = "rtmp://2107.liveplay.myqcloud.com/live/2107_3100673b756411e69776e435c87f075e";
//            play(inputurl, surfaceViewHolder.getSurface());
//            easyPlayer.play(inputurl, surfaceViewHolder.getSurface());
        }
    }

    fun onResolutionChange(width: Int, height: Int) {
        val display: Display = getWindowManager().getDefaultDisplay()
        val displayWidth = display.width
        mainHandler.post {
            // TODO Auto-generated method stub
            val params =
                surfaceView!!.layoutParams as RelativeLayout.LayoutParams
            params.height = displayWidth * height / width
            surfaceView!!.layoutParams = params
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}