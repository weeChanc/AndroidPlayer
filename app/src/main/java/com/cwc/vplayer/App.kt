package com.cwc.vplayer

import android.app.Application
import com.cwc.vplayer.entity.db.AppDataBase


class App : Application() {
    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this
//        val db = AppDataBase.INSTANCE

        Thread.currentThread().uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e ->
            e.printStackTrace()
        }
    }
}