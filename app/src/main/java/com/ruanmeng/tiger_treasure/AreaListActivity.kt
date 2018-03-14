package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.model.CityNewData
import com.ruanmeng.model.CityNewEvent
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_city.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2017/12/16.
 */
class AreaListActivity : BaseActivity() {
    private val list = ArrayList<Any>()
    private var cityName = ""
    private var areaId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        init_title("选择区镇")
        getData("")
    }

    override fun init_title() {
//        super.init_title()
        city_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
        }
        mAdapter = SlimAdapter.create()
                .register<CityNewData>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .visibility(R.id.item_city_divider, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_city_divider1, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_city_name, {
                                areaId = data.areaId
                                putString("areaId", areaId)
                                putString("areaName", data.areaName)
                                cityName = data.areaName
                                getData(areaId)
                            })
                }
                .attachTo(city_list)
    }

    fun getData(areaId: String) {
        OkGo.post<BaseResponse<ArrayList<CityNewData>>>(BaseHttp.area_mine_list)
                .tag(this@AreaListActivity)
                .headers("token", getString("token"))
                .params("areaId", areaId)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityNewData>>>(baseContext, true) {
                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CityNewData>>>) {
                        list.clear()
                        list.addItems(response.body().`object`)
                        if (list.size > 0) {
                            mAdapter.updateData(list).notifyDataSetChanged()
                        } else {
                            EventBus.getDefault().post(CityNewEvent(getString("areaName"), getString("areaId")))
                            onBackPressed()
                        }
                    }
                })
    }

    override fun doClick(v: View) {
        when (v.id) {
            R.id.iv_nav_back -> {
                if (areaId.isNotEmpty()) {
                    areaId = ""
                    getData(areaId)
                } else {
                    onBackPressed()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("选择区镇")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("选择区镇")
    }
}

