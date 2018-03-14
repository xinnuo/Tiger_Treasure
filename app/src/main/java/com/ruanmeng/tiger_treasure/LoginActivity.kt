package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.RongCloudContext
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.push.RongPushClient
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    private var accountType = "App_Manager"
    private var time_count: Int = 90
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""
    private var verifyType: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init_title("登录")
        hide_title()
    }

    override fun init_title() {
        super.init_title()

//        bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//        bt_login.isClickable = false

        et_phone.addTextChangedListener(this)
        et_yzm.addTextChangedListener(this)

        if (getString("telephone").isNotEmpty()) {
            et_phone.setText(getString("telephone"))
            et_phone.setSelection(et_phone.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {
            if (intent.getBooleanExtra("isDialog", false)) {
                DialogHelper.showDialog(this, "提示", "您的账号在别的设备登录，您被迫下线！", "知道了", null)
            }

            clearData()

            ActivityStack.getScreenManager().popAllActivityExcept(this@LoginActivity::class.java)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_yzm -> {
                if (!CommonUtil.isMobileNumber(et_phone.text.toString())) {
                    toast("手机号格式不正确！")
                    return
                }
                thread = Runnable {
                    bt_yzm.text = "$time_count 秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "获取验证码"
                        bt_yzm.isClickable = true
                        bt_yzm.setTextColor(resources.getColor(R.color.colorAccent))
                        time_count = 90
                    }
                }

                OkGo.post<String>(BaseHttp.get_smscode)
                        .tag(this@LoginActivity)
                        .params("mobile", et_phone.text.toString())
                        .params("isreg", false)
                        .params("smsKey", "hOWt3hiakXHrePCqDKUsPz5T6f7j8P")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_phone.text.toString()
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                bt_yzm.setTextColor(resources.getColor(R.color.light))
                                time_count = 90
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.bt_login -> {
                if (et_phone.text.toString().isEmpty()) {
                    toast("请输入手机号")
                    return
                }
                if (!CommonUtil.isMobileNumber(et_phone.text.toString())) {
                    toast("请输入正确的手机号")
                    return
                }

                if (et_phone.text.toString() != mTel && verifyType.equals(2)) {
                    toast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.toString() != YZM && verifyType.equals(2)) {
                    toast("验证码错误，请重新输入")
                    return
                }
                if (et_yzm.text.toString().isBlank() && verifyType.equals(1)) {
                    toast("请输入密码")
                    return
                }

                OkGo.post<String>(BaseHttp.account_login)
                        .tag(this@LoginActivity)
                        .params("accountName", et_phone.text.toString())
                        .params("password", et_yzm.text.toString())
                        .params("verifyType", verifyType)//1  密码
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getJSONObject("object")

                                putString("token", obj.getString("token"))
                                putString("rongToken", obj.getString("rongToken"))
                                putString("accountType", obj.getString("accountType"))
                                putString("telephone", obj.getString("telephone"))
                                putString("platform", obj.getString("platformphone"))

                                putString("vipInfo", obj.getString("vipInfo"))
                                putString("userName", obj.getString("userName"))
                                putString("userhead", obj.getString("userhead"))
                                putString("areaId", obj.getString("areaId"))
                                putString("areaName", obj.getString("areaName"))
                                putString("lastLoginDate", obj.getString("lastLoginDate"))//最后登陆时间，目前用于判断第一次登陆会员提示弹窗
                                //用于判断首页左上角地址右边的小箭头是否显示
                                putString("vipCode", obj.getString("vipCode"))
                                putBoolean("isLogin", true)
                                putString("isGov", obj.getString("isGov"))
                                putString("vipLock", obj.optInt("vipLock").toString())
                                startActivity(MainActivity::class.java)
                                ActivityStack.getScreenManager().popActivities(this@LoginActivity::class.java)
                                MobclickAgent.onEvent(this@LoginActivity, "login_success")
                            }

                            override fun onError(response: Response<String>?) {
                                super.onError(response)
                                MobclickAgent.onEvent(this@LoginActivity, "login_faild")
                            }
                        })
            }
            R.id.tv_login_type -> {//使用验证码登录/使用密码登录
                if ("使用密码登录" == tv_login_type.text.toString()) {
                    verifyType = 1
                    tv_login_type.text = "使用验证码登录"
                    tv_login_se.text = "密码"
                    bt_yzm.visibility = View.GONE
                    et_yzm.hint = "请输入密码"
                } else {
                    verifyType = 2
                    tv_login_type.text = "使用密码登录"
                    tv_login_se.text = "验证码"
                    bt_yzm.visibility = View.VISIBLE
                    et_yzm.hint = "请输入验证码"
                }
            }
            R.id.bt_register -> {//企业注册
                intent.setClass(baseContext, EnterpriseBeforeActivity::class.java)
                intent.putExtra("compName", "")
                intent.putExtra("compCode", "")
                startActivity(intent)
            }
        //忘记密码
            R.id.bt_forget -> {
                startActivity(GetYZMActivity::class.java)
            }
        }

    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putString("token", "")
        putString("rongToken", "")
        putString("accountType", "")
        putString("hasCompany", "")
        putString("platform", "")

        putString("sex", "1")
        putString("vipTypeName", "")
        putString("expiryDate", "")
        putString("scNum", "0")
        putString("msgNum", "0")
        putString("footPrintNum", "0")

        putString("userhead", "")
        putString("userName", "")
        putString("age", "0")
        putString("positionName", "")
        putString("compTel", "")
        putString("telephoneIsOpen", "0")

        //清除通知栏消息
        RongCloudContext.getInstance().clearNotificationMessage()
        RongPushClient.clearAllPushNotifications(applicationContext)
        RongIM.getInstance().logout()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        if (et_phone.text.isNotBlank()
//                && et_yzm.text.isNotBlank()) {
//            bt_login.setBackgroundResource(R.drawable.rec_bg_blue)
//            bt_login.isClickable = true
//        } else {
//            bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//            bt_login.isClickable = false
//        }
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("登录")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("登录")
    }
}
