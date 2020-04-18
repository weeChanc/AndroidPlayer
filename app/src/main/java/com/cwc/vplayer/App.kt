package com.cwc.vplayer

import android.app.Application
import android.os.SystemClock
import android.util.Log
import com.cwc.vplayer.entity.db.AppDataBase


class App : Application() {
    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this
//        val db = AppDataBase.INSTANC
        Thread.setDefaultUncaughtExceptionHandler { t, e ->

            Log.e("Crash !!! thread $t",e.message)
            SystemClock.sleep(3000)
            System.exit(-1)

        }
    }
}