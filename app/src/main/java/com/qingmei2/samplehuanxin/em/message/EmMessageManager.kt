package com.qingmei2.samplehuanxin.em.message

import android.content.Context
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage


/**
 * Created by QingMei on 2017/11/30.
 * desc:
 */
class EmMessageManager(val application: Context) : IEmMessageManager {

    val msgListener = EmMessageListener(application)

    override fun startReceiveMessage() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }

    override fun stopReceiveMessage() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
    }

    override fun singleAudio(filePath: String,
                             length: Int,
                             password: String) {

    }

    override fun singleText(content: String,
                            userName: String) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id
        val message = EMMessage.createTxtSendMessage(content, userName)

        //如果是群聊，设置chattype，默认是单聊
//        if (chatType === CHATTYPE_GROUP)
//            message.chatType = ChatType.GroupChat

        EMClient.getInstance().chatManager().sendMessage(message)
    }

}