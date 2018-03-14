package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.amap.api.AMapLocationHelper
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.HotCityGridAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.putString
import com.ruanmeng.model.CityData
import com.ruanmeng.model.CityMessageEvent
import com.ruanmeng.model.CityModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.sort.PinyinComparator
import com.ruanmeng.utils.DensityUtil
import com.ruanmeng.view.CustomGridView
import kotlinx.android.synthetic.main.activity_city_list.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import qdx.stickyheaderdecoration.NormalDecoration
import java.util.*

class CityListActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_hot = ArrayList<CityData>()
    private val list_index = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)
        setToolbarVisibility(false)
        init_title()
        getData()
    }

    override fun init_title() {
        left_nav_title.text = "选择城市"
        city_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
            val decoration = object : NormalDecoration() {
                override fun getHeaderName(pos: Int): String = when (pos) {
                    0 -> "定位"
                    1 -> "热门"
                    else -> (list[pos] as CityData).firstLetter
                }
            }
            @Suppress("DEPRECATION")
            decoration.setHeaderContentColor(resources.getColor(R.color.divider))
            decoration.setHeaderHeight(DensityUtil.dp2px(30f))
            decoration.setTextSize(DensityUtil.sp2px(14f))
            @Suppress("DEPRECATION")
            decoration.setTextColor(resources.getColor(R.color.gray))
            addItemDecoration(decoration)
        }

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.item_city_header) { data, injector ->
                    injector.text(R.id.located_city, data)
                            .clicked(R.id.layout_locate, {
                                when (data) {
                                    "正在定位..." -> return@clicked
                                    "定位失败" -> startLocation()
                                    else -> itemSelect(list[0].toString())
                                }
                            })
                }
                .register<Int>(R.layout.item_hot_grid) { _, injector ->
                    injector.with<CustomGridView>(R.id.hot_grid, { view ->
                        view.adapter = HotCityGridAdapter(baseContext, list_hot)
                        view.setOnItemClickListener { _, _, position, _ ->

                            itemSelect(list_hot[position].areaName)
                        }
                    })
                }
                .register<CityData>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .visibility(
                                    R.id.item_city_divider,
                                    if (list.indexOf(data) != list.size - 1
                                            && data.firstLetter != (list[list.indexOf(data) + 1] as CityData).firstLetter)
                                        View.GONE
                                    else View.VISIBLE)
                            .clicked(R.id.item_city_name, {

                                itemSelect(data.areaName)
                            })
                }
                .attachTo(city_list)
    }

    private fun itemSelect(areaName: String) {
        if (intent.getBooleanExtra("isArea", false)) {
            intent.setClass(baseContext, CityActivity::class.java)
            intent.putExtra("areaName", areaName)
            startActivity(intent)
        } else {
            putString("location_city", areaName)
            EventBus.getDefault().post(CityMessageEvent(areaName))
            onBackPressed()
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<CityModel>>(BaseHttp.city_list)
                .tag(this@CityListActivity)
                .execute(object : JacksonDialogCallback<BaseResponse<CityModel>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<CityModel>>) {
                        seperateLists(response.body().`object`.citys)
                        list_hot.addItems(response.body().`object`.hots)

                        for (item in list) {
                            when (item) {
                                is String -> list_index.add("定位")
                                is Int -> list_index.add("热门")
                                is CityData -> {
                                    if (!list_index.contains(item.firstLetter))
                                        list_index.add(item.firstLetter)
                                }
                            }
                        }
                        index_layout.setIndexBarHeightRatio(0.9f)
                        index_layout.indexBar.setIndexsList(list_index)
                        index_layout.indexBar.setIndexChangeListener { name ->

                            if (list_index.indexOf(name) == 0 || list_index.indexOf(name) == 1) {
                                linearLayoutManager.scrollToPositionWithOffset(list_index.indexOf(name), 0)
                                return@setIndexChangeListener
                            }

                            for (i in 2 until list.size) {
                                if (name == (list[i] as CityData).firstLetter) {
                                    linearLayoutManager.scrollToPositionWithOffset(i, 0)
                                    return@setIndexChangeListener
                                }
                            }
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                        startLocation()
                    }

                })
    }

    private fun seperateLists(mlist: List<CityData>?) {
        if (mlist != null && mlist.isNotEmpty()) Collections.sort(mlist, PinyinComparator())
        list.apply {
            add("正在定位...")
            add(1)
            if (mlist != null) addAll(mlist)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startLocation() {
        AMapLocationHelper.getInstance(baseContext).clearCodes()
        AMapLocationHelper.getInstance(baseContext).startLocation(2) { location, isSuccessed, codes ->
            if (isSuccessed && 2 in codes) {
                list[0] = location.city.replace("市", "")
                left_nav_title.text = "当前城市：" + list[0]
                mAdapter.updateData(list).notifyDataSetChanged()
            } else {
                list[0] = "定位失败"
                mAdapter.updateData(list).notifyDataSetChanged()
            }
        }
    }
}
