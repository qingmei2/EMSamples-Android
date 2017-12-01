package com.qingmei2.samplehuanxin.em

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions
import com.qingmei2.samplehuanxin.em.account.EmAccountManager
import com.qingmei2.samplehuanxin.em.account.IEmAccountManager
import com.qingmei2.samplehuanxin.em.message.EmMessageManager
import com.qingmei2.samplehuanxin.em.message.IEmMessageManager

/**
 * Created by QingMei on 2017/11/30.
 * desc:EM代理类
 */
class EmManager(val application: Context) : IEmAccountManager, IEmMessageManager {

    val TAG = "EmManager"

    var accoutManager = EmAccountManager(application)
    var messageManager = EmMessageManager(application)

    fun initConfig() {
        val pid = android.os.Process.myPid()
        val processAppName = getAppName(pid)
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName!!.equals(application.getPackageName(), ignoreCase = true)) {
            Log.e(TAG, "enter the service process!")

            // 则此application::onCreate 是被service 调用的，直接返回
            return
        }

        val options = EMOptions().apply {
            setAcceptInvitationAlways(false)    // 默认添加好友时，是不需要验证的，改成需要验证
            setAutoTransferMessageAttachments(true)// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
            setAutoDownloadThumbnail(true)  // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
            setAutoLogin(false)             //是否自动登录
        }
        EMClient.getInstance().apply {
            init(application, options)         //初始化
            setDebugMode(true)           // 在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        }
    }

    private fun getAppName(pID: Int): String? {
        var processName: String? = null
        val am = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.runningAppProcesses
        val i = l.iterator()
        while (i.hasNext()) {
            val info = i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid == pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) {
            }
        }
        return processName
    }

    override fun regist(userName: String,
                        password: String) {
        accoutManager.regist(userName, password)
    }

    override fun login(userName: String,
                       password: String) {
        accoutManager.login(userName, password)
    }

    override fun logout() {
        accoutManager.logout()
    }

    override fun singleText(content: String,
                            userName: String) {
        messageManager.singleText(content, userName)
    }

    override fun singleAudio(filePath: String,
                             length: Int,
                             password: String) {
        messageManager.singleAudio(filePath, length, password)
    }

    override fun startReceiveMessage() {
        messageManager.startReceiveMessage()
    }

    override fun stopReceiveMessage() {
        messageManager.stopReceiveMessage()
    }

}