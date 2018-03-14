package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_step.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter

class StepActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private lateinit var mAccountAdapter: SlimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {

        left_nav_title.text = "足迹"
        empty_hint.text = "暂无足迹信息！"

        step_zu.setOnClickListener { step_expand.isExpanded = !step_expand.isExpanded }

        swipe_refresh.refresh {
            if (mPosition == 1) {
                getAccountData(1)
            } else {
                getCompanyData(1)
            }
        }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                if (mPosition == 1) {
                    getAccountData(pageNum)
                } else {
                    getCompanyData(pageNum)
                }
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_watch_list) { data, injector ->
                    injector.text(R.id.item_watch_name, data.compName)
                    injector.text(R.id.item_watch_time, data.showDate)
                            .visible(R.id.item_watch_time)
                            .gone(R.id.item_watch_cname)
                            .with<RoundedImageView>(R.id.item_watch_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.compLogo)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_watch) {
                                val intent = Intent(baseContext, EnterpriseDetailActivity::class.java)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
        mAccountAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_watch_list) { data, injector ->
                    injector.text(R.id.item_watch_name, data.userName)
                    injector.text(R.id.item_watch_time, data.showDate)
                            .visible(R.id.item_watch_time)
                            .visible(R.id.item_watch_cname)
                            .text(R.id.item_watch_cname, data.compName)
                            .with<RoundedImageView>(R.id.item_watch_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_watch) {
                                val intent = Intent(baseContext, PersonActivity::class.java)
                                intent.putExtra("accountInfoId", data.accountInfoId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        step_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position + 1
                    OkGo.getInstance().cancelTag(this@StepActivity)//确保每个时段只请求一个接口
                    if (mPosition == 1) {
                        swipe_refresh.isRefreshing = true
                        if (list.size > 0) {
                            list.clear()
                            mAdapter.updateData(list).notifyDataSetChanged()
                        }
                        mAccountAdapter.attachTo(recycle_list)
                        pageNum = 1
                        getAccountData(pageNum)
                    } else {
                        swipe_refresh.isRefreshing = true
                        if (list.size > 0) {
                            list.clear()
                            mAccountAdapter.updateData(list).notifyDataSetChanged()
                        }
                        mAdapter.attachTo(recycle_list)
                        pageNum = 1
                        getCompanyData(pageNum)
                    }
                }
            })
//管家登录只有看过的企业
            if (getString("accountType") == "App_Manager") {
                step_tab.visibility = View.GONE
                addTab(this.newTab().setText("看过的企业家"), false)//默认选中
                addTab(this.newTab().setText("看过的企业"), true)
            } else {
                step_tab.visibility = View.VISIBLE
                addTab(this.newTab().setText("看过的企业家"), true)//默认选中
                addTab(this.newTab().setText("看过的企业"), false)
            }
            post { Tools.setIndicator(this, 30, 30) }
        }
    }

    fun getCompanyData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.stepcompany)
                .tag(this@StepActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
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

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    fun getAccountData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.stepaccount)
                .tag(this@StepActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JsonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.rows)
                            if (count(response.body().`object`.rows) > 0) pageNum++
                        }
                        mAccountAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("我的足迹")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("我的足迹")
    }

}
