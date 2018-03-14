package com.ruanmeng.tiger_treasure

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.CityData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.sort.PinyinContactComparator
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DensityUtil
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_contact_add.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import qdx.stickyheaderdecoration.NormalDecoration
import java.util.*
import kotlin.collections.ArrayList

class ContactAddActivity : BaseActivity() {

    private lateinit var list: ArrayList<CityData>
    private lateinit var list_member: ArrayList<String>
    private val list_index = ArrayList<String>()

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_add)
        init_title("创建群聊")

        if (intent.getBooleanExtra("isAdd", false)) {
            tvTitle.text = "添加成员"
            list_member = intent.getSerializableExtra("list") as ArrayList<String>
            list = ArrayList()

            getData()
        } else {
            list = intent.getSerializableExtra("list") as ArrayList<CityData>
            mAdapter.updateData(list).notifyDataSetChanged()
        }
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "确定"

        contact_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
            val decoration = object : NormalDecoration() {
                override fun getHeaderName(pos: Int): String = list[pos].letter
            }
            @Suppress("DEPRECATION")
            decoration.setHeaderContentColor(resources.getColor(R.color.divider))
            decoration.setHeaderHeight(DensityUtil.dp2px(30f))
            decoration.setTextSize(DensityUtil.sp2px(14f))
            @Suppress("DEPRECATION")
            decoration.setTextColor(resources.getColor(R.color.gray))
            addItemDecoration(decoration)
        }

        mAdapter = SlimAdapter.create()
                .register<CityData>(R.layout.item_contact_list) { data, injector ->
                    injector.text(R.id.item_contact_name, data.userName)
                            .checked(R.id.item_contact_check, data.isChecked)
                            .visibility(R.id.item_contact_cname, View.VISIBLE)
                            .text(R.id.item_contact_cname, data.compName)
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
                                data.isChecked = !data.isChecked
                                mAdapter.updateData(list).notifyDataSetChanged()
                            }
                }
                .attachTo(contact_list)

        val letters = arrayOf(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
        list_index.addAll(letters)
        index_layout.setIndexBarHeightRatio(0.9f)
        index_layout.indexBar.setIndexsList(list_index)
        index_layout.indexBar.setIndexChangeListener { name ->
            for (item in list) {
                if (name == item.letter) {
                    linearLayoutManager.scrollToPositionWithOffset(list.indexOf(item), 0)
                    return@setIndexChangeListener
                }
            }
        }

        btRight.setOnClickListener {
            if (intent.getBooleanExtra("isAdd", false)) {

                val memberNews = ArrayList<String>()
                list.filter { it.isChecked }.mapTo(memberNews) { it.enterpriserId }

                /**
                 * 添加一名或者一组用户加入讨论组
                 *
                 * @param discussionId 讨论组 Id。
                 * @param userIdList   邀请的用户 Id 列表。
                 * @param callback     执行操作的回调。
                 */
                RongIM.getInstance().addMemberToDiscussion(
                        intent.getStringExtra("discussionId"),
                        memberNews,
                        object : RongIMClient.OperationCallback() {

                            override fun onSuccess() {
                                toast("添加成功")

                                EventBus.getDefault().post(RefreshMessageEvent(4, "添加成员"))
                                ActivityStack.getScreenManager().popActivities(this@ContactAddActivity::class.java)
                            }

                            override fun onError(errorCode: RongIMClient.ErrorCode) {
                                OkLogger.e("添加讨论组成员失败，错误码：" + errorCode.message)

                                toast("添加成员失败")
                            }

                        })
            } else {
                val targetUserIds = ArrayList<String>()
                val str_builder = StringBuilder()
                val names: String

                list.filter { it.isChecked }.mapTo(targetUserIds) { it.enterpriserId }
                list.forEach { if (it.isChecked) str_builder.append(it.userName).append("、") }

                if (targetUserIds.isEmpty()) {
                    toast("请选择聊天成员")
                    return@setOnClickListener
                }

                if (!targetUserIds.contains(getString("token"))) {
                    targetUserIds.add(getString("token"))
                    str_builder.append(getString("userName"))
                }

                names = if (str_builder.lastIndexOf("、") == str_builder.length - 1) {
                    str_builder.substring(0, str_builder.length - 1)
                } else {
                    str_builder.toString()
                }

                /**
                 * 创建讨论组会话并进入会话界面。
                 * 讨论组创建成功后，会返回讨论组 id。
                 *
                 * @param context 应用上下文。
                 * @param targetUserIds 要与之聊天的讨论组用户 Id 列表。
                 * @param title 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
                 * @param callback 讨论组回调，成功时，返回讨论组 id。
                 */
                RongIM.getInstance().createDiscussionChat(
                        baseContext,
                        targetUserIds,
                        names,
                        object : RongIMClient.CreateDiscussionCallback() {

                            override fun onSuccess(disscussionId: String) {
                                OkLogger.i("disscussionId = " + disscussionId)

                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        disscussionId,
                                        names,
                                        Uri.parse(BaseHttp.baseImg + getString("userhead"))))

                                ActivityStack.getScreenManager().popActivities(
                                        this@ContactAddActivity::class.java,
                                        ContactActivity::class.java)
                            }

                            override fun onError(errorCode: RongIMClient.ErrorCode) {
                                OkLogger.e("创建讨论组失败，错误码：" + errorCode.message)

                                toast("创建群聊失败")
                            }

                        })
            }
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CityData>>>(BaseHttp.enterpriser2_list)
                .tag(this@ContactAddActivity)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CityData>>>) {

                        seperateLists(response.body().`object`)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }
                })
    }

    private fun seperateLists(mlist: List<CityData>?) {
        if (mlist != null && mlist.isNotEmpty()) {
            mlist.filter {
                !list_member.contains(it.enterpriserId)
            }.forEach {
                if (it.letter.isEmpty()) it.letter = "#"
                list.add(it)
            }
            Collections.sort(list, PinyinContactComparator())
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("创建群聊/添加成员")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("创建群聊/添加成员")
    }
}
