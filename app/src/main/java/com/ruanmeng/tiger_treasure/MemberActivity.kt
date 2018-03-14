package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.TabLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.AnimationHelper
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.PopupWindowUtils
import com.ruanmeng.utils.Tools
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_enterprise_list.*
import kotlinx.android.synthetic.main.header_member.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_search_hint.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 供货采购界面
 */
class MemberActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_slider = ArrayList<CommonData>()
    private val list_qu = ArrayList<CityData>()
    private val list_way = ArrayList<CommonData>()
    private var list_Screen = ArrayList<ScreenData>()
    private var scr_type = ""
    private var scr_time = ""
    private var scr_cap = ""
    private var scr_num = ""
    private var list_send = ArrayList<String>()

    private val list_type = listOf(
            CommonData().apply { title = "不限" },
            CommonData().apply {
                title = "国有"
                content = "CT_01"
            },
            CommonData().apply {
                title = "民营"
                content = "CT_02"
            },
            CommonData().apply {
                title = "合资"
                content = "CT_03"
            },
            CommonData().apply {
                title = "外资"
                content = "CT_04"
            },
            CommonData().apply {
                title = "上市"
                content = "CT_05"
            })

    private val list_time = listOf(
            CommonData().apply { title = "不限" },
            CommonData().apply {
                title = "小于1年"
                content = "0-1"
            },
            CommonData().apply {
                title = "1~5年"
                content = "1-5"
            },
            CommonData().apply {
                title = "5~10年"
                content = "5-10"
            },
            CommonData().apply {
                title = "10年以上"
                content = "10-100"
            })

    private var id_first = ""
    private var id_second = ""
    private var aid_first = ""
    private var aid_second = ""

    private var url_http = BaseHttp.supply_list
    private val list_manager = ArrayList<CommonData>()
    private lateinit var banner: RollPagerView
    private lateinit var mLoopAdapter: LoopAdapter
    private lateinit var tab: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        setToolbarVisibility(false)
        init_title()
        EventBus.getDefault().register(this@MemberActivity)
        search_nav_right.visibility = View.INVISIBLE
        getData()
        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @SuppressLint("InflateParams")
    override fun init_title() {

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }



        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(baseContext).inflate(R.layout.header_member, null)
                    banner = view.findViewById(R.id.member_banner)
                    tab = view.findViewById(R.id.member_tab)

                    mLoopAdapter = LoopAdapter(baseContext, banner)
                    banner.apply {
                        setAdapter(mLoopAdapter)
                        setOnItemClickListener { position ->
                            //轮播图点击事件
                            if (list_slider.get(position).jumpType.equals("2")) {//跳转web
                                val intent = Intent(this@MemberActivity, WebActivity::class.java)
                                intent.putExtra("title", list_slider.get(position).sliderTitle)
                                intent.putExtra("url", list_slider[position].content)
                                startActivity(intent)
                            } else if (list_slider.get(position).jumpType.equals("3")) {//文本解析

                            }
                        }
                    }

                    tab.apply {
                        addTab(this.newTab().setText("供货"))
                        addTab(this.newTab().setText("采购"))
                        addTab(this.newTab().setText("合作"))

                        post { Tools.setIndicator(this, 40, 40) }

                        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                            override fun onTabReselected(tab: TabLayout.Tab) {}
                            override fun onTabUnselected(tab: TabLayout.Tab) {}

                            override fun onTabSelected(tab: TabLayout.Tab) {
                                OkGo.getInstance().cancelTag(this@MemberActivity)

                                when (tab.position) {
                                    0 -> url_http = BaseHttp.supply_list
                                    1 -> url_http = BaseHttp.purchasing_list
                                    2 -> url_http = BaseHttp.cooperate_list
                                }

                                updateList()
                            }

                        })
                    }

                    addHeader(view)

                    register<ProductData>(R.layout.item_member_list) { data, injector ->
                        injector.text(R.id.item_member_title, data.supplyTitle)
                        injector.text(R.id.item_member_company, data.compName)
                                .text(R.id.item_member_miao, data.supplyDescribe)
                                .text(R.id.item_member_count, data.supplyNum)
                                .text(R.id.item_member_address, data.address)
                                .text(R.id.item_member_pi, "¥${data.tradePrice}")
                                .text(R.id.item_member_ling, "¥${data.retailPrice}")
                                .text(R.id.item_member_date, data.expiryDate)

                                .with<ImageView>(R.id.item_member_logo) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.compLogo)
                                            .placeholder(R.mipmap.default_logo) //等待时的图片
                                            .error(R.mipmap.default_logo)       //加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .with<ImageView>(R.id.item_member_img) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.supplyPic)
                                            .placeholder(R.mipmap.default_product2) //等待时的图片
                                            .error(R.mipmap.default_product2)       //加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .clicked(R.id.item_member_length) {
                                    getMasterList(data.companyId)
