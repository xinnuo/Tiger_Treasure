package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo.post
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class ScanActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        init_title("谁浏览过我")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无浏览信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_watch_list) { data, injector ->
                    injector.text(R.id.item_watch_name, data.userName)
                            .text(R.id.item_watch_time, data.showDate)
                            .text(R.id.item_watch_cname, data.compName)
                            .visible(R.id.item_watch_time)
                            .visible(R.id.item_watch_cname)

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
    }

    override fun getData(pindex: Int) {
        post<BaseResponse<CommonModel>>(BaseHttp.scanlist)
                .tag(this@ScanActivity)
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

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("谁浏览过我")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("谁浏览过我")
    }
}
