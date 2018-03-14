package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.MemberCardHandler
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.model.VipData
import com.ruanmeng.model.VipModel
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_member_mine.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MemberMineActivity : BaseActivity() {

    private val list = ArrayList<VipData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_mine)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@MemberMineActivity)

        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        GlideApp.with(baseContext)
                .load(BaseHttp.baseImg + getString("userhead"))
                .placeholder(R.mipmap.default_user)
                .error(R.mipmap.default_user)
                .dontAnimate()
                .into(member_img)


        member_help.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "帮助中心")
            startActivity(intent)
        }
//        if (getString("vipCode") == "VIP_SLIVER") {
//            member_vip.setBackgroundResource(R.mipmap.vip_center)
//        } else if (getString("vipCode") == "VIP_GOLD") {
//            member_vip.setBackgroundResource(R.mipmap.vip_most)
//        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<VipModel>>(BaseHttp.viptype_list)
                .tag(this@MemberMineActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<BaseResponse<VipModel>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<VipModel>>) {
                        list.clear()
                        list.addItems(response.body().`object`.rows)
                        if (list.isNotEmpty()) {
                            member_card.setCardTransformer(180f, 0.2f)
                            member_card.bind(supportFragmentManager, MemberCardHandler(), list)
                            member_hint.text = "Hi~${response.body().`object`.params.compName}"
                            member_tim.text=response.body().`object`.params.vipTip+"("+response.body().`object`.params.expiryDate+"到期)"
                        }
                    }

                })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().unregister(this@MemberMineActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "开通会员" -> {
                list[event.type].vipIsPay = "1"
                member_card.notifyUI(member_card.currentMode)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("会员选择")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("会员选择")
    }

}
