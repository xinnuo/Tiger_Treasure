package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.PurchaseData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_issue_second.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class IssueSecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_second)
        init_title("发布采购信息")
        if (intent.getStringExtra("mine") != null) {
            getData()//我的发布-编辑-详情
        }
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "发布"
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.issue_date -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                DialogHelper.showDateDialog(baseContext,
                        year_now,
                        year_now + 2,
                        3,
                        "截止时间",
                        true,
                        false) { _, _, _, _, _, date ->
                    issue_date_tv.text = date
                }
            }
            R.id.btn_nav_right -> {
                if (et_buy.text.isBlank()) {
                    toast("请输入采购信息")
                    et_buy.requestFocus()
                    return
                }

                if (et_count.text.isBlank()) {
                    toast("请输入采购数量")
                    et_count.requestFocus()
                    return
                }

//                if (issue_date_tv.text.isBlank()) {
//                    toast("请选择截止日期")
//                    return
//                }

                OkGo.post<String>(BaseHttp.purchasing_add)
                        .tag(this@IssueSecondActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            if (intent.getStringExtra("mine") != null) {
                                params("purchasingId", intent.getStringExtra("purchasingId"))//编辑的时候把id提交上去
                            }
                            params("purchasingDescribe", et_buy.text.trim().toString())
                            params("purchasingNum", et_count.text.trim().toString())
//                            params("expiryDate", issue_date_tv.text.toString())
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                MobclickAgent.onEvent(this@IssueSecondActivity,"send_caigou_success")
                                EventBus.getDefault().post(RefreshMessageEvent(1, "发布信息"))
                                ActivityStack.getScreenManager().popActivities(this@IssueSecondActivity::class.java)
                            }

                        })
            }
        }
    }

    /**
     * 我的发布-编辑-详情
     */
    override fun getData() {
        OkGo.post<BaseResponse<PurchaseData>>(BaseHttp.purchasing_details)
                .tag(this@IssueSecondActivity)
                .headers("token", getString("token"))
                .params("purchasingId", intent.getStringExtra("purchasingId"))
                .execute(object : JacksonDialogCallback<BaseResponse<PurchaseData>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<PurchaseData>>) {
                        val data = response.body().`object`
                        et_buy.setText(data.purchasingDescribe)
                        et_count.setText(data.purchasingNum + "")
                        issue_date_tv.text = data.expiryDate
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("发布采购信息")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("发布采购信息")
    }
}
