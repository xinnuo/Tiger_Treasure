package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.Tools
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_product_mine.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProductMineActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var url_http = BaseHttp.supply_list
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_mine)
        init_title("我的发布")
        //编辑后返回刷新广播
        EventBus.getDefault().register(this@ProductMineActivity)
    }


    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无发布信息！"

        if (getString("isGov") == "1") {
            product_tab.visibility = View.GONE
            url_http = BaseHttp.message_list
            getData(1)
        } else {
            product_tab.apply {
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                    override fun onTabReselected(tab: TabLayout.Tab) {}
                    override fun onTabUnselected(tab: TabLayout.Tab) {}

                    override fun onTabSelected(tab: TabLayout.Tab) {
                        mPosition = tab.position + 1

                        window.decorView.postDelayed({
                            runOnUiThread {
                                OkGo.getInstance().cancelTag(this@ProductMineActivity)
                                when (tab.position) {
                                    0 -> url_http = BaseHttp.supply_list
                                    1 -> url_http = BaseHttp.purchasing_list
                                    2 -> url_http = BaseHttp.cooperate_list
                                }

                                updateList()
                            }
                        }, 300)
                    }

                })
                addTab(this.newTab().setText("供货"), true)
                addTab(this.newTab().setText("采购"), false)
                addTab(this.newTab().setText("合作"), false)
                post { Tools.setIndicator(this, 20, 20) }
            }
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .apply {
                    register<ProductData>(R.layout.item_product_mine) { data, injector ->
                        injector.text(R.id.item_product_name, data.supplyTitle)
                                .visible(R.id.item_product_desc)
                                .visible(R.id.item_product_img)
                                .text(R.id.item_product_desc, "产品描述：" + data.supplyDescribe)
                                .text(R.id.item_product_price, "¥" + data.retailPrice)
                                .with<RoundedImageView>(R.id.item_product_img) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.supplyPic)
                                            .placeholder(R.mipmap.default_banner) // 等待时的图片
                                            .error(R.mipmap.default_banner)       // 加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .clicked(R.id.item_product_edit) {
                                    val intent = Intent(baseContext, IssueFirstActivity::class.java)
                                    intent.putExtra("mine", "1")
                                    intent.putExtra("supplyId", data.supplyId)
                                    startActivity(intent)
                                }

                                .clicked(R.id.item_product_del) {
                                    DialogHelper.showDialog(
                                            baseContext,
                                            "删除发布",
                                            "确认删除该条发布？",
                                            "取消",
                                            "确定") {
                                        OkGo.post<String>(BaseHttp.productdel)
                                                .tag(this@ProductMineActivity)
                                                .isMultipart(true)
                                                .headers("token", getString("token"))
                                                .params("supplyId", data.supplyId)
                                                .execute(object : StringDialogCallback(baseContext) {

                                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                        toast(msg)
                                                        list.remove(data)
                                                        isShowNull()
                                                        mAdapter.updateData(list).notifyDataSetChanged()
                                                    }
                                                })
                                    }
                                }
                    }
                    register<PurchaseData>(R.layout.item_product_mine) { data, injector ->
                        injector.text(R.id.item_product_name, data.purchasingDescribe)
                                .visible(R.id.item_product_desc)
                                .gone(R.id.item_product_img)
                                .gone(R.id.item_product_price)
                                .text(R.id.item_product_desc, "采购数量：" + data.purchasingNum)

                                .clicked(R.id.item_product_edit) {
                                    val intent = Intent(baseContext, IssueSecondActivity::class.java)
                                    intent.putExtra("mine", "1")
                                    intent.putExtra("purchasingId", data.purchasingId)
                                    startActivity(intent)
                                }

                                .clicked(R.id.item_product_del) {
                                    DialogHelper.showDialog(
                                            baseContext,
                                            "删除发布",
                                            "确认删除该条发布？",
                                            "取消",
                                            "确定") {
                                        OkGo.post<String>(BaseHttp.purchasing_del)
                                                .tag(this@ProductMineActivity)
                                                .isMultipart(true)
                                                .headers("token", getString("token"))
                                                .params("purchasingId", data.purchasingId)
                                                .execute(object : StringDialogCallback(baseContext) {

                                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                        toast(msg)
                                                        list.remove(data)
                                                        isShowNull()
                                                        mAdapter.updateData(list).notifyDataSetChanged()
                                                    }
                                                })
                                    }

                                }
                    }
                    register<CooperateData>(R.layout.item_product_mine) { data, injector ->
                        injector.text(R.id.item_product_name, data.cooperateTitle)
                                .gone(R.id.item_product_desc)
                                .gone(R.id.item_product_img)
                                .gone(R.id.item_product_price)
                                .text(R.id.item_product_desc, "截止日期：" + data.expiryDate)

                                .clicked(R.id.item_product_edit) {
                                    val intent = Intent(baseContext, IssueThirdActivity::class.java)
                                    intent.putExtra("mine", "1")
                                    intent.putExtra("cooperateId", data.cooperateId)
                                    startActivity(intent)
                                }

                                .clicked(R.id.item_product_del) {
                                    DialogHelper.showDialog(
                                            baseContext,
                                            "删除发布",
                                            "确认删除该条发布？",
                                            "取消",
                                            "确定") {
                                        OkGo.post<String>(BaseHttp.cooperate_del)
                                                .tag(this@ProductMineActivity)
                                                .isMultipart(true)
                                                .headers("token", getString("token"))
                                                .params("cooperateId", data.cooperateId)
                                                .execute(object : StringDialogCallback(baseContext) {

                                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                        toast(msg)
                                                        list.remove(data)
                                                        isShowNull()
                                                        mAdapter.updateData(list).notifyDataSetChanged()

                                                    }
                                                })

                                    }
                                }
                    }
                    register<CommonData>(R.layout.item_product_mine) { data, injector ->
                        //发布的公告
                        injector.text(R.id.item_product_name, data.title)
                                .gone(R.id.item_product_desc)
                                .gone(R.id.item_product_price)
                                .gone(R.id.split)
                                .with<RoundedImageView>(R.id.item_product_img) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.coverImg)
                                            .placeholder(R.mipmap.default_banner) // 等待时的图片
                                            .error(R.mipmap.default_banner)       // 加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }
                                .clicked(R.id.item_product_edit) {
                                    val intent = Intent(baseContext, ReleaseMessageActivity::class.java)
                                    intent.putExtra("messageInfoId", data.messageInfoId)
                                    startActivity(intent)
                                }
                                .clicked(R.id.item_product_del) {
                                    DialogHelper.showDialog(
                                            baseContext,
                                            "删除发布",
                                            "确认删除该条发布？",
                                            "取消",
                                            "确定") {
                                        OkGo.post<String>(BaseHttp.message_delete)
                                                .tag(this@ProductMineActivity)
                                                .isMultipart(true)
                                                .headers("token", getString("token"))
                                                .params("messageInfoId", data.messageInfoId)
                                                .execute(object : StringDialogCallback(baseContext) {

                                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                        toast(msg)
                                                        list.remove(data)
                                                        isShowNull()
                                                        mAdapter.updateData(list).notifyDataSetChanged()

                                                    }
                                                })

                                    }
                                }

                    }


                }
                .attachTo(recycle_list)
    }

    /**
     * 空布局
     */
    fun isShowNull() {
        if (list.size == 0) {
            empty_view.visibility = View.VISIBLE
            recycle_list.visibility = View.GONE
        } else {
            empty_view.visibility = View.GONE
            recycle_list.visibility = View.VISIBLE
        }
    }

    override fun getData(pindex: Int) {
        when (url_http) {
            BaseHttp.supply_list -> OkGo.post<BaseResponse<ProductModel>>(url_http)
                    .tag(this@ProductMineActivity)
                    .headers("token", getString("token"))
                    .params("page", pindex)
                    .params("mine", "1")
                    .execute(object : JacksonDialogCallback<BaseResponse<ProductModel>>(baseContext) {

                        override fun onSuccess(response: Response<BaseResponse<ProductModel>>) {
                            list.apply {
                                if (pindex == 1) {
                                    clear()
                                    pageNum = pindex
                                }
                                addItems(response.body().`object`.rows)
                                if (count(response.body().`object`.rows) > 0) pageNum++
                            }

                            mAdapter.updateData(list).notifyDataSetChanged()
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false
                            isShowNull()
                        }

                    })
            BaseHttp.purchasing_list -> OkGo.post<BaseResponse<PurchaseModel>>(url_http)
                    .tag(this@ProductMineActivity)
                    .params("page", pindex)
                    .headers("token", getString("token"))
                    .params("mine", 1)
                    .execute(object : JsonDialogCallback<BaseResponse<PurchaseModel>>(baseContext) {

                        override fun onSuccess(response: Response<BaseResponse<PurchaseModel>>) {
                            list.apply {
                                if (pindex == 1) {
                                    clear()
                                    pageNum = pindex
                                }
                                addItems(response.body().`object`.rows)
                                if (count(response.body().`object`.rows) > 0) pageNum++
                            }

                            mAdapter.updateData(list).notifyDataSetChanged()
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false
                            isShowNull()
                        }

                    })
            BaseHttp.cooperate_list -> OkGo.post<BaseResponse<CooperateModel>>(url_http)
                    .tag(this@ProductMineActivity)
                    .params("page", pindex)
                    .headers("token", getString("token"))
                    .params("mine", 1)
                    .execute(object : JsonDialogCallback<BaseResponse<CooperateModel>>(baseContext) {

                        override fun onSuccess(response: Response<BaseResponse<CooperateModel>>) {
                            list.apply {
                                if (pindex == 1) {
                                    clear()
                                    pageNum = pindex
                                }
                                addItems(response.body().`object`.rows)
                                if (count(response.body().`object`.rows) > 0) pageNum++
                            }

                            mAdapter.updateData(list).notifyDataSetChanged()
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false
                            isShowNull()
                        }

                    })
            BaseHttp.message_list -> OkGo.post<BaseResponse<CommonModel>>(BaseHttp.message_list)
                    .tag(this)
                    .headers("token", getString("token"))
                    .params("page", pindex)
                    .params("mine", "1")
                    .params("messageType", 2)
                    .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                        override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {
                            list.apply {
                                if (pindex == 1) {
                                    clear()
                                    pageNum = pindex
                                }
                                addItems(response.body().`object`.rows)
                                if (count(response.body().`object`.rows) > 0) pageNum++
                            }
                            mAdapter.updateData(list).notifyDataSetChanged()
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false
                            isShowNull()
                        }
                    })
        }

    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().unregister(this@ProductMineActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "发布信息" -> updateList()
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("我的发布")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("我的发布")
    }

}
