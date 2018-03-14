package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
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
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Discussion
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_contact_del.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.util.*

class ContactDelActivity : BaseActivity() {
    private lateinit var list: ArrayList<CommonData>
    private val list_index = ArrayList<String>()
    private lateinit var list_member: ArrayList<String>
    private var mTargetId = ""
    private val targetIds = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_del)
        init_title("移除成员")
        list_member = intent.getSerializableExtra("list") as ArrayList<String>
        list = ArrayList()
        getMemebers()
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "确定"
        mTargetId = intent.getStringExtra("mTargetId")
        del_list.apply {
            linearLayoutManager = LinearLayoutManager(baseContext)
            layoutManager = linearLayoutManager
            /*val decoration = object : NormalDecoration() {
                override fun getHeaderName(pos: Int): String = list[pos].letter
            }
            @Suppress("DEPRECATION")
            decoration.setHeaderContentColor(resources.getColor(R.color.divider))
            decoration.setHeaderHeight(DensityUtil.dp2px(30f))
            decoration.setTextSize(DensityUtil.sp2px(14f))
            @Suppress("DEPRECATION")
            decoration.setTextColor(resources.getColor(R.color.gray))
            addItemDecoration(decoration)*/
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_contact_list) { data, injector ->
                    injector.text(R.id.item_contact_name, data.userName)
                            .checked(R.id.item_contact_check, data.isChecked)
                            .visibility(R.id.item_contact_cname, View.VISIBLE)
                            .text(R.id.item_contact_cname, data.compName)
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
                .attachTo(del_list)

        /*val letters = arrayOf(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
        list_index.addAll(letters)
        indexdel_layout.setIndexBarHeightRatio(0.9f)
        indexdel_layout.indexBar.setIndexsList(list_index)
        indexdel_layout.indexBar.setIndexChangeListener { name ->
            for (item in list) {
                if (name == item.letter) {
                    linearLayoutManager.scrollToPositionWithOffset(list.indexOf(item), 0)
                    return@setIndexChangeListener
                }
            }
        }*/
        btRight.setOnClickListener {
            val memberNews = ArrayList<String>()
            list.filter { it.isChecked }.mapTo(memberNews) { it.accountInfoId }
            if(memberNews.size==0){
                toast("请选择您要移除的成员")
                return@setOnClickListener
            }
            memberNews.forEach {
                /**
                 * 供创建者将某用户移出讨论组。
                 *
                 * 移出自己或者调用者非讨论组创建者将产生 {@link RongIMClient.ErrorCode#UNKNOWN} 错误。
                 *
                 * @param discussionId 讨论组 Id。
                 * @param userId       用户 Id。
                 * @param callback     执行操作的回调。
                 */
                RongIM.getInstance().removeMemberFromDiscussion(mTargetId, it, object : RongIMClient.OperationCallback() {

                    override fun onSuccess() {
                        toast("移除成功")
                        EventBus.getDefault().post(RefreshMessageEvent(5, "移除成员"))
                        ActivityStack.getScreenManager().popActivities(this@ContactDelActivity::class.java)
                    }

                    override fun onError(errorCode: RongIMClient.ErrorCode) {
                        OkLogger.e("移除讨论组成员失败，错误码：" + errorCode.message)

                        toast("移除成员失败")
                    }
                })
            }

        }
    }
    private fun getMemebers() {
        /**
         * 获取讨论组信息和设置
         *
         * @param id       讨论组 Id。
         * @param callback 获取讨论组的回调。
         */
        RongIM.getInstance().getDiscussion(mTargetId, object : RongIMClient.ResultCallback<Discussion>() {

            @SuppressLint("SetTextI18n")
            override fun onSuccess(discussion: Discussion) {
                targetIds.clear()
                targetIds.addAll(discussion.memberIdList)

                val strBuilder = StringBuilder()
                targetIds.forEach { strBuilder.append(it).append(",") }
                getData(strBuilder.subSequence(0, strBuilder.length - 1).toString())
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {
                OkLogger.e("获取讨论组失败，错误码：" + errorCode.message)
            }

        })
    }

    fun getData(userIds: String) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.chatroom_users)
                .tag(this@ContactDelActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userIds", userIds)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.clear()
                        list.addItems(response.body().`object`)

                        list.forEach {
                            //融云刷新用户信息
                            RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                    (it).accountInfoId,
                                    it.userName,
                                    Uri.parse(BaseHttp.baseImg + it.userhead)))
                        }
//                        seperateLists(response.body().`object`)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }
                })
    }


    /*private fun seperateLists(mlist: List<CommonData>?) {
        if (mlist != null && mlist.isNotEmpty()) {
            mlist.filter {
                !list_member.contains(it.enterpriserId)
            }.forEach {
                if (it.letter.isEmpty()) it.letter = "#"
                list.add(it)
            }
            Collections.sort(list, PinyinContactComparator())
        }
    }*/


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("移除成员")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("移除成员")
    }
}