//                                    startActivity(MemberMessageActivity::class.java)
                                }

                                .clicked(R.id.item_member) {
                                    val intent = Intent(baseContext, MemberFirstActivity::class.java)
                                    intent.putExtra("supplyId", data.supplyId)
                                    intent.putExtra("fromWhere", "gonghuo")
                                    startActivity(intent)
                                }
                    }
                    register<PurchaseData>(R.layout.item_member_list_buy) { data, injector ->
                        injector.text(R.id.item_member_buy_company, data.compName)
                                .text(R.id.item_member_buy_msg, data.purchasingDescribe)
                                .text(R.id.item_member_buy_count, data.purchasingNum)
                                .text(R.id.item_member_buy_address, data.address)
                                .text(R.id.item_member_buy_date, data.expiryDate)

                                .with<ImageView>(R.id.item_member_buy_logo) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.compLogo)
                                            .placeholder(R.mipmap.default_logo) //等待时的图片
                                            .error(R.mipmap.default_logo)       //加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .clicked(R.id.item_member_buy_length) {
                                    //                                    startActivity(MemberMessageActivity::class.java)
                                    getMasterList(data.companyId)
                                }

                                .clicked(R.id.item_member_buy) {
                                    val intent = Intent(baseContext, MemberSecondActivity::class.java)
                                    intent.putExtra("purchasingId", data.purchasingId)
                                    startActivity(intent)
                                }
                    }
                    register<CooperateData>(R.layout.item_member_list_coo) { data, injector ->
                        injector.text(R.id.item_member_coo_company, data.compName)
                                .text(R.id.item_member_coo_project, data.cooperateTitle)
                                .text(R.id.item_member_coo_condition, data.cooperateCondition)
                                .text(R.id.item_member_coo_address, data.address)
                                .text(R.id.item_member_coo_date, data.expiryDate)

                                .with<ImageView>(R.id.item_member_coo_logo) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.compLogo)
                                            .placeholder(R.mipmap.default_logo) //等待时的图片
                                            .error(R.mipmap.default_logo)       //加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .clicked(R.id.item_member_coo_length) {
                                    //                                    startActivity(MemberMessageActivity::class.java)
                                    getMasterList(data.companyId)
                                }

                                .clicked(R.id.item_member_coo) {
                                    val intent = Intent(baseContext, MemberThirdActivity::class.java)
                                    intent.putExtra("cooperateId", data.cooperateId)
                                    startActivity(intent)
                                }
                    }

                    attachTo(recycle_list)
                }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.slider_list)
                .tag(this@MemberActivity)
                .params("sliderType", "3")
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                        list_slider.addItems(response.body().`object`.rows)

                        val imgs = ArrayList<String>()
                        list_slider.mapTo(imgs) { it.sliderImg }
                        mLoopAdapter.setImgs(imgs)
                        if (imgs.size < 2) {
                            banner.pause()
                            banner.setHintViewVisibility(false)
                        } else {
                            banner.resume()
                            banner.setHintViewVisibility(true)
                        }
                    }

                })
    }

    //联系企业
    fun getMasterList(id: String) {
        OkGo.post<BaseResponse<java.util.ArrayList<CommonData>>>(BaseHttp.company_manager_list)
                .tag(this@MemberActivity)
                .headers("token", getString("token"))
                .params("companyId", id)
                .execute(object : JsonDialogCallback<BaseResponse<java.util.ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<java.util.ArrayList<CommonData>>>) {
                        list_manager.clear()
                        list_manager.addItems(response.body().`object`)
                        if (list_manager.size > 0) showSheetDialog()
                    }
                })
    }

    override fun getData(pindex: Int) {
        when (url_http) {
            BaseHttp.supply_list -> OkGo.post<BaseResponse<ProductModel>>(url_http)
                    .tag(this@MemberActivity)
                    .headers("token", getString("token"))
                    .params("page", pindex)
                    .params("areaCode", if (aid_second.isEmpty()) aid_first else aid_second)
                    .params("industryId", if (id_second.isEmpty()) id_first else id_second)
                    .params("compType", scr_type)//类型
                    .params("foundingTime", scr_time)//时间
                    .params("staffNum", scr_num)//公司规模
                    .params("regCap", scr_cap)//资本

                    .execute(object : JsonDialogCallback<BaseResponse<ProductModel>>(baseContext) {

                        override fun onSuccess(response: Response<BaseResponse<ProductModel>>) {
                            list.apply {
                                if (pindex == 1) {
                                    clear()
                                    pageNum = pindex
                                }
                                addItems(response.body().`object`.rows)
                                if (count(response.body().`object`.rows) > 0) pageNum++
                                if (response.body().`object`.params!!.send == "1" && getString("isGov") != "1") {
                                    search_nav_right.visibility = View.VISIBLE
                                } else {
                                    search_nav_right.visibility = View.GONE
                                }
                            }

                            mAdapterEx.updateData(list).notifyDataSetChanged()
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false
                            isShowNull()
                        }

                    })
            BaseHttp.purchasing_list -> OkGo.post<BaseResponse<PurchaseModel>>(url_http)
                    .tag(this@MemberActivity)
                    .params("page", pindex)
                    .headers("token", getString("token"))
                    .params("areaCode", if (aid_second.isEmpty()) aid_first else aid_second)
                    .params("industryId", if (id_second.isEmpty()) id_first else id_second)
                    .params("compType", scr_type)//类型
                    .params("foundingTime", scr_time)//时间
                    .params("staffNum", scr_num)//公司规模
                    .params("regCap", scr_cap)//资本

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

                            mAdapterEx.updateData(list).notifyDataSetChanged()
                        }

                        override fun onFinish() {
                            super.onFinish()
                            swipe_refresh.isRefreshing = false
                            isLoadingMore = false
//                            isShowNull()
                        }

                    })
            BaseHttp.cooperate_list -> OkGo.post<BaseResponse<CooperateModel>>(url_http)
                    .tag(this@MemberActivity)
                    .params("page", pindex)
                    .headers("token", getString("token"))
                    .params("areaCode", if (aid_second.isEmpty()) aid_first else aid_second)
                    .params("industryId", if (id_second.isEmpty()) id_first else id_second)
                    .params("compType", scr_type)//类型
                    .params("foundingTime", scr_time)//时间
                    .params("staffNum", scr_num)//公司规模
                    .params("regCap", scr_cap)//资本

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

                            mAdapterEx.updateData(list).notifyDataSetChanged()
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

    /**
     * 空布局
     */
    fun isShowNull() {
        if (list.size == 0) {
            empty_view.visibility = View.VISIBLE
//            recycle_list.visibility= View.GONE
        } else {
            empty_view.visibility = View.GONE
//            recycle_list.visibility= View.VISIBLE
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.search_nav_hint -> {
                val intent = Intent(baseContext, SearchActivity::class.java)
                intent.putExtra("member", "1")
                startActivity(intent)
            }

            R.id.search_nav_right -> {
                if (list_send.isEmpty()) {
                    list_send.add("发布供货信息")
                    list_send.add("发布采购信息")
                    list_send.add("发布合作信息")
                }
                DialogHelper.showTypeBottomDialog(baseContext, "请选择发布类型", list_send) { position, _ ->
                    when (position) {
                        0 -> startActivity(IssueFirstActivity::class.java)
                        1 -> startActivity(IssueSecondActivity::class.java)
                        2 -> startActivity(IssueThirdActivity::class.java)
                    }
                }
            }
            R.id.member_qu -> {
                if (list_qu.isNotEmpty()) {
                    AnimationHelper.startRotateAnimator(member_qu_iv, 0f, 180f)

                    PopupWindowUtils.showAreaPopWindow(
                            baseContext,
                            member_divider,
                            list_qu,
                            aid_first,
                            aid_second,
                            object : PopupWindowUtils.PopupWindowTypeCallBack {

                                override fun doWork(id_left: String, id_right: String, name: String) {
                                    member_qu_tv.text = name
                                    aid_first = id_left
                                    aid_second = id_right

                                    updateList()
                                }

                                override fun onDismiss() = AnimationHelper.startRotateAnimator(member_job_iv, 180f, 0f)

                            })
                } else {
                    OkGo.post<BaseResponse<ArrayList<CityData>>>(BaseHttp.area_list)
                            .tag(this@MemberActivity)
                            .isMultipart(true)
                            .params("code", getString("location_city"))
                            .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityData>>>(baseContext, true) {

                                override fun onSuccess(response: Response<BaseResponse<ArrayList<CityData>>>) {
                                    list_qu.apply {
                                        clear()
                                        add(CityData().apply { areaName = "不限区域" })
                                        addItems(response.body().`object`)
                                    }

                                    AnimationHelper.startRotateAnimator(member_qu_iv, 0f, 180f)

                                    PopupWindowUtils.showAreaPopWindow(
                                            baseContext,
                                            member_divider,
                                            list_qu,
                                            aid_first,
                                            aid_second,
                                            object : PopupWindowUtils.PopupWindowTypeCallBack {

                                                override fun doWork(id_left: String, id_right: String, name: String) {
                                                    member_qu_tv.text = name
                                                    aid_first = id_left
                                                    aid_second = id_right

                                                    updateList()
                                                }

                                                override fun onDismiss() = AnimationHelper.startRotateAnimator(member_job_iv, 180f, 0f)

                                            })
                                }

                            })
                }
            }
            R.id.member_job -> {
                OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.industry_list)
                        .tag(this@MemberActivity)
                        .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                            override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                                list_way.apply {
                                    clear()
                                    add(CommonData().apply {
                                        position = 0
                                        industryName = "不限行业"
                                    })
                                    addItems(response.body().`object`)
                                }

                                AnimationHelper.startRotateAnimator(member_job_iv, 0f, 180f)

                                PopupWindowUtils.showTypePopWindow(
                                        baseContext,
                                        member_divider,
                                        list_way,
                                        id_first,
                                        id_second,
                                        object : PopupWindowUtils.PopupWindowTypeCallBack {

                                            override fun doWork(id_left: String, id_right: String, name: String) {
                                                member_job_tv.text = name
                                                id_first = id_left
                                                id_second = id_right

                                                updateList()
                                            }

                                            override fun onDismiss() = AnimationHelper.startRotateAnimator(member_job_iv, 180f, 0f)

                                        })
                            }

                        })
            }
            R.id.member_xuan -> {
                showScreenPop()
            }
        }
    }

    fun showScreenPop() {
        if (list_Screen.size == 0) {
            var data = ScreenData()
            data.name = "注册资本"
            var str_regCap = ArrayList<KeyValueData>()
            var cap1 = KeyValueData("0_10", "10万元以下")
            var cap2 = KeyValueData("10_50", "10~50万元")
            var cap3 = KeyValueData("50_100", "50~100万元")
            var cap4 = KeyValueData("100_500", "100~500万元")
            var cap5 = KeyValueData("500_1000", "500~1000万元")
            var cap6 = KeyValueData("1000", "1000万元以上")
            str_regCap.add(cap1)
            str_regCap.add(cap2)
            str_regCap.add(cap3)
            str_regCap.add(cap4)
            str_regCap.add(cap5)
            str_regCap.add(cap6)
            data.value = str_regCap
            list_Screen.add(data)

            data = ScreenData()
            data.name = "成立时间"
            var str_time = ArrayList<KeyValueData>()
            var time1 = KeyValueData("0_1", "1年以下")
            var time2 = KeyValueData("1_5", "1~5年")
            var time3 = KeyValueData("5_10", "5~10年")
            var time4 = KeyValueData("10_15", "10~15年")
            var time5 = KeyValueData("15", "15年以上")
            str_time.add(time1)
            str_time.add(time2)
            str_time.add(time3)
            str_time.add(time4)
            str_time.add(time5)
            data.value = str_time
            list_Screen.add(data)
            /**
            CT1	有限责任公司
            CT2	股份有限公司
            CT3	私营企业
            CT4	国有、集体、联营、股份合作企业
            CT5	港、澳、台投资企业
            CT6	外商企业
            CT7	其他企业
             */
            data = ScreenData()
            data.name = "企业类型"
            var str_data = ArrayList<KeyValueData>()
            var CT1 = KeyValueData("CT_1", "有限责任公司")
            var CT2 = KeyValueData("CT_2", "股份有限公司")
            var CT3 = KeyValueData("CT_3", "私营企业")
            var CT4 = KeyValueData("CT_4", "国有、集体、联营、股份合作企业")
            var CT5 = KeyValueData("CT_5", "港、澳、台投资企业")
            var CT6 = KeyValueData("CT_6", "外商投资企业")
            var CT7 = KeyValueData("CT_7", "其他企业")
            str_data.add(CT1)
            str_data.add(CT2)
            str_data.add(CT3)
            str_data.add(CT4)
            str_data.add(CT5)
            str_data.add(CT6)
            str_data.add(CT7)
            data.value = str_data
            list_Screen.add(data)

            data = ScreenData()
            data.name = "企业规模"
            var str_gm = ArrayList<KeyValueData>()
            var gm1 = KeyValueData("0_50", "50人以下")
            var gm2 = KeyValueData("50_100", "50~100人")
            var gm3 = KeyValueData("100_500", "100~500人")
            var gm4 = KeyValueData("500_1000", "500~1000人")
            var gm5 = KeyValueData("1000", "1000人以上")
            str_gm.add(gm1)
            str_gm.add(gm2)
            str_gm.add(gm3)
            str_gm.add(gm4)
            str_gm.add(gm5)

            data.value = str_gm
            list_Screen.add(data)
        }
        AnimationHelper.startRotateAnimator(enterprise_xuan_iv, 0f, 180f)

        PopupWindowUtils.showFilterPopWindow(
                baseContext,
                member_divider,
                list_Screen,
                object : PopupWindowUtils.PopupWindowFilterCallBack {
                    override fun reset() {
                        scr_cap = ""
                        scr_time = ""
                        scr_type = ""
                        scr_num = ""
                    }

                    override fun doWork(map: MutableMap<String, KeyValueData>?) {
                        if (map != null) {
                            for ((key, value) in map) {
                                for (item in list_Screen) {
                                    if (item.name.equals(key)) {
                                        item.selected = value.name
                                    }
                                }
                                when (key) {
                                    "注册资本" -> scr_cap = value.id
                                    "企业类型" -> scr_type = value.id
                                    "企业规模" -> scr_num = value.id
                                    "成立时间" -> scr_time = value.id
                                }
                            }
                        }
                        updateList()
                    }


                    override fun onDismiss() = AnimationHelper.startRotateAnimator(enterprise_xuan_iv, 180f, 0f)

                })
    }


    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapterEx.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
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

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().unregister(this@MemberActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "发布信息" -> {
                if (tab.selectedTabPosition == event.type) updateList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("供货采购合作列表")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("供货采购合作列表")
    }
}
