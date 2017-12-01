package com.qingmei2.samplehuanxin.em.account

import android.content.Context
import android.util.Log
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import org.jetbrains.annotations.NotNull


/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
class EmAccountManager(val application: Context) : IEmAccountManager {

    private val TAG = "EmAccountManager"

    override fun regist(@NotNull userName: String,
                        @NotNull password: String) {
        //注册失败会抛出HyphenateException
        EMClient.getInstance().createAccount(userName, password)    //同步方法
    }

    override fun login(@NotNull userName: String,
                       @NotNull password: String) {
        EMClient.getInstance().login(userName, password, object : EMCallBack {
            //回调
            override fun onSuccess() {
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                Log.d(TAG, "登录聊天服务器成功！")
            }

            override fun onProgress(progress: Int, status: String) {
                Log.d(TAG, "login: onProgress")
            }

            override fun onError(code: Int, message: String) {
                Log.d(TAG, "登录聊天服务器失败！")
            }
        })
    }

    override fun logout() {
        EMClient.getInstance().logout(true, object : EMCallBack {

            override fun onSuccess() {

            }

            override fun onProgress(progress: Int, status: String) {

            }

            override fun onError(code: Int, message: String) {

            }
        })
    }
}