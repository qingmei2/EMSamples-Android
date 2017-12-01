package com.qingmei2.samplehuanxin.em.account

/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
interface IEmAccountManager {

    fun regist(userName: String,
               password: String)

    fun login(userName: String,
              password: String)

    fun logout()
}