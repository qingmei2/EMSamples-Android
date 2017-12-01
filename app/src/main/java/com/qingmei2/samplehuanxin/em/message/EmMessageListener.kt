package com.qingmei2.samplehuanxin.em.message

import android.content.Context
import android.util.Log
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMMessage

/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
class EmMessageListener(val application: Context) : EMMessageListener {

    override fun onMessageRecalled(p0: MutableList<EMMessage>?) {

    }

    override fun onMessageChanged(p0: EMMessage?, p1: Any?) {

    }

    override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {

    }

    override fun onMessageReceived(ms: MutableList<EMMessage>?) {
        ms?.forEach {
            val s = it.body.toString()
            Log.d("tag","接收到信息：$s")
//            application.runOnUiThread {
//                application.toast("接收到信息：$s")
//            }
        }
    }

    override fun onMessageDelivered(p0: MutableList<EMMessage>?) {

    }

    override fun onMessageRead(p0: MutableList<EMMessage>?) {

    }
}