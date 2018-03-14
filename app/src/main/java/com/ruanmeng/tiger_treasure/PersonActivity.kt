package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.PersonData
import com.ruanmeng.model.PersonMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_person.*
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat

class PersonActivity : BaseActivity() {

    private var mTargetId = ""
    private var userName = ""
    private var userhead = ""
    private var companyId = ""
    private var compTel = ""
    private var isGov = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
//        init_title("企业家详情")
        mTargetId = intent.getStringExtra("accountInfoId")
        isGov = intent.getBooleanExtra("isGov", false)
        if (isGov) {

        }

        if (mTargetId == getString("token")) {
            person_send.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
            person_send.isClickable = false
            person_watch.isClickable = false
        }
        getData()
    }


    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.person_send -> {
                if (companyId.isNotEmpty()) {
                    //融云刷新用户信息
                    RongIM.getInstance().refreshUserInfoCache(UserInfo(
                            mTargetId,
                            userName,
                            Uri.parse(BaseHttp.baseImg + userhead)))
                    //融云单聊
                    RongIM.getInstance().startPrivateChat(baseContext, mTargetId, userName)
                    MobclickAgent.onEvent(this, "boss_chat_click")
                }
            }
            R.id.person_watch -> {
                if (companyId.isNotEmpty()) {
                    OkGo.post<String>(BaseHttp.collecon_gz_sub)
                            .tag(this@PersonActivity)
                            .headers("token", getString("token"))
                            .params("targetType", "AccountInfo")
                            .params("targetId", mTargetId)
                            .execute(object : StringDialogCallback(baseContext) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                    toast(msg)
                                    EventBus.getDefault().post(PersonMessageEvent(5, ""))
                                    person_hint.text = if (person_hint.text == "关注") "已关注" else "关注"
                                    if (person_hint.text.equals("已关注")) {
                                        MobclickAgent.onEvent(this@PersonActivity, "boss_focus_success")
                                    }
                                }

                            })
                }
            }
            R.id.person_company -> {
                if (companyId.isNotEmpty()) {
                    val intent = Intent(baseContext, EnterpriseDetailActivity::class.java)
                    intent.putExtra("companyId", companyId)
                    startActivity(intent)
                }
            }
            R.id.person_tel -> {
                if (person_tel.text.toString().isNotEmpty() && person_tel.text.toString() != "未公开") {
                    DialogHelper.showDialog(
                            baseContext,
                            "企业电话",
                            "拨打企业电话 " + compTel,
                            "取消",
                            "呼叫") {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + compTel))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<PersonData>>(BaseHttp.other_accountinfo_details)
                .tag(this@PersonActivity)
                .headers("token", getString("token"))
                .params("accountInfoId", mTargetId)
                .execute(object : JacksonDialogCallback<BaseResponse<PersonData>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<PersonData>>) {
                        val data = response.body().`object`

                        companyId = data.companyId
                        compTel = data.telephone
                        userName = data.userName
                        userhead = data.userhead
                        if (data.accountType == "App_Company") {//company是企业家    manager是管家  App_Company
                            init_title("企业家详情")
                            person_watch.visibility = View.VISIBLE
                        } else if (data.accountType == "App_Manager") {//company是企业家    manager是管家  隐藏关注
                            init_title("管家详情")
                            person_watch.visibility = View.GONE
                        }

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + userhead)
                                .placeholder(R.mipmap.default_user)
                                .error(R.mipmap.default_user)
                                .dontAnimate()
                                .into(person_img)

                        person_name.text = userName
                        person_gender.setImageResource(
                                when (data.sex) {
                                    "0" -> R.mipmap.icon_sex_woman
                                    else -> R.mipmap.icon_sex_man
                                })
                        val length = if (data.distance.toDouble() < 1000) "${data.distance}m"
                        else "${DecimalFormat(".#").format(data.distance.toDouble() / 1000)}km"
                        person_distance.text = "距离\n$length"
                        person_hint.text = if (data.collecon == "1") "已关注" else "关注"

                        person_company.text = data.compName
                        person_job.text = data.positionName
                        person_tel.text = compTel
                        person_email.text = data.compEmail
                        person_web.text = data.compUrl
                        person_address.text = data.address
                        person_ye.text = data.copmProfile
                        person_yi.text = data.compService
                        person_sign.text = "兴趣爱好：" + data.hobby

                        if (isGov) {
                            init_title("政府号详情")
                            ll_email.visibility = View.GONE
                            ll_web.visibility = View.GONE
                            text_psn_address.text = "地        址："
                            text_psn_buss.text = "业务范围："
                        }
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业家详情")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业家详情")
    }
}
