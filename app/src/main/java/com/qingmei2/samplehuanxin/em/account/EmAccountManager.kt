package com.qingmei2.samplehuanxin.em.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.annotations.NotNull


/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
class EmAccountManager(val application: Context) : IEmAccountManager {

    val sp: SharedPreferences = application.getSharedPreferences("accountInfo", Context.MODE_PRIVATE)

    companion object {
        const val spUserName = "EM_ACCOUNT_USER_NAME"
        const val spPassword = "EM_ACCOUNT_USER_PWD"
        const val TAG = "EmAccountManager"
    }

    override fun getAccountInfo(): EmAccountInfo {
        val userName = sp.getString(spUserName, "")
        val password = sp.getString(spPassword, "")
        return EmAccountInfo(userName, password)
    }

    override fun saveAccountInfo(@NotNull account: EmAccountInfo) {
        sp.edit().apply {
            putString(spUserName, account.userName)
            putString(spPassword, account.pwd)
            apply()
        }
    }

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
                application.runOnUiThread {
                    application.toast("$userName 登录成功！")
                }
            }

            override fun onProgress(progress: Int, status: String) {
                Log.d(TAG, "login: onProgress")
            }

            override fun onError(code: Int, message: String) {
                Log.d(TAG, "登录聊天服务器失败！")
                application.runOnUiThread {
                    application.toast("$userName 登录失败！")
                }
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