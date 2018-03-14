package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.MyVipModel
import com.ruanmeng.model.VipConfigs
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_vip.*
import net.idik.lib.slimadapter.SlimAdapter

class VipActivity : BaseActivity() {
    private val list = ArrayList<VipConfigs>()
    //    private val list_vip = ArrayList<VipPriceData>()
    var vipTypeName: String = ""
    var vipTypeId: String = ""
    var expiryDate: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip)
        init_title("我的会员")
        getData()
    }

    override fun init_title() {
        super.init_title()
        recycle_list.apply {
            //            layoutManager = FullyGridLayoutManager(baseContext, 4, GridLayoutManager.VERTICAL, false)
            mAdapter = SlimAdapter.create()
                    .register<VipConfigs>(R.layout.item_vip) { data, injector ->
                        injector.text(R.id.tv_vip_first, data.configName)
                                .with<ImageView>(R.id.imv) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.configIcon)
                                            .placeholder(R.mipmap.default_product) // 等待时的图片
                                            .error(R.mipmap.default_product)       // 加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                    }
                    .attachTo(recycle_list)
        }
        recycle_list.load_Linear(baseContext)

        if (getString("vipCode") == "VIP_SLIVER") {
            img_vip.setBackgroundResource(R.mipmap.me_vip_center)
        } else if (getString("vipCode") == "VIP_GOLD") {
            img_vip.setBackgroundResource(R.mipmap.me_vip_most)
        } else {
            img_vip.setBackgroundResource(R.mipmap.me_vip_normal)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<MyVipModel>>(BaseHttp.vip_info)
                .tag(this@VipActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<BaseResponse<MyVipModel>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<MyVipModel>>) {
                        tv_vipname.text = response.body().`object`.compName
                        if (response.body().`object`.expiryDate != "" && response.body().`object`.expiryDate.isNotBlank()) {
                            tv_viptime.text = response.body().`object`.vipTypeName + "(" + response.body().`object`.expiryDate + ")到期"
                            expiryDate = response.body().`object`.expiryDate
                        } else {
                            tv_viptime.text = response.body().`object`.vipTypeName
                        }
                        vipTypeName = response.body().`object`.vipTypeName
                        vipTypeId = response.body().`object`.vipTypeId
                        list.apply {
                            clear()
                        }
//                        list_vip.addItems(response.body().`object`.vipPrices)
                        list.addItems(response.body().`object`.vipConfigs)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }
                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.item_vip_open -> {
                intent = Intent(baseContext, PayActivity::class.java)
                intent.putExtra("title", vipTypeName)
//                intent.putExtra("items", list_vip)
                intent.putExtra("vipIsPay", "2")
                intent.putExtra("vipTypeId", vipTypeId)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("我的会员")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("我的会员")
    }

}
