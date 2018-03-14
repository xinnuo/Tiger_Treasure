package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.text.InputFilter
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.CompanyData
import com.ruanmeng.model.TicketMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_ticket.*
import org.greenrobot.eventbus.EventBus

class TicketActivity : BaseActivity() {

    private var invoiceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        init_title("设置发票信息")

        getData()
    }

    override fun init_title() {
        super.init_title()
        ticket_name.filters = arrayOf<InputFilter>(NameLengthFilter(12))

        ticket_done.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        ticket_done.isClickable = false

        ticket_company.addTextChangedListener(this)
        ticket_num.addTextChangedListener(this)
        ticket_tel.addTextChangedListener(this)
        ticket_ying.addTextChangedListener(this)
        ticket_bank.addTextChangedListener(this)
        ticket_card.addTextChangedListener(this)
        ticket_name.addTextChangedListener(this)
        ticket_phone.addTextChangedListener(this)
        ticket_address.addTextChangedListener(this)
        ticket_email.addTextChangedListener(this)

        ticket_done.setOnClickListener {
            /*if (!BankcardHelper.checkBankCard(ticket_card.text.trim().toString())) {
                toast("银行卡号格式错误，请重新输入")
                ticket_card.setText("")
                ticket_card.requestFocus()
                return@setOnClickListener
            }*/

            if (!CommonUtil.isMobileNumber(ticket_phone.text.trim().toString())) {
                toast("手机号码格式错误，请重新输入")
                ticket_phone.setText("")
                ticket_phone.requestFocus()
                return@setOnClickListener
            }
            if (!CommonUtil.isEmail(ticket_email.text.trim().toString())) {
                toast("电子邮箱格式错误，请重新输入")
                ticket_email.setText("")
                ticket_email.requestFocus()
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.invoice_add)
                    .tag(this@TicketActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("invoiceId", invoiceId)
                    .params("addressee", ticket_name.text.trim().toString())
                    .params("bankName", ticket_bank.text.trim().toString())
                    .params("invoiceTitle", ticket_name.text.trim().toString())
                    .params("bankNo", ticket_card.text.trim().toString())
                    .params("telephone", ticket_phone.text.trim().toString())
                    .params("addressInfo", ticket_address.text.trim().toString())
                    .params("compEmail", ticket_email.text.trim().toString())
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            toast(msg)
                            EventBus.getDefault().post(TicketMessageEvent(invoiceId, "发票信息"))
                            ActivityStack.getScreenManager().popActivities(this@TicketActivity::class.java)
                        }

                    })
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CompanyData>>(BaseHttp.findinvoice_by_company)
                .tag(this@TicketActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CompanyData>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<CompanyData>>) {

                        val data = response.body().`object`
                        invoiceId = data.invoiceId
                        ticket_company.text = data.compName
                        ticket_num.text = data.compCode
                        ticket_tel.text = data.compTel
                        ticket_ying.text = data.address
                        ticket_bank.setText(data.bankName)
                        ticket_card.setText(data.bankNo)
                        ticket_name.setText(data.invoiceTitle)
                        ticket_phone.setText(data.telephone)
                        ticket_address.setText(data.addressInfo)
                        ticket_email.setText(data.compEmail)
                    }

                })
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (ticket_company.text.isNotBlank()
                && ticket_num.text.isNotBlank()
                && ticket_tel.text.isNotBlank()
                && ticket_ying.text.isNotBlank()
                && ticket_bank.text.isNotBlank()
                && ticket_card.text.isNotBlank()
                && ticket_name.text.isNotBlank()
                && ticket_phone.text.isNotBlank()
                && ticket_email.text.isNotBlank()
                && ticket_address.text.isNotBlank()) {
            ticket_done.setBackgroundResource(R.drawable.rec_bg_blue)
            ticket_done.isClickable = true
        } else {
            ticket_done.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            ticket_done.isClickable = false
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("设置发票信息")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("设置发票信息")
    }

}
