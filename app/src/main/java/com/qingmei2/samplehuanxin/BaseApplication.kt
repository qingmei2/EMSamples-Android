package com.qingmei2.samplehuanxin

import android.app.Application
import android.support.multidex.MultiDex
import com.qingmei2.samplehuanxin.em.EmManager


/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
class BaseApplication : Application() {

    val emManager = EmManager(this)

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        initEM()
    }

    private fun initEM() {
        emManager.initConfig()
    }

}