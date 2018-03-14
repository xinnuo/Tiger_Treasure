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
import kotlinx.android.synthetic.main.activity_modify_duty.*
import org.greenrobot.eventbus.EventBus

class ModifyDutyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_duty)
        init_title("更改职务")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "保存"

        if (getString("positionName").isNotEmpty()) {
            et_duty.setText(getString("positionName"))
            et_duty.setSelection(et_duty.text.length)
        }

        btRight.setOnClickListener {
            if (et_duty.text.isBlank()) {
                toast("请输入职务")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.accountinfo_update)
                    .tag(this@ModifyDutyActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("positionName", et_duty.text.toString())
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            toast(msg)
                            EventBus.getDefault().post(PersonMessageEvent(3, et_duty.text.toString()))
                            ActivityStack.getScreenManager().popActivities(this@ModifyDutyActivity::class.java)
                        }

                    })
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更改职务")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更改职务")
    }
}
