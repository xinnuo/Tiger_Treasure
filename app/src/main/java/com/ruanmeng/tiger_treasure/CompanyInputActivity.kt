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
import kotlinx.android.synthetic.main.activity_company_input.*
import org.json.JSONObject

class CompanyInputActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_input)
        init_title("企业资料认证")
    }

    override fun init_title() {
        super.init_title()
        company_next.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        company_next.isClickable = false

        et_name.addTextChangedListener(this)
        et_code.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.company_next -> {
                if (et_code.text.length < 18) {
                    toast("请输入18位社会信用代码")
                    return
                }

                OkGo.post<String>(BaseHttp.company_cheeck)
                        .tag(this@CompanyInputActivity)
                        .headers("token", getString("token"))
                        .params("compName", et_name.text.trim().toString())
                        .params("compCode", et_code.text.trim().toString().toUpperCase())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val isReg = JSONObject(response.body()).getJSONObject("object").getBoolean("isReg")

                                if (isReg) {
                                    toast(msg)

                                    when(getString("accountType")) {
                                        "App_Manager" -> {
                                            val obj = JSONObject(response.body()).getJSONObject("object").getJSONObject("company")

                                            intent.setClass(baseContext, EnterpriseBecomeActivity::class.java)
                                            intent.putExtra("compName", obj.getString("compName"))
                                            intent.putExtra("compTel", obj.getString("compTel"))
                                            intent.putExtra("legalMan", obj.getString("legalMan"))
                                            intent.putExtra("companyId", obj.getString("companyId"))
                                            startActivity(intent)
                                        }
                                        "App_Company" -> { }
                                    }
                                } else {
                                    intent.setClass(baseContext, EnterpriseActivity::class.java)
                                    intent.putExtra("compName", et_name.text.trim().toString())
                                    intent.putExtra("compCode", et_code.text.trim().toString().toUpperCase())
                                    startActivity(intent)
                                }
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_code.text.isNotBlank()) {
            company_next.setBackgroundResource(R.drawable.rec_bg_blue)
            company_next.isClickable = true
        } else {
            company_next.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            company_next.isClickable = false
        }
    }
}
