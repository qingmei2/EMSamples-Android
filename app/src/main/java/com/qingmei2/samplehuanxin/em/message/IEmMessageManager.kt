package com.qingmei2.samplehuanxin.em.message

/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
interface IEmMessageManager {

    fun startReceiveMessage()

    fun stopReceiveMessage()

    fun singleText(content: String,
                   userName: String)

    fun singleAudio(filePath: String,
                    length: Int,
                    password: String)

}