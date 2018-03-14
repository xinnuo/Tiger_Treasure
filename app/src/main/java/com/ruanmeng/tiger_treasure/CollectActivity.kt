package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter

class CollectActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private lateinit var mProductAdapter: SlimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "我的收藏"
        empty_hint.text = "暂无企业收藏信息！"

        swipe_refresh.refresh {
            if (mPosition == 1) {
                getCompanyData(1)
            } else {
                getProductsData(1)
            }
        }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                if (mPosition == 1) {
                    getCompanyData(pageNum)
                } else {
                    getProductsData(pageNum)
                }
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_collect_list) { data, injector ->
                    injector.gone(R.id.item_collect_watch)
                            .text(R.id.item_collect_title, data.compName)
                            .text(R.id.item_collect_addr, data.address)

                            .with<RoundedImageView>(R.id.item_collect_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.compLogo)
                                        .placeholder(R.mipmap.default_banner) // 等待时的图片
                                        .error(R.mipmap.default_banner)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_collect) {
                                val intent = Intent(baseContext, EnterpriseDetailActivity::class.java)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        mProductAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_product_list) { data, injector ->
                    injector.text(R.id.item_product_title, data.productName)
                            .text(R.id.item_product_price, "¥"+data.productPrice)

                            .with<ImageView>(R.id.item_product_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.productPic)
                                        .placeholder(R.mipmap.default_banner) // 等待时的图片
                                        .error(R.mipmap.default_banner)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_product) {
                                val intent = Intent(baseContext, MemberFirstActivity::class.java)
                                intent.putExtra("supplyId", data.productId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)

        collect_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position + 1
                    OkGo.getInstance().cancelTag(this@CollectActivity)//确保每个时段只请求一个接口
                    if (mPosition == 1) {
                        swipe_refresh.isRefreshing = true
                        if (list.size > 0) {
                            list.clear()
                            mProductAdapter.updateData(list).notifyDataSetChanged()
                        }
                        mAdapter.attachTo(recycle_list)
                        pageNum = 1
                        getCompanyData(pageNum)
                    } else {
                        swipe_refresh.isRefreshing = true
                        if (list.size > 0) {
                            list.clear()
                            mAdapter.updateData(list).notifyDataSetChanged()
                        }
                        mProductAdapter.attachTo(recycle_list)
                        pageNum = 1
                        getProductsData(pageNum)
                    }
                }
            })

            addTab(this.newTab().setText("企业"), true)//默认选中
            addTab(this.newTab().setText("产品"), false)

            post { Tools.setIndicator(this, 50, 50) }
        }
    }

    fun getCompanyData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.company_collect)
                .tag(this@CollectActivity)
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

                        empty_hint.text = "暂无企业收藏信息！"
                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    fun getProductsData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.products_collect)
                .tag(this@CollectActivity)
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

                        mProductAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_hint.text = "暂无产品收藏信息！"
                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("我的收藏")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("我的收藏")
    }
}
