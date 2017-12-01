package com.qingmei2.samplehuanxin

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.hyphenate.EMError
import com.hyphenate.exceptions.HyphenateException
import com.qingmei2.samplehuanxin.em.EmManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    private lateinit var emManager: EmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val application = application as BaseApplication
        emManager = application.emManager

        emManager.startReceiveMessage()

        btnLogin.setOnClickListener { login() }
        btnLogout.setOnClickListener { emManager.logout() }
        btnRegist.setOnClickListener { register() }
        btnSendMessage.setOnClickListener { sendMessage() }
    }

    private fun login() {
        emManager.login(etName.text.toString(), etPwd.text.toString())
    }

    private fun sendMessage() {
        val userName = etOther.text.toString()
        etMessage.text.toString().also {
            emManager.singleText(it, userName)
        }
    }

    private fun register() {
        val pd = ProgressDialog(this).apply {
            setMessage(resources.getString(R.string.Is_the_registered))
            show()
        }

        val userName = etName.text.toString()
        val password = etPwd.text.toString()

        doAsync {
            try {
                emManager.regist(userName, password)
                uiThread {
                    if (!this@MainActivity.isFinishing())
                        pd.dismiss()
                    Toast.makeText(applicationContext, resources.getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show()
                }
            } catch (e: HyphenateException) {
                uiThread {
                    if (!this@MainActivity.isFinishing())
                        pd.dismiss()
                    val errorCode = e.errorCode
                    if (errorCode == EMError.NETWORK_ERROR) {
                        Toast.makeText(applicationContext, resources.getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show()
                    } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                        Toast.makeText(applicationContext, resources.getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show()
                    } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                        Toast.makeText(applicationContext, resources.getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show()
                    } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                        Toast.makeText(applicationContext, resources.getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, resources.getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
