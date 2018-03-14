package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.app.Dialog
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
import android.widget.LinearLayout
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CompanyData
import com.ruanmeng.model.ProductData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.Tools
import com.ruanmeng.view.FullyLinearLayoutManager
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_enterprise_detail.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class EnterpriseDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_manager = ArrayList<CommonData>()

    private var compLat = ""
    private var compLng = ""
    private var address = ""
    private var copmLic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_detail)
        init_title("企业详情")

        getData()
    }

    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE

        enterprise_list.apply {
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<ProductData>(R.layout.item_product_list) { data, injector ->
                        injector.text(R.id.item_product_title, data.supplyTitle)
                                .text(R.id.item_product_price, "¥" + data.retailPrice)

                                .visibility(R.id.item_product_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)

                                .with<RoundedImageView>(R.id.item_product_img, { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.supplyPic)
                                            .placeholder(R.mipmap.default_product2) // 等待时的图片
                                            .error(R.mipmap.default_product2)       // 加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                })

                                .clicked(R.id.item_product) {
                                    val intent = Intent(baseContext, MemberFirstActivity::class.java)
                                    intent.putExtra("supplyId", data.supplyId)
                                    startActivity(intent)
                                }
                    }
                    .attachTo(this@apply)
        }

        enterprise_collect.setOnClickListener {
            OkGo.post<String>(BaseHttp.collecon_sc_sub)
                    .tag(this@EnterpriseDetailActivity)
                    .headers("token", getString("token"))
                    .params("targetId", intent.getStringExtra("companyId"))
                    .params("targetType", "Company")
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            toast(msg)
                            enterprise_check.isChecked = !enterprise_check.isChecked
                            if (enterprise_check.isChecked) {
                                MobclickAgent.onEvent(this@EnterpriseDetailActivity, "com_det_coll_success")
                            }
                        }

                    })
        }

        enterprise_zhan.setOnClickListener {
            //            enterprise_expand.expand()
//            enterprise_detail2.visibility = View.GONE
            enterprise_detail2.setLines(enterprise_detail2.lineCount)
            enterprise_zhan.visibility = View.GONE
        }

        enterprise_look.setOnClickListener {
            if (copmLic.isNotEmpty()) {
                val img = arrayListOf(BaseHttp.baseImg + copmLic)

                MNImageBrowser.showImageBrowser(baseContext, enterprise_look, 0, img)
            }
        }
        li_phone.setOnClickListener {
            if (enterprise_tel.text.toString().isNotEmpty()) {
                DialogHelper.showDialog(
                        baseContext,
                        "企业电话",
                        "拨打企业电话 " + enterprise_tel.text.toString(),
                        "取消",
                        "呼叫") {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + enterprise_tel.text.toString()))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }

        }
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.enterprise_chan -> {
                intent.setClass(baseContext, ProductActivity::class.java)
                startActivity(intent)
            }
            R.id.enterprise_nav -> {
                if (compLat.isEmpty() || compLng.isEmpty()) return

                if (Tools.isAvilible(baseContext, "com.autonavi.minimap")) {
                    Tools.goToNaviActivity(
                            baseContext, resources.getString(R.string.app_name),
                            address, compLat, compLng, "0", "0")
                }
            }
            R.id.iv_nav_right -> {
                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_enterprise_share, null) as View
                val ll_wechat = view.findViewById(R.id.ll_dialog_share_wechat) as LinearLayout
                val ll_wechatf = view.findViewById(R.id.ll_dialog_share_wechatf) as LinearLayout
                val ll_qq = view.findViewById(R.id.ll_dialog_share_qq) as LinearLayout
                val ll_qqzone = view.findViewById(R.id.ll_dialog_share_qqzone) as LinearLayout
                val ll_sina = view.findViewById(R.id.ll_dialog_share_sina) as LinearLayout
                val tv_cancel = view.findViewById(R.id.btn_dialog_share_cancel) as TextView

                val dialog = BottomSheetDialog(baseContext)
                dialog.setContentView(view)
                dialog.show()

                ll_wechat.setOnClickListener {
                    Share(SHARE_MEDIA.WEIXIN, dialog)
                }
                ll_wechatf.setOnClickListener {
                    Share(SHARE_MEDIA.WEIXIN_CIRCLE, dialog)
                }
                ll_qq.setOnClickListener {
                    Share(SHARE_MEDIA.QQ, dialog)
                }
                ll_qqzone.setOnClickListener {
                    Share(SHARE_MEDIA.QZONE, dialog)
                }
                ll_sina.setOnClickListener {
                    Share(SHARE_MEDIA.SINA, dialog)
                }
                tv_cancel.setOnClickListener { dialog.dismiss() }
            }
            R.id.enterprise_contact -> {
                MobclickAgent.onEvent(this, "com_det_contact_click")
                if (list_manager.size > 0) showSheetDialog()
                else {
                    OkGo.post<BaseResponse<java.util.ArrayList<CommonData>>>(BaseHttp.company_manager_list)
                            .tag(this@EnterpriseDetailActivity)
                            .headers("token", getString("token"))
                            .params("companyId", intent.getStringExtra("companyId"))
                            .execute(object : JsonDialogCallback<BaseResponse<java.util.ArrayList<CommonData>>>(baseContext, true) {

                                override fun onSuccess(response: Response<BaseResponse<java.util.ArrayList<CommonData>>>) {

                                    list_manager.addItems(response.body().`object`)
                                    if (list_manager.size > 0) showSheetDialog()
                                }
                            })
                }
            }
        }
    }

    fun Share(sharetype: SHARE_MEDIA, dialog: Dialog) {
        var str = BaseHttp.baseUrl + "/api/comapny_share.rm?comapnyId=" + intent.getStringExtra("companyId")
        ShareAction(baseContext)
                .setPlatform(sharetype)
                .withText(enterprise_name.text.toString())
                .withMedia(UMWeb(str, "欢迎使用虎宝企业联盟", enterprise_name.text.toString(), UMImage(baseContext, R.mipmap.ic_launcher_round)))
                .share()
        dialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * attention to this below ,must add this*
         * 分享图片方式
         * imageurl       //网络图片
         * file           //本地文件
         * R.drawable.xxx //资源文件
         * bitmap         //bitmap文件
         * byte[]         //字节流
         */
        UMShareAPI.get(this@EnterpriseDetailActivity).onActivityResult(requestCode, resultCode, data)
        MobclickAgent.onEvent(this, "com_det_share_success")
    }

    override fun onDestroy() {
        super.onDestroy()
        UMShareAPI.get(this).release()
    }

    override fun getData() {
        OkGo.post<BaseResponse<CompanyData>>(BaseHttp.company_details)
                .tag(this@EnterpriseDetailActivity)
                .headers("token", getString("token"))
                .params("companyId", intent.getStringExtra("companyId"))
                .execute(object : JsonDialogCallback<BaseResponse<CompanyData>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CompanyData>>) {

                        val data = response.body().`object`
                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.copmImage)
                                .placeholder(R.mipmap.img_02) // 等待时的图片
                                .error(R.mipmap.img_02)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(enterprise_background)
                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.compLogo)
                                .placeholder(R.mipmap.default_logo) // 等待时的图片
                                .error(R.mipmap.default_logo)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(enterprise_img)

                        compLat = data.compLat
                        compLng = data.compLng
                        address = data.address
                        copmLic = data.copmLic
                        val length = if (data.distance.toDouble() < 1000) "${data.distance}m"
                        else "${DecimalFormat(".#").format(data.distance.toDouble() / 1000)}km"
                        company_distance.text = "距离\n$length"
                        enterprise_name.text = data.compName
                        if (data.vipTypeId.equals("VIP_SLIVER")) {
                            val drawable = getResources().getDrawable(R.mipmap.vip_center)
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                            enterprise_name.setCompoundDrawables(null, null, drawable, null)
                        } else if (data.vipTypeId.equals("VIP_GOLD")) {
                            val drawable = getResources().getDrawable(R.mipmap.vip_most)
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                            enterprise_name.setCompoundDrawables(null, null, drawable, null)
                        }
                        enterprise_web.text = data.compUrl
                        if (data.industryName.isNotBlank()) {
                            enterprise_type.text = data.industryName
                            enterprise_type.visibility = View.VISIBLE
                        } else {
                            enterprise_type.visibility = View.GONE
                        }
                        enterprise_tel.text = data.compTel
                        enterprise_email.text = data.compEmail
                        enterprise_address.text = data.address
                        if (data.compService.isEmpty()) {
                            tv_detail_range.text = "暂无"
                        } else {
                            tv_detail_range.text = data.compService
                        }
                        enterprise_register.text = "* 注册地址\n   " + data.regAddress
                        if (data.staffNumName.isEmpty()) {
                            enterprise_gui.text = "未填写"
                        } else {
                            enterprise_gui.text = data.staffNumName
                        }
                        if (data.registerDate.isEmpty()) {
                            enterprise_cheng.text = "未填写"
                        } else {
                            enterprise_cheng.text = data.registerDate
                        }
                        if (data.regCap.isEmpty()) {
                            enterprise_zhu.text = "未填写"
                        } else {
                            enterprise_zhu.text = data.regCap
                        }
                        enterprise_fa.text = "公司法人：${data.legalMan}"
                        enterprise_xing.text = "公司性质：${data.compTypeName}"

