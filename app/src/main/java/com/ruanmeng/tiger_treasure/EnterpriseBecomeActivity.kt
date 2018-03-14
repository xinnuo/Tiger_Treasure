package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_enterprise_become.*
import kotlinx.android.synthetic.main.activity_login.*

class EnterpriseBecomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_become)
        init_title("企业资料")
    }

    override fun init_title() {
        super.init_title()
        enterprise_name.text = intent.getStringExtra("compName")
        enterprise_tel.text = intent.getStringExtra("compTel")
        enterprise_fa.text = intent.getStringExtra("legalMan")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_apply -> {
                if (!CommonUtil.isMobileNumber(et_phone.text.toString())) {
                    toast("手机号格式不正确！")
                    return
                }
                if (enterprise_mastername.text.isBlank()) {
                    toast("请输入管家姓名！")
                    return
                }
                if (enterprise_masterjob.text.isBlank()) {
                    toast("请输入管家职务！")
                    return
                }

                DialogHelper.showDialog(
                        baseContext,
                "温馨提示",
                "确定要申请成为该企业的管家吗？",
                "取消",
                "申请") {
                    OkGo.post<String>(BaseHttp.manager_apply)
                            .tag(this@EnterpriseBecomeActivity)
                            .headers("token", getString("token"))
                            .params("companyId", intent.getStringExtra("companyId"))
                            .params("managerName", enterprise_mastername.text.toString())
                            .params("positionName", enterprise_masterjob.text.toString())
                            .execute(object : StringDialogCallback(baseContext) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    toast("提交成功")

                                    intent.setClass(baseContext, EnterpriseHintActivity::class.java)
                                    intent.putExtra("info", msg)
                                    intent.putExtra("title", "申请企业管家")
                                    startActivity(intent)

                                    ActivityStack.getScreenManager().popActivities(
                                            this@EnterpriseBecomeActivity::class.java,
                                            CompanyInputActivity::class.java)
                                }

                            })
                }
            }
        }
    }
}
