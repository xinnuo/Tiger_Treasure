package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_add_manager.*
import org.greenrobot.eventbus.EventBus

class AddManagerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_manager)
        init_title("添加管家")
    }

    override fun init_title() {
//        bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//        bt_submit.isClickable = false
        et_mastername.addTextChangedListener(this)
        et_masterphone.addTextChangedListener(this)
        et_masterjob.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        when (v.id) {
            R.id.bt_submit -> {
                if(et_mastername.text.toString().isEmpty()){
                    toast("请输入管家姓名")
                    return
                }
                if(et_masterphone.text.toString().isEmpty()){
                    toast("请输入管家手机号")
                    return
                }
                if (!CommonUtil.isMobileNumber(et_masterphone.text.toString())) {
                    toast("手机号格式不正确！")
                    return
                }
                if(et_masterjob.text.toString().isEmpty()){
                    toast("请输入管家职务")
                    return
                }
                DialogHelper.showDialog(
                        baseContext,
                        "温馨提示",
                        "确定要添加该管家吗？",
                        "取消",
                        "确定") {
                    OkGo.post<String>(BaseHttp.manager_apply)
                            .tag(this@AddManagerActivity)
                            .isMultipart(true)
                            .headers("token", getString("token"))
//                            .params("companyId", intent.getStringExtra("companyId"))
                            .params("managerName", et_mastername.text.toString())
                            .params("positionName", et_masterjob.text.toString())
                            .params("accountName", et_masterphone.text.toString())
                            .execute(object : StringDialogCallback(baseContext) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    toast("提交成功")
                                    EventBus.getDefault().post(LocationMessageEvent(
                                            "",
                                            "",
                                            "",
                                            "添加管家"))

                                    ActivityStack.getScreenManager().popActivities(
                                            this@AddManagerActivity::class.java)
                                }

                            })
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        if (et_mastername.text.isNotBlank()
//                && et_masterphone.text.isNotBlank() && et_masterjob.text.isNotBlank()) {
//            bt_submit.setBackgroundResource(R.drawable.rec_bg_blue)
//            bt_submit.isClickable = true
//        } else {
//            bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//            bt_submit.isClickable = false
//        }

    }
}
