package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.model.CityMess
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_city.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.util.*

class AreaActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        init_title("选择区域")
        getData()
    }

    override fun init_title() {
        super.init_title()
        city_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
        }
        mAdapter = SlimAdapter.create()
                .register<CityMess>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .visibility(R.id.item_city_divider, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_city_divider1, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .clicked(R.id.item_city_name, {
                                EventBus.getDefault().post(LocationMessageEvent(
                                        data.areaId,
                                        data.location,
                                        intent.getStringExtra("areaName") + intent.getStringExtra("cityName") + data.areaName,
                                        "选择城镇"))
                                ActivityStack.getScreenManager().popActivities(this@AreaActivity::class.java, ProvinceListActivity::class.java, CityActivity::class.java)
                            })
                }
                .attachTo(city_list)
    }

    override fun getData() {
        list.addItems(intent.getSerializableExtra("areaList") as ArrayList<CityMess>)
        mAdapter.updateData(list).notifyDataSetChanged()
        /* OkGo.post<BaseResponse<ArrayList<CityData>>>(BaseHttp.area_list)
                 .tag(this@AreaActivity)
                 .isMultipart(true)
                 .params("code", intent.getStringExtra("areaName"))
                 .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityData>>>(baseContext, true) {

                     override fun onSuccess(response: Response<BaseResponse<ArrayList<CityData>>>) {
 //                        list.addItems(response.body().`object`)
                         list.addItems(intent.getSerializableExtra("areaList") as ArrayList<CityMess>)
                         mAdapter.updateData(list).notifyDataSetChanged()
                     }

                 })*/
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("选择区域")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("选择区域")
    }
}