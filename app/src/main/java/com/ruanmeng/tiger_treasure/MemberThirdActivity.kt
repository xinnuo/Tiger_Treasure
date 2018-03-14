package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CooperateData
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_member_third.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class MemberThirdActivity : BaseActivity() {
    private val list_manager = ArrayList<CommonData>()
    private var companyId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_third)
        init_title("合作详情", "联系企业")

        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        val drawable = resources.getDrawable(R.mipmap.icon_guanjia)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(drawable, null, null, null)
        tvRight.setTextColor(resources.getColor(R.color.colorAccent))

        tvRight.setOnClickListener {
            getMasterList(companyId)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.li_third_company -> {
                val intent = Intent(this@MemberThirdActivity, EnterpriseDetailActivity::class.java)
                intent.putExtra("companyId", companyId)
                startActivity(intent)
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CooperateData>>(BaseHttp.cooperate_details)
                .tag(this@MemberThirdActivity)
                .headers("token", getString("token"))
                .params("cooperateId", intent.getStringExtra("cooperateId"))
                .execute(object : JacksonDialogCallback<BaseResponse<CooperateData>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<CooperateData>>) {

                        val data = response.body().`object`
                        companyId = data.companyId
                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.compLogo)
                                .placeholder(R.mipmap.default_logo) //等待时的图片
                                .error(R.mipmap.default_logo)       //加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(member_logo)

                        member_company.text = data.compName
                        member_he.text = data.cooperateTitle
                        member_condition.text = data.cooperateCondition
                        member_address.text = data.address
                        member_date.text = data.expiryDate
                    }

                })
    }

    //联系企业
    fun getMasterList(id: String) {
        MobclickAgent.onEvent(this, "hz_det_contact_click")
        OkGo.post<BaseResponse<java.util.ArrayList<CommonData>>>(BaseHttp.company_manager_list)
                .tag(this@MemberThirdActivity)
                .headers("token", getString("token"))
                .params("companyId", id)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<java.util.ArrayList<CommonData>>>) {
                        list_manager.clear()
                        list_manager.addItems(response.body().`object`)
                        if (list_manager.size > 0) showSheetDialog()
                    }
                })
    }

    @SuppressLint("InflateParams")
    private fun showSheetDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_manager_contact, null) as View
        val tv_ok = view.findViewById(R.id.dialog_manager_ok) as TextView
        val tv_cancel = view.findViewById(R.id.dialog_manager_cancle) as TextView
        val recyclerView = view.findViewById(R.id.dialog_manager_list) as RecyclerView
        val dialog = BottomSheetDialog(baseContext)
        dialog.setContentView(view)
        dialog.show()

        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_city_listnew, { data, injector ->
                        injector.text(R.id.item_citynew_name, data.userName)
                        injector.text(R.id.item_citynew_job, data.positionName)
                                .visible(R.id.enterprise_img)
                                .with<RoundedImageView>(R.id.enterprise_img, { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.userhead)
                                            .placeholder(R.mipmap.default_user) // 等待时的图片
                                            .error(R.mipmap.default_user)       // 加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                })
                                .with<TextView>(R.id.item_citynew_name, { view ->
                                    view.gravity = Gravity.CENTER
                                    @Suppress("DEPRECATION")
                                    view.setTextColor(resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black_dark))
                                })
                                .clicked(R.id.li_citynew_name, {
                                    //融云刷新用户信息
                                    RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                            data.managerId,
                                            data.userName,
                                            Uri.parse(BaseHttp.baseImg + data.userhead)))
                                    //融云单聊
                                    RongIM.getInstance().startPrivateChat(baseContext, data.managerId, data.userName)
                                    dialog.dismiss()
                                })
                    })
                    .attachTo(recyclerView)
        }

        (recyclerView.adapter as SlimAdapter).updateData(list_manager).notifyDataSetChanged()


        tv_ok.setOnClickListener {
            dialog.dismiss()
            for (item in list_manager) {
                if (item.isChecked) {
                    //融云刷新用户信息
                    RongIM.getInstance().refreshUserInfoCache(UserInfo(
                            item.managerId,
                            item.userName,
                            Uri.parse(BaseHttp.baseImg + item.userhead)))
                    //融云单聊
                    RongIM.getInstance().startPrivateChat(baseContext, item.managerId, item.userName)
                }
            }
        }
        tv_cancel.setOnClickListener { dialog.dismiss() }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("合作详情")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("合作详情")
    }
}
