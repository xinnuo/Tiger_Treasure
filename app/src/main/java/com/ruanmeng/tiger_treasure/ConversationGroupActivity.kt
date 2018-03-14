package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.view.FullyGridLayoutManager
import com.ruanmeng.view.MultiGapDecoration
import com.umeng.analytics.MobclickAgent
import io.rong.eventbus.EventBus
import io.rong.imkit.RongIM
import io.rong.imkit.model.Event
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Discussion
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_conversation_group.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.collections.ArrayList

class ConversationGroupActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private val targetIds = ArrayList<String>()

    private var mTargetId = ""
    private var mTitle = ""
    private var creatorId = ""
    private var index_remove = ""
    private lateinit var mConversationType: Conversation.ConversationType
    private var isTop = false
    private var mStatus = Conversation.ConversationNotificationStatus.NOTIFY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_group)
        setToolbarVisibility(false)
        init_title()

        org.greenrobot.eventbus.EventBus.getDefault().register(this@ConversationGroupActivity)

        getMemebers()
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
                //创建者id
                creatorId = discussion.creatorId
                targetIds.addAll(discussion.memberIdList)
                left_nav_title.text = "聊天信息(${targetIds.size})"

                val strBuilder = StringBuilder()
                targetIds.forEach { strBuilder.append(it).append(",") }
                getData(strBuilder.subSequence(0, strBuilder.length - 1).toString())
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {
                OkLogger.e("获取讨论组失败，错误码：" + errorCode.message)
            }

        })
    }

    override fun init_title() {
        super.init_title()
        mTargetId = intent.data.getQueryParameter("targetId")
        mTitle = intent.data.getQueryParameter("title")
        mConversationType = Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))

        /**
         * 根据不同会话类型的目标Id，回调方式获取某一会话信息
         *
         * @param type     会话类型。
         * @param id       目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
         * @param callback 获取会话信息的回调。
         */
        RongIM.getInstance().getConversation(mConversationType, mTargetId, object : RongIMClient.ResultCallback<Conversation>() {

            override fun onSuccess(conversation: Conversation?) {
                if (conversation == null) return
                isTop = conversation.isTop
                mStatus = conversation.notificationStatus
                check_top.isChecked = isTop
                check_mian.isChecked = mStatus != Conversation.ConversationNotificationStatus.NOTIFY
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {
                OkLogger.e("Conversation回调失败，错误码：" + errorCode.message)
            }

        })

        group_list.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 5)
            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_grid_imgs) { data, injector ->
                        injector.text(R.id.grid_name, data.userName)

                                .with<RoundedImageView>(R.id.grid_img) { view ->
                                    GlideApp.with(baseContext)
                                            .load(
                                                    when {
                                                        data.userhead == "添加" -> R.mipmap.uploade
                                                        data.userhead == "删除" -> R.mipmap.addesdel
                                                        else -> BaseHttp.baseImg + data.userhead
                                                    })
                                            .placeholder(R.mipmap.default_user) //等待时的图片
                                            .error(R.mipmap.default_user)       //加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .clicked(R.id.grid_img) {
                                    if (data.userhead == "添加") {
                                        val intent = Intent(baseContext, ContactAddActivity::class.java)
                                        intent.putExtra("isAdd", true)
                                        intent.putExtra("discussionId", mTargetId)
                                        intent.putExtra("list", targetIds)
                                        startActivity(intent)
                                    } else if (data.userhead == "删除") {
                                        val intent = Intent(baseContext, ContactDelActivity::class.java)
                                        intent.putExtra("mTargetId", mTargetId)
                                        intent.putExtra("list", targetIds)
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(baseContext, PersonActivity::class.java)
                                        intent.putExtra("accountInfoId", data.accountInfoId)
                                        startActivity(intent)
                                    }
                                }
                    }
        }

        check_top.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                /**
                 * 设置某一会话为置顶或者取消置顶，回调方式获取设置是否成功
                 *
                 * @param type     会话类型。
                 * @param id       目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
                 * @param isTop    是否置顶。
                 * @param callback 设置置顶或取消置顶是否成功的回调。
                 */
                RongIM.getInstance().setConversationToTop(
                        mConversationType,
                        mTargetId,
                        !isTop,
                        object : RongIMClient.ResultCallback<Boolean>() {

                            override fun onSuccess(result: Boolean) {
                                isTop = !isTop
                                check_top.isChecked = isTop
                            }

                            override fun onError(errorCode: RongIMClient.ErrorCode) {
                                OkLogger.e("置顶失败，错误码：" + errorCode.message)
                            }

                        })
            }
            return@setOnTouchListener true
        }

        check_mian.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showLoadingDialog()
                /**
                 * 设置会话消息提醒状态
                 *
                 * @param conversationType   会话类型。
                 * @param targetId           目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
                 * @param notificationStatus 是否屏蔽。
                 * @param callback           设置状态的回调。
                 */
                RongIM.getInstance().setConversationNotificationStatus(
                        mConversationType,
                        mTargetId,
                        if (check_mian.isChecked) Conversation.ConversationNotificationStatus.NOTIFY else Conversation.ConversationNotificationStatus.DO_NOT_DISTURB,
                        object : RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {

                            override fun onSuccess(result: Conversation.ConversationNotificationStatus) {
                                cancelLoadingDialog()
                                mStatus = result
                                check_mian.isChecked = !check_mian.isChecked
                            }

                            override fun onError(errorCode: RongIMClient.ErrorCode) {
                                OkLogger.e("消息提醒状态设置失败，错误码：" + errorCode.message)
                                cancelLoadingDialog()
                            }

                        })
            }
            return@setOnTouchListener true
        }

        group_clear.setOnClickListener {
            /**
             * 根据会话类型，清空某一会话的所有聊天消息记录，回调方式获取清空是否成功
             *
             * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
             * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
             * @param callback         清空是否成功的回调
             */
            RongIMClient.getInstance().clearMessages(
                    mConversationType,
                    mTargetId,
                    object : RongIMClient.ResultCallback<Boolean>() {

                        override fun onSuccess(result: Boolean) {
                            OkLogger.i("清空聊天记录成功：" + result.toString())

                            EventBus.getDefault().post(Event.MessagesClearEvent(mConversationType, mTargetId)) //融云刷新聊天列表和界面
                            toast("已清空聊天记录")
                        }

                        override fun onError(errorCode: RongIMClient.ErrorCode) {
                            OkLogger.e("清空聊天记录失败，错误码：" + errorCode.message)
                        }
                    })
        }

        bt_quit.setOnClickListener {
            /**
             * 退出当前用户所在的某讨论组
             *
             * @param discussionId 讨论组 Id。
             * @param callback     执行操作的回调。
             */
            RongIM.getInstance().quitDiscussion(mTargetId, object : RongIMClient.OperationCallback() {

                override fun onSuccess() {
                    ActivityStack.getScreenManager().popActivities(ConversationActivity::class.java)
                    EventBus.getDefault().post(Event.QuitDiscussionEvent(mTargetId))
                    ActivityStack.getScreenManager().popActivities(this@ConversationGroupActivity::class.java)
                }

                override fun onError(errorCode: RongIMClient.ErrorCode) {
                    OkLogger.e("退出讨论组失败，错误码：" + errorCode.message)
                }

            })
        }
        //修改群名片
        group_name.setOnClickListener {
            val intent = Intent(baseContext, ChangeGroupNameActivity::class.java)
            intent.putExtra("discussionId", mTargetId)
            startActivity(intent)
        }
    }


    fun getData(userIds: String) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.chatroom_users)
                .tag(this@ConversationGroupActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userIds", userIds)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.clear()
                        //讨论组创建者放在第一位
                        response.body().`object`.filterTo(list) { creatorId == it.accountInfoId }
                        list.addItems(response.body().`object`)
                        list.indices
                                .filter { it != 0 && list[it].accountInfoId == creatorId }
                                .forEach { index_remove = "" + it }
                        if (index_remove.isNotEmpty()) {
                            list.removeAt(index_remove.toInt())
                        }
                        list.forEach {
                            //融云刷新用户信息
                            RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                    (it as CommonData).accountInfoId,
                                    it.userName,
                                    Uri.parse(BaseHttp.baseImg + it.userhead)))
                        }

                        list.add(CommonData().apply { userhead = "添加" })
                        if(creatorId==getString("token")){//只有创建者显示减号
                            list.add(CommonData().apply { userhead = "删除" })
                        }
                        (group_list.adapter as    SlimAdapter).updateData(list).notifyDataSetChanged()
                    }
                })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this@ConversationGroupActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "添加成员" -> getMemebers()
            "移除成员" -> getMemebers()
        }
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("聊天信息（群聊）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("聊天信息（群聊）")
    }
}
