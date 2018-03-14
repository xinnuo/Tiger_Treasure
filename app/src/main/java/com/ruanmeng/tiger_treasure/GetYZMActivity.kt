package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_get_yzm.*
import org.json.JSONObject

class GetYZMActivity : BaseActivity() {
    private lateinit var thread: Runnable
    private var time_count: Int = 90
    private var YZM: String = ""
    private var mTel: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_yzm)
        init_title("忘记密码")
    }

    override fun init_title() {
        super.init_title()

    }
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_fyzm -> {
                if (et_fphone.text.toString().isEmpty()) {
                    toast("请输入手机号！")
                    return
                }
                if (!CommonUtil.isMobileNumber(et_fphone.text.toString())) {
                    toast("请输入正确的手机号！")
                    return
                }
                thread = Runnable {
                    bt_fyzm.text = "$time_count 秒后重发"
                    if (time_count > 0) {
                        bt_fyzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_fyzm.text = "获取验证码"
                        bt_fyzm.isClickable = true
                        bt_fyzm.setTextColor(resources.getColor(R.color.colorAccent))
                        time_count = 90
                    }
                }

                OkGo.post<String>(BaseHttp.get_smscode)
                        .tag(this@GetYZMActivity)
                        .params("mobile", et_fphone.text.toString())
                        .params("isreg", false)
                        .params("smsKey", "hOWt3hiakXHrePCqDKUsPz5T6f7j8P")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_fphone.text.toString()
                                if (BuildConfig.LOG_DEBUG) et_fyzm.setText(YZM)
//                                toast(""+YZM)
                                bt_fyzm.isClickable = false
                                bt_fyzm.setTextColor(resources.getColor(R.color.light))
                                time_count = 90
                                bt_fyzm.post(thread)
                            }

                        })
            }
            R.id.bt_fnext -> {
                if (!CommonUtil.isMobileNumber(et_fphone.text.toString())) {
                    toast("请输入正确的手机号")
                    return
                }

                if (et_fphone.text.toString() != mTel) {
                    toast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_fyzm.text.toString() != YZM) {
                    toast("验证码错误，请重新输入")
                    return
                }
                intent.setClass(baseContext, ForgetPswActivity::class.java)
                intent.putExtra("mTel", et_fphone.text.toString())
                intent.putExtra("mYzm", YZM)
                startActivity(intent)

            }

        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("忘记密码（获取验证码）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("忘记密码（获取验证码）")
    }
}
