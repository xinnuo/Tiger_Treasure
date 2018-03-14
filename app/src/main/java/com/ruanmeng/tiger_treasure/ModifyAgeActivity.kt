package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.PersonMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_modify_age.*
import org.greenrobot.eventbus.EventBus

class ModifyAgeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_age)
        init_title("更改年龄")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "保存"

        if (getString("age").isNotEmpty()) {
            et_age.setText(getString("age") + "岁")
            et_age.setSelection(et_age.text.length)
        }

        btRight.setOnClickListener {
            if (et_age.text.isBlank()) {
                toast("请输入年龄")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.accountinfo_update)
                    .tag(this@ModifyAgeActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("age", et_age.text.toString())

                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            toast("修改成功")
                            EventBus.getDefault().post(PersonMessageEvent(2, et_age.text.toString()))
                            ActivityStack.getScreenManager().popActivities(this@ModifyAgeActivity::class.java)
                        }

                    })
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更改年龄")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更改年龄")
    }
}
