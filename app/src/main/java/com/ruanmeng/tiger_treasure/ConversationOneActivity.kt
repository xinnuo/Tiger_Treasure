package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import io.rong.eventbus.EventBus
import io.rong.imkit.RongIM
import io.rong.imkit.model.Event
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_conversation_one.*
import kotlinx.android.synthetic.main.layout_title_left.*
import java.util.*

class ConversationOneActivity : BaseActivity() {

    private var mTargetId = ""
    private var mTitle = ""
    private lateinit var mConversationType: Conversation.ConversationType

    private var isTop = false
    private var mStatus = Conversation.ConversationNotificationStatus.NOTIFY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_one)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    override fun init_title() {
        super.init_title()
        left_nav_title.text = "聊天信息"

        mTargetId = intent.data.getQueryParameter("targetId")
        mTitle = intent.data.getQueryParameter("title")
        mConversationType = Conversation.ConversationType.valueOf(intent.data.lastPathSegment.toUpperCase(Locale.US))
        conversation_name.text = mTitle

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
                            ActivityStack.getScreenManager().popActivities(ConversationActivity::class.java)
                            toast("已清空聊天记录")
                        }

                        override fun onError(errorCode: RongIMClient.ErrorCode) {
                            OkLogger.e("清空聊天记录失败，错误码：" + errorCode.message)
                        }
                    })
        }

        conversation_img.setOnClickListener {
            val intent = Intent(baseContext, PersonActivity::class.java)
            intent.putExtra("accountInfoId", mTargetId)
            startActivity(intent)
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.chatroom_users)
                .tag(this@ConversationOneActivity)
                .headers("token", getString("token"))
                .params("userIds", mTargetId)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {

                        val data = response.body().`object`[0]

                        //融云刷新用户信息
                        RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                data.accountInfoId,
                                data.userName,
                                Uri.parse(BaseHttp.baseImg + data.userhead)))

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.userhead)
                                .placeholder(R.mipmap.default_user) //等待时的图片
                                .error(R.mipmap.default_user)       //加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(conversation_img)
                    }
                })
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("聊天信息（单聊）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("聊天信息（单聊）")
    }
}
