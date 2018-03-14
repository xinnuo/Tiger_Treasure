package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.tiger_treasure.ModifyPhoneActivity
import com.ruanmeng.tiger_treasure.R
import com.ruanmeng.utils.KeyboardHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_phone_third.*

class PhoneThirdFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_phone_third, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        modify_phone.text = (activity as ModifyPhoneActivity).getMobile()

        modify_yzm.initStyle(
                R.mipmap.pwd_bg,
                4,
                15f,
                R.color.background,
                R.color.black_dark,
                35)

        modify_yzm.setOnTextFinishListener { code ->
            if (code.length == 4) {
                KeyboardHelper.showSoftInput(activity)

                activity.window.decorView.postDelayed({
                    activity.runOnUiThread {
                        if (code == (activity as ModifyPhoneActivity).getYZM()) {
                            OkGo.post<String>(BaseHttp.mobile_update)
                                    .tag(this@PhoneThirdFragment)
                                    .params("new_mobile", (activity as ModifyPhoneActivity).getMobile())
                                    .params("old_mobile", getString("telephone"))
                                    .params("accountType", getString("accountType"))
                                    .params("new_vercode", code)
                                    .execute(object : StringDialogCallback(activity) {

                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                            toast(msg)
                                            putString("telephone", (activity as ModifyPhoneActivity).getMobile())
                                            (activity as ModifyPhoneActivity).getDone()
                                            (activity as ModifyPhoneActivity).changeResult()
                                        }

                                    })
                        } else {
                            toast("验证码错误，请重新输入")
                            modify_yzm.clearText()
                        }
                    }
                }, 300)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更换手机号（3）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更换手机号（3）")
    }
}
