package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_enterprise.*

class EnterpriseBeforeActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_before)
        init_title("企业注册")
    }

    override fun init_title() {
        super.init_title()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_deal -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "服务协议")
                startActivity(intent)
            }
            R.id.bt_done -> {
                if (enterprise_name.text.isBlank()) {
                    enterprise_name.requestFocus()
                    toast("请输入企业全称或企业代码")
                    return
                }
                OkGo.post<String>(BaseHttp.company_info)
                        .tag(this@EnterpriseBeforeActivity)
                        .isMultipart(true)
                        .params("keyword", enterprise_name.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                intent.setClass(baseContext, EnterpriseActivity::class.java)
                                intent.putExtra("compName", enterprise_name.text.trim().toString())
                                startActivity(intent)
                            }

                        })


            }
        }
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业认证（输入企业名称）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业认证（输入企业名称）")
    }
}
