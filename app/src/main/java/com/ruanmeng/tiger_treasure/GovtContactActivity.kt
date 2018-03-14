package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.model.CityData
import com.ruanmeng.share.BaseHttp
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx

/**
 * Created by PYM2017 on 2018/1/24.
 */
class GovtContactActivity : BaseActivity() {

    private val list = ArrayList<CityData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_list)
        init_title("政府号")
        getData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.isEnabled = false
        recycle_list.layoutManager = LinearLayoutManager(this)
        mAdapter = SlimAdapter.create(SlimAdapterEx::class.java)
                .register<CityData>(R.layout.item_contact_list) { data, injector ->
                    injector.gone(R.id.item_contact_check)
                            .visibility(R.id.item_contact_cname, View.VISIBLE)
                            .text(R.id.item_contact_cname, data.compName)
                            .text(R.id.item_contact_name, data.userName)
                            .visibility(
                                    R.id.item_contact_divider1,
                                    if (list.indexOf(data) != list.size - 1
                                            && data.letter != list[list.indexOf(data) + 1].letter)
                                        View.GONE
                                    else View.VISIBLE)

                            .with<ImageView>(R.id.item_contact_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .placeholder(R.mipmap.default_user) //等待时的图片
                                        .error(R.mipmap.default_user)       //加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_contact) {
                                //融云刷新用户信息
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.enterpriserId,
                                        data.userName,
                                        Uri.parse(BaseHttp.baseImg + data.userhead)))
                                //融云单聊
                                RongIM.getInstance().startPrivateChat(baseContext, data.enterpriserId, data.userName)
                            }
                            //点击头像跳转企业家详情页
                            .clicked(R.id.item_contact_img) {
                                val intent = Intent(baseContext, PersonActivity::class.java)
                                intent.putExtra("isGov", true)
                                intent.putExtra("accountInfoId", data.enterpriserId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }


    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CityData>>>(BaseHttp.government_list)
                .tag(this@GovtContactActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CityData>>>) {
                        list.addAll(response.body().`object`)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }
                })
    }
}