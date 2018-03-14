package com.ruanmeng.tiger_treasure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.AnimationHelper
import com.ruanmeng.utils.PopupWindowUtils
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_enterprise_list.*
import kotlinx.android.synthetic.main.header_member.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_blue.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class EnterpriseListActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_qu = ArrayList<CityData>()
    private val list_way = ArrayList<CommonData>()
    private var list_Screen = ArrayList<ScreenData>()

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

    private var pos_type = 0
    private var pos_time = 0
    private var id_first = ""
    private var id_second = ""
    private var aid_first = ""
    private var aid_second = ""
    private var scr_type = ""
    private var scr_time = ""
    private var scr_cap = ""
    private var scr_num = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_list)
        setToolbarVisibility(false)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum, false)
    }

    override fun init_title() {
        blue_nav_title.text = "企业展示"
        empty_hint.text = "暂无相关企业信息！"

        swipe_refresh.refresh { getData(1, false) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum, false)
            }
        }
        et_enkeyword.setOnEditorActionListener { v, actionId, _ ->
            /*判断是否是“SEARCH”键*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (et_enkeyword.text.toString().isBlank()) {
                    toast("请输入关键字")
                } else {
                    /*隐藏软键盘*/
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (imm.isActive) imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)

                    if (et_enkeyword.text.toString().trim() != "") {
                        pageNum = 1
                        getData(pageNum, true)
                    }
                }
            }

            return@setOnEditorActionListener false
        }
        mAdapter = SlimAdapter.create()
                .register<CompanyData>(R.layout.item_first_list) { data, injector ->
                    injector.invisible(R.id.item_first_type)
                            .text(R.id.item_first_company, data.compName)
                            .with<TextView>(R.id.item_first_company) { view ->
                                if (data.vipTypeId.equals("VIP_SLIVER")) {
                                    val drawable = getResources().getDrawable(R.mipmap.vip_center)
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                                    view.setCompoundDrawables(null, null, drawable, null)
                                } else if (data.vipTypeId.equals("VIP_GOLD")) {
                                    val drawable = getResources().getDrawable(R.mipmap.vip_most)
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                                    view.setCompoundDrawables(null, null, drawable, null)
                                } else
                                    view.setCompoundDrawables(null, null, null, null)
                            }
                            .text(R.id.item_first_length,
                                    if (data.distance.toDouble() < 1000) "${data.distance}m"
                                    else "${DecimalFormat(".#").format(data.distance.toDouble() / 1000)}km")
                            .text(R.id.item_first_title, data.compTypeName)
                            .text(R.id.item_first_addr, data.address)
                            .text(R.id.item_first_scan, "浏览 ${data.llNum}")
                            .text(R.id.item_first_watch, "收藏 ${data.gzNum}")

                            .with<ImageView>(R.id.item_first_logo) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.compLogo)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .with<ImageView>(R.id.item_first_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.copmImage)
                                        .placeholder(R.mipmap.default_product) // 等待时的图片
                                        .error(R.mipmap.default_product)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_first_11) {
                                val intent = Intent(baseContext, EnterpriseDetailActivity::class.java)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        //        header_search.setOnClickListener { startActivity(SearchActivity::class.java) }
    }

    override fun getData(pindex: Int, isshow: Boolean) {
        OkGo.post<BaseResponse<CompanyModel>>(BaseHttp.company_list)
                .tag(this@EnterpriseListActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("areaCode", if (aid_second.isEmpty()) aid_first else aid_second)
                .params("industryId", if (id_second.isEmpty()) id_first else id_second)
                .params("compType", scr_type)//类型
                .params("foundingTime", scr_time)//时间
                .params("staffNum", scr_num)//公司规模
                .params("regCap", scr_cap)//资本

                .params("isHot", intent.getStringExtra("isHot"))
                .execute(object : JacksonDialogCallback<BaseResponse<CompanyModel>>(baseContext, isshow) {

                    override fun onSuccess(response: Response<BaseResponse<CompanyModel>>) {
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

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.enterprise_qu -> {
                if (list_qu.isNotEmpty()) {
                    AnimationHelper.startRotateAnimator(enterprise_qu_iv, 0f, 180f)

                    PopupWindowUtils.showAreaPopWindow(
                            baseContext,
                            enterprise_filter_divider,
                            list_qu,
                            aid_first,
                            aid_second,
                            object : PopupWindowUtils.PopupWindowTypeCallBack {

                                override fun doWork(id_left: String, id_right: String, name: String) {
                                    enterprise_qu_tv.text = name
                                    aid_first = id_left
                                    aid_second = id_right

                                    updateList()
                                }

                                override fun onDismiss() = AnimationHelper.startRotateAnimator(member_job_iv, 180f, 0f)

                            })
                } else {
                    OkGo.post<BaseResponse<ArrayList<CityData>>>(BaseHttp.area_list)
                            .tag(this@EnterpriseListActivity)
                            .isMultipart(true)
                            .params("code", getString("location_city"))
                            .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityData>>>(baseContext, true) {

                                override fun onSuccess(response: Response<BaseResponse<ArrayList<CityData>>>) {
                                    list_qu.apply {
                                        clear()
                                        add(CityData().apply { areaName = "不限区域" })
                                        addItems(response.body().`object`)
                                    }

                                    AnimationHelper.startRotateAnimator(enterprise_qu_iv, 0f, 180f)

                                    PopupWindowUtils.showAreaPopWindow(
                                            baseContext,
                                            enterprise_filter_divider,
                                            list_qu,
                                            aid_first,
                                            aid_second,
                                            object : PopupWindowUtils.PopupWindowTypeCallBack {

                                                override fun doWork(id_left: String, id_right: String, name: String) {
                                                    enterprise_qu_tv.text = name
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
            R.id.enterprise_job -> {
                OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.industry_list)
                        .tag(this@EnterpriseListActivity)
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

                                AnimationHelper.startRotateAnimator(enterprise_job_iv, 0f, 180f)

                                PopupWindowUtils.showTypePopWindow(
                                        baseContext,
                                        enterprise_filter_divider,
                                        list_way,
                                        id_first,
                                        id_second,
                                        object : PopupWindowUtils.PopupWindowTypeCallBack {

                                            override fun doWork(id_left: String, id_right: String, name: String) {
                                                enterprise_job_tv.text = name
                                                id_first = id_left
                                                id_second = id_right

                                                updateList()
                                            }

                                            override fun onDismiss() = AnimationHelper.startRotateAnimator(enterprise_job_iv, 180f, 0f)

                                        })
                            }

                        })
            }
            R.id.enterprise_xuan -> {
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
            var gm1 = KeyValueData("SF_1", "50人以下")
            var gm2 = KeyValueData("SF_2", "50~100人")
            var gm3 = KeyValueData("SF_3", "100~500人")
            var gm4 = KeyValueData("SF_4", "500~1000人")
            var gm5 = KeyValueData("SF_5", "1000人以上")
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
                enterprise_filter_divider,
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
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum, false)
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业展示")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业展示")
    }
}
