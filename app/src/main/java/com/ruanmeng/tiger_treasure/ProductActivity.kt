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
import com.ruanmeng.model.ProductData
import com.ruanmeng.model.ProductModel
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class ProductActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        init_title("产品列表")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关产品信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<ProductData>(R.layout.item_product_list) { data, injector ->
                    injector.text(R.id.item_product_title, data.supplyTitle)
                            .text(R.id.item_product_price, "¥${data.retailPrice}")

                            .visibility(R.id.item_product_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_product_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_product_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.supplyPic)
                                        .placeholder(R.mipmap.default_product2) // 等待时的图片
                                        .error(R.mipmap.default_product2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_product) {
                                val intent = Intent(baseContext, MemberFirstActivity::class.java)
                                intent.putExtra("supplyId", data.supplyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<ProductModel>>(BaseHttp.supply_company)
                .tag(this@ProductActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("companyId", intent.getStringExtra("companyId"))
                .execute(object : JacksonDialogCallback<BaseResponse<ProductModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ProductModel>>) {
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

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("产品列表")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("产品列表")
    }
}
