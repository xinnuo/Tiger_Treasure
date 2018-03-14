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
import com.ruanmeng.model.ProvinceData
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_city.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class ProvinceListActivity : BaseActivity() {
    private val list = ArrayList<Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        init_title("选择省")
        getData()
    }


    override fun init_title() {
        super.init_title()
        city_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
        }

        mAdapter = SlimAdapter.create()
                .register<ProvinceData>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .visibility(R.id.item_city_divider, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_city_divider1, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_city_name, {
                                intent.setClass(baseContext, CityActivity::class.java)
                                intent.putExtra("areaName", data.areaName)
                                startActivity(intent)
                                //发广播--刷新数据
                                /*EventBus.getDefault().post(LocationMessageEvent(
                                        data.areaId,
                                        "",
                                        intent.getStringExtra("areaName") + data.areaName,
                                        "选择区域"))
                                        //回到信息展示页
                                ActivityStack.getScreenManager().popActivities(this@CityActivity::class.java, CityListActivity::class.java)*/
                            })
                }
                .attachTo(city_list)
    }

    /**
     * 获取区域
     */
    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<ProvinceData>>>(BaseHttp.pro_list)
                .tag(this@ProvinceListActivity)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<ProvinceData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<ProvinceData>>>) {
                        list.addItems(response.body().`object`)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("选择省")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("选择省")
    }
}
