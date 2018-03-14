package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.model.ManagerData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ManagerActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        setToolbarVisibility(false)
        init_title()
        EventBus.getDefault().register(this@ManagerActivity)
        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        left_nav_right.visibility = View.VISIBLE
        left_nav_right.text = "添加"
        left_nav_title.text = "管理企业管家"
        empty_hint.text = "暂企业管家信息！"

        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<ManagerData>(R.layout.item_manager_list) { data, injector ->
                    injector.text(R.id.item_manager_name, data.userName)
                            .text(R.id.item_manager_work, "职务：" + data.positionName)
                            .gone(R.id.item_manager_check)
                            .visible(R.id.item_manager_tel)
                            .text(R.id.item_manager_tel,data.telephone)

                            .visibility(R.id.item_manager_agree, if ("1" == data.status) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_manager_circle, if ("1" == data.status) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_manager_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .placeholder(R.mipmap.default_logo) //等待时的图片
                                        .error(R.mipmap.default_logo)       //加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)

                            }
                            //联系管家
                            .clicked(R.id.item_manager_link) {
                                //融云刷新用户信息
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.managerId,
                                        data.userName,
                                        Uri.parse(BaseHttp.baseImg + data.userhead)))
                                //融云单聊
                                RongIM.getInstance().startPrivateChat(baseContext, data.managerId, data.userName)
                            }
                            .clicked(R.id.item_manager_del) {
                                DialogHelper.showDialog(
                                        baseContext,
                                        "温馨提示",
                                        "确定要删除该管家吗？",
                                        "取消",
                                        "确定") {
                                    OkGo.post<String>(BaseHttp.managerdel)
                                            .tag(this@ManagerActivity)
                                            .isMultipart(true)
                                            .headers("token", getString("token"))
                                            .params("managerId", data.managerId)
                                            .execute(object : StringDialogCallback(baseContext) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                    toast(msg)
                                                    list.remove(data)
                                                    mAdapter.updateData(list).notifyDataSetChanged()
                                                    empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                                                }
                                            })
                                }
                            }
                            .clicked(R.id.item_manager_agree) {
                                OkGo.post<String>(BaseHttp.manageraccept)
                                        .tag(this@ManagerActivity)
                                        .isMultipart(true)
                                        .headers("token", getString("token"))
                                        .params("managerId", data.managerId)
                                        .execute(object : StringDialogCallback(baseContext) {

                                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                toast(msg)
                                                data.status = "1"
                                                mAdapter.updateData(list).notifyDataSetChanged()
                                            }
                                        })
                            }
                }
                .attachTo(recycle_list)
        //添加
        left_nav_right.setOnClickListener{
            val intent = Intent(baseContext, AddManagerActivity::class.java)
//            intent.putExtra("companyId",)
            startActivity(intent)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<ManagerData>>>(BaseHttp.managerlist)
                .tag(this@ManagerActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<ManagerData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<ManagerData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                            if (count(response.body().`object`) > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })

    }
    override fun finish() {
        EventBus.getDefault().unregister(this@ManagerActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: LocationMessageEvent) {
        when (event.type) {
            "添加管家" -> {
                getData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("管理企业管家")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("管理企业管家")
    }
}
