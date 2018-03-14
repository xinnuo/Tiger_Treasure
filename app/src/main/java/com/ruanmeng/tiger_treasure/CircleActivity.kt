package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class CircleActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        init_title("金融圈", "联系平台")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @Suppress("DEPRECATION")
    override fun init_title() {
        super.init_title()
        val drawable = resources.getDrawable(R.mipmap.icon_call)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(drawable, null, null, null)
        tvRight.setTextColor(resources.getColor(R.color.colorAccent))
        tvRight.setOnClickListener {
            if (getString("platform").isNotEmpty()) {
                DialogHelper.showDialog(
                        baseContext,
                        "平台电话",
                        "拨打平台电话 " + getString("platform"),
                        "取消",
                        "呼叫") {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString("platform")))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }

        empty_hint.text = "暂无金融圈相关信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_circle_list) { data, injector ->
                    injector.invisible(R.id.item_circle_scan)
                            .text(R.id.item_circle_title, data.financialTitle)
                            .text(R.id.item_circle_company, data.compName)
                            .text(R.id.item_circle_scan, "0")

                            .with<ImageView>(R.id.item_circle_logo) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.compLogo)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .with<ImageView>(R.id.item_circle_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.financialImage)
                                        .placeholder(R.mipmap.default_banner) // 等待时的图片
                                        .error(R.mipmap.default_banner)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_circle) {
                                intent.setClass(baseContext, CircleDetailActivity::class.java)
                                intent.putExtra("financialId", data.financialId)
                                intent.putExtra("compName", data.compName)
                                intent.putExtra("compLogo", data.compLogo)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.financial_list)
                .tag(this@CircleActivity)
                .params("page", pindex)
                .headers("token", getString("token"))
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
        MobclickAgent.onPageStart("金融圈")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("金融圈")
    }

}