//                        enterprise_detail1.text = data.copmProfile
                        enterprise_detail2.text = data.copmProfile
                        enterprise_detail2.post(Runnable {
                            kotlin.run {
                                if (enterprise_detail2.lineCount > 3) {
                                    enterprise_detail2.maxLines = 3
                                    enterprise_zhan.visibility = View.VISIBLE
                                }
                            }
                        })

                        enterprise_scan.text = "浏览 ${data.llNum}"
                        if (data.collecon == "1") enterprise_check.isChecked = true

                        if (data.compService.isNotEmpty()) {
                            val items = data.compService.split(",")

                            enterprise_range.adapter = object : TagAdapter<String>(items) {
                                override fun getView(parent: FlowLayout, position: Int, item: String): View {

                                    val tv_name = LayoutInflater.from(baseContext).inflate(
                                            R.layout.item_enterprise_flow,
                                            enterprise_range,
                                            false) as TextView
                                    tv_name.text = item
                                    return tv_name
                                }
                            }
                        }

                        list.addItems(response.body().`object`.productList)
                        (enterprise_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()

                        if (response.body().`object`.isGov == "1") {
                            init_title("政府号详情")
                            ll_gov_gone.visibility = View.GONE
                            ll_com_desc.visibility = View.GONE
                            ll_com_pro.visibility = View.GONE
                        }
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
        MobclickAgent.onPageStart("企业详情")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业详情")
    }
}
