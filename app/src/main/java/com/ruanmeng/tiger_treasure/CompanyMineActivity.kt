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
import com.ruanmeng.model.CompanyData
import com.ruanmeng.model.CompanyModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class CompanyMineActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_mine)
        init_title("我的公司")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无我的公司信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CompanyData>(R.layout.item_company_list) { data, injector ->
                    injector.gone(R.id.item_company_type)
                            .text(R.id.item_company_name, data.compName)
                            .text(R.id.item_company_watch, "关注 ${data.gzNum}")

                            .with<ImageView>(R.id.item_company_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.copmImage)
                                        .placeholder(R.mipmap.default_product) // 等待时的图片
                                        .error(R.mipmap.default_product)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_company_edit) {
                                val intent = Intent(baseContext, CompanyModifyActivity::class.java)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CompanyModel>>(BaseHttp.company_mine)
                .tag(this@CompanyMineActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<BaseResponse<CompanyModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CompanyModel>>) {
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
}
