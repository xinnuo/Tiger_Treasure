package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.lzy.extend.BaseResponse
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
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter

class MessageActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "消息"
        empty_hint.text = "暂无消息"
        if (getString("isGov") == "1") {
            left_nav_right.text = "发布"
            left_nav_right.setOnClickListener {
                startActivity(ReleaseMessageActivity::class.java)
            }
        }
        message_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position + 1

                    window.decorView.postDelayed({
                        runOnUiThread {
                            OkGo.getInstance().cancelTag(this@MessageActivity)
                            updateList()
                        }
                    }, 300)
                }

            })

            val isNotice = intent.getBooleanExtra("isNotice", false)
            addTab(this.newTab().setText("我的消息"), !isNotice)
            addTab(this.newTab().setText("公告"), isNotice)

            post { Tools.setIndicator(this, 50, 50) }
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_message_list) { data, injector ->
                    injector.gone(R.id.item_message_time)
                            .gone(R.id.item_message_content)
                            .apply {
                                if (data.readStatus == "0") {//item_message_count
                                    visible(R.id.item_message_count)
                                } else {
                                    invisible(R.id.item_message_count)
                                }
                            }
                            .text(R.id.item_message_title, data.title)
                            .visibility(R.id.item_message_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_message_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_message_img) { view -> view.setImageResource(R.mipmap.icon_msg) }

                            .clicked(R.id.item_message) {
                                data.readStatus = "1"
                                mAdapter.updateData(list).notifyDataSetChanged()
                                val intent = Intent(baseContext, GGWebActivity::class.java)
                                intent.putExtra("title", "详情")
                                intent.putExtra("messageInfoId", data.messageInfoId)
                                intent.putExtra("messageId", data.messageId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.message_list)
                .tag(this@MessageActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("messageType", mPosition)
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

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("消息公告界面")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("消息公告界面")
    }

}
