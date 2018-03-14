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
import com.ruanmeng.model.CooperateData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_issue_third.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class IssueThirdActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_third)
        init_title("发布合作信息")
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
                if (et_project.text.isBlank()) {
                    toast("请输入合作项目")
                    et_project.requestFocus()
                    return
                }

                if (et_condition.text.isBlank()) {
                    toast("请输入参与条件")
                    et_condition.requestFocus()
                    return
                }

//                if (issue_date_tv.text.isBlank()) {
//                    toast("请选择截止日期")
//                    return
//                }

                OkGo.post<String>(BaseHttp.cooperate_add)
                        .tag(this@IssueThirdActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            if (intent.getStringExtra("mine") != null) {
                                params("cooperateId", intent.getStringExtra("cooperateId"))//编辑的时候把id提交上去
                            }
                            params("cooperateTitle", et_project.text.trim().toString())
                            params("cooperateCondition", et_condition.text.trim().toString())
//                            params("expiryDate", issue_date_tv.text.toString())
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                MobclickAgent.onEvent(this@IssueThirdActivity, "send_cooperation_success")
                                EventBus.getDefault().post(RefreshMessageEvent(2, "发布信息"))
                                ActivityStack.getScreenManager().popActivities(this@IssueThirdActivity::class.java)
                            }

                        })
            }
        }
    }

    /**
     * 我的发布-编辑-详情
     */
    override fun getData() {
        OkGo.post<BaseResponse<CooperateData>>(BaseHttp.cooperate_details)
                .tag(this@IssueThirdActivity)
                .headers("token", getString("token"))
                .params("cooperateId", intent.getStringExtra("cooperateId"))
                .execute(object : JacksonDialogCallback<BaseResponse<CooperateData>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<CooperateData>>) {
                        val data = response.body().`object`
                        et_project.setText(data.cooperateTitle)
                        et_condition.setText(data.cooperateCondition)
                        issue_date_tv.text = data.expiryDate
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("发布合作信息")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("发布合作信息")
    }
}
