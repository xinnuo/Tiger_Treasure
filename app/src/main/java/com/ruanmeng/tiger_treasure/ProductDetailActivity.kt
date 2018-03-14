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
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.adapter.LoopProductAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_product_detail.*
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONObject
import java.util.*

/**
 * 此类已弃用，共用了供货详情界面
 */
class ProductDetailActivity : BaseActivity() {

    private val list_manager = ArrayList<CommonData>()
    private var compTel = ""
    private var companyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        init_title("产品详情")

        getData()
    }

    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE

        product_collect.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                OkGo.post<String>(BaseHttp.collecon_sc_sub)
                        .tag(this@ProductDetailActivity)
                        .headers("token", getString("token"))
                        .params("targetId", intent.getStringExtra("supplyId"))
                        .params("targetType", "Product")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                product_collect.isChecked = !product_collect.isChecked
                            }

                        })
            }
            return@setOnTouchListener true
        }

        product_tel.setOnClickListener {
            if (compTel.isEmpty()) {
                toast("暂无企业联系方式")
                return@setOnClickListener
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

        product_company_ll.setOnClickListener {
            ActivityStack.getScreenManager().popActivities(this@ProductDetailActivity::class.java, ProductActivity::class.java)
        }
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
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
            R.id.product_contact -> {
                if (list_manager.size > 0) showSheetDialog()
                else {
                    OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.company_manager_list)
                            .tag(this@ProductDetailActivity)
                            .headers("token", getString("token"))
                            .params("companyId", companyId)
                            .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                                override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                                    list_manager.addItems(response.body().`object`)
                                    if (list_manager.size > 0) showSheetDialog()
                                }
                            })
                }
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
        OkGo.post<String>(BaseHttp.supply_details)
                .tag(this@ProductDetailActivity)
                .headers("token", getString("token"))
                .params("supplyId", intent.getStringExtra("supplyId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("object")

                        val mLoopAdapter = LoopProductAdapter(baseContext, product_banner)
                        product_banner.apply {
                            setAdapter(mLoopAdapter)
                            setOnItemClickListener { position ->
                                //轮播图点击事件
                            }
                        }

                        val imgs = obj.getString("supplyPics").split(",")
                        mLoopAdapter.setImgs(imgs)
                        if (imgs.size <= 1) {
                            product_banner.pause()
                            product_banner.setHintViewVisibility(false)
                        } else {
                            product_banner.resume()
                            product_banner.setHintViewVisibility(true)
                        }

                        product_title.text = obj.getString("supplyTitle")
                        product_price.text = "¥" + obj.getString("retailPrice")
                        product_company.text = obj.getString("compName")
                        product_desc.text = obj.getString("supplyDescribe")
                        product_num.text = obj.getString("supplyNum")
                        product_address.text = obj.getString("address")
                        product_pprice.text = "¥" + obj.getString("retailPrice")
                        product_lprice.text = "¥" + obj.getString("tradePrice")
                        product_data.text = obj.getString("expiryDate")
                        if (!obj.isNull("companyId")) companyId = obj.getString("companyId")
                        compTel = obj.getString("compTel")
                        if (obj.getString("collecon") == "1") product_collect.isChecked = true
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
                                            .placeholder(R.mipmap.default_product2) // 等待时的图片
                                            .error(R.mipmap.default_product2)       // 加载失败的图片
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
                    dialog.dismiss()
                }
            }
        }
        tv_cancel.setOnClickListener { dialog.dismiss() }
    }
}
