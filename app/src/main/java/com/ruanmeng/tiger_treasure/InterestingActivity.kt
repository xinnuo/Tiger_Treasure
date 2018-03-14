package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.PersonMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus

class InterestingActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private var interestList = ArrayList<CommonData>()

    private var names = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interesting)
        init_title("我的兴趣爱好")
        @Suppress("UNCHECKED_CAST")
        interestList = intent.getSerializableExtra("interestList") as ArrayList<CommonData>
        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无兴趣爱好信息！"
        btRight.visibility = View.VISIBLE
        btRight.text = "保存"
        //包含列表初始化
        recycle_list.load_Linear(baseContext)
        swipe_refresh.refresh { getData() }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_contact_list) { data, injector ->
                    injector.gone(R.id.item_contact_img)
                            .text(R.id.item_contact_name, data.chatRoomName)
                            .checked(R.id.item_contact_check, data.isChecked)
                            .clicked(R.id.item_contact) {
                                data.isChecked = !data.isChecked
                                mAdapter.updateData(list).notifyDataSetChanged()
                            }
                }
                .attachTo(recycle_list)
        //保存
        btRight.setOnClickListener {
            val chatRoomId = ArrayList<String>()
            val str_builder = StringBuilder()
            val namestr_builder = StringBuilder()
            val Ids: String

            list.filter { it.isChecked }.mapTo(chatRoomId) { it.chatRoomId }
            list.forEach { if (it.isChecked) str_builder.append(it.chatRoomId).append(",") }
            list.forEach {
                if (it.isChecked) namestr_builder.append(it.chatRoomName).append(",")
            }

            if (chatRoomId.isEmpty()) {
                toast("请选择您的兴趣爱好")
                return@setOnClickListener
            }
            Ids = if (str_builder.lastIndexOf(",") == str_builder.length - 1) {
                str_builder.substring(0, str_builder.length - 1)
            } else {
                str_builder.toString()
            }
            names = if (namestr_builder.lastIndexOf(",") == namestr_builder.length - 1) {
                namestr_builder.substring(0, namestr_builder.length - 1)
            } else {
                namestr_builder.toString()
            }
            putString("interstname", names)
            OkGo.post<String>(BaseHttp.saveinteresting)
                    .tag(this@InterestingActivity)
                    .headers("token", getString("token"))
                    .params("chatRoomIds", Ids)
                    .execute(object : StringDialogCallback(baseContext) {
                        override fun onSuccessResponse(response: Response<String>?, msg: String?, msgCode: String?) {
                            toast("保存成功！")
                            EventBus.getDefault().post(PersonMessageEvent(4, getString("interstname")))
                            ActivityStack.getScreenManager().popActivities(this@InterestingActivity::class.java)
                        }
                    })

        }
    }

    /**
     * 列表接口
     */
    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.allinteresting)
                .tag(this@InterestingActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.apply {
                            clear()
                            addItems(response.body().`object`)
                        }

                        list.forEach {
                            interestList
                                    .filter { item -> item.chatRoomId == it.chatRoomId }
                                    .forEach { _ -> it.isChecked = true }
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
        MobclickAgent.onPageStart("兴趣爱好")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("兴趣爱好")
    }
}
