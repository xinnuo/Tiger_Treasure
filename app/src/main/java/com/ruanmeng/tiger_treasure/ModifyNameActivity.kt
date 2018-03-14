package com.ruanmeng.tiger_treasure

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.toast
import com.ruanmeng.model.PersonMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_modify_name.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class ModifyNameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_name)
        init_title("更改姓名")
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "保存"

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))

        if (getString("userName").isNotEmpty()) {
            et_name.setText(getString("userName"))
            et_name.setSelection(et_name.text.length)
        }

        btRight.setOnClickListener {
            if (et_name.text.isBlank()) {
                toast("请输入姓名")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.accountinfo_update)
                    .tag(this@ModifyNameActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("userName", et_name.text.toString())
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            toast("修改成功")

                            val obj = JSONObject(response.body()).getJSONObject("object")
                            if (!obj.isNull("userName"))
                                putString("userName", obj.getString("userName"))

                            RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                    getString("token"),
                                    getString("userName"),
                                    Uri.parse(BaseHttp.baseImg + getString("userhead"))))

                            EventBus.getDefault().post(PersonMessageEvent(1, getString("userName")))

                            ActivityStack.getScreenManager().popActivities(this@ModifyNameActivity::class.java)
                        }

                    })
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更改姓名")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更改姓名")
    }
}
