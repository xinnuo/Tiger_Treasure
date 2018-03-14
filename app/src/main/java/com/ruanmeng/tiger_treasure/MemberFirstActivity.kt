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
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.adapter.LoopProductAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.ProductData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyLinearLayoutManager
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_member_first.*
import net.idik.lib.slimadapter.SlimAdapter

class MemberFirstActivity : BaseActivity() {
    private val list_manager = ArrayList<CommonData>()
    private var companyId: String = ""
    private var compTel = ""
    private var paramsList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_first)
        if ("gonghuo".equals(intent.getStringExtra("fromWhere")))
            init_title("产品详情", "") else init_title("产品详情", "")
        getData()
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE
        product_fcollect.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                OkGo.post<String>(BaseHttp.collecon_sc_sub)
                        .tag(this@MemberFirstActivity)
                        .headers("token", getString("token"))
                        .params("targetId", intent.getStringExtra("supplyId"))
                        .params("targetType", "Product")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                product_fcollect.isChecked = !product_fcollect.isChecked
                                if (product_fcollect.isChecked) {
                                    product_fcollect.text = "已收藏"
                                } else {
                                    product_fcollect.text = "收藏"
                                }
                            }

                        })
            }
            return@setOnTouchListener true
        }

        recycle_param.apply {
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<String>(R.layout.item_params) { data, injector ->
                        injector.text(R.id.param_name, data.split(":")[0])
                                .text(R.id.param_content, data.split(":")[1])

                    }
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.product_ftel -> {
                if (compTel.isEmpty()) {
                    toast("暂无企业联系方式")
                    return
                }
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
            R.id.product_company_ll -> {
                val intent = Intent(this@MemberFirstActivity, EnterpriseDetailActivity::class.java)
                intent.putExtra("companyId", companyId)
                startActivity(intent)
            }
            R.id.text_param -> {
//                if (recycle_param.visibility == View.GONE)
//                    recycle_param.visibility = View.VISIBLE
//                else
//                    recycle_param.visibility = View.GONE
            }
            R.id.product_contact -> {
                getMasterList(companyId)
            }
        }


    }

    fun Share(sharetype: SHARE_MEDIA, dialog: Dialog) {
        ShareAction(baseContext)
                .setPlatform(sharetype)
                .withText("虎宝企业联盟")
                .withMedia(UMWeb(BaseHttp.baseUrl + "/api/supply_share.rm?supplyId=" + intent.getStringExtra("supplyId"), "欢迎使用虎宝企业联盟", product_title.text.toString(), UMImage(baseContext, R.mipmap.ic_launcher_round)))
                .share()
        dialog.dismiss()
    }

    override fun getData() {
        OkGo.post<BaseResponse<ProductData>>(BaseHttp.supply_details)
                .tag(this@MemberFirstActivity)
                .headers("token", getString("token"))
                .params("supplyId", intent.getStringExtra("supplyId"))
                .execute(object : JacksonDialogCallback<BaseResponse<ProductData>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<ProductData>>) {

                        val data = response.body().`object`
                        companyId = data.companyId
                        val mLoopAdapter = LoopProductAdapter(baseContext, member_img)
                        member_img.apply {
                            setAdapter(mLoopAdapter)
                            setOnItemClickListener { position ->
                                //轮播图点击事件
                            }
                        }

                        val imgs = data.supplyPics.split(",")
                        mLoopAdapter.setImgs(imgs)
                        if (imgs.size <= 1) {
                            member_img.pause()
                            member_img.setHintViewVisibility(false)
                        } else {
                            member_img.resume()
                            member_img.setHintViewVisibility(true)
                        }
                        product_title.text = data.supplyTitle
                        product_price.text = "¥${data.retailPrice}"
                        product_company.text = data.compName
                        product_num.text = data.supplyNum
                        product_company.text = data.compName
                        product_address.text = data.address
//                        product_pprice.text = "¥${data.tradePrice}" + "元"
                        product_lprice.text = "¥${data.retailPrice}"
//                        product_data.text = data.expiryDate
                        product_fdesc.text = data.supplyDescribe
                        if (data.collecon == "1") product_fcollect.isChecked = true
                        compTel = data.compTel
                        if (product_fcollect.isChecked) {
                            product_fcollect.text = "已收藏"
                        } else {
                            product_fcollect.text = "收藏"
                        }
                        //参数
                        if ("" != data.spection1)
                            paramsList.add(data.spection1)
                        if ("" != data.spection2)
                            paramsList.add(data.spection2)
                        if ("" != data.spection3)
                            paramsList.add(data.spection3)
                        if ("" != data.spection4)
                            paramsList.add(data.spection4)
                        if ("" != data.spection5)
                            paramsList.add(data.spection5)
                        (recycle_param.adapter as SlimAdapter).updateData(paramsList).notifyDataSetChanged()
                    }

                })
    }

    //联系企业
    private fun getMasterList(id: String) {
        MobclickAgent.onEvent(this, "gh_det_contact_click")
        if (list_manager.size > 0) showSheetDialog()
        else {
            OkGo.post<BaseResponse<java.util.ArrayList<CommonData>>>(BaseHttp.company_manager_list)
                    .tag(this@MemberFirstActivity)
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
        MobclickAgent.onPageStart("产品详情")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("产品详情")
    }
}
