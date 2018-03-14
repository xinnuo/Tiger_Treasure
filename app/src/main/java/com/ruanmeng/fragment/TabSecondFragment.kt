package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.startChatRoomChat
import com.ruanmeng.tiger_treasure.R
import com.ruanmeng.view.RoundAngleImageView
import io.rong.imkit.RongIM
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class TabSecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tab_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(activity, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_second_list) { data, injector ->
                    injector.invisible(R.id.item_second_count)
                            .invisible(R.id.item_second_hint)
                            .invisible(R.id.item_second_time)
                            .text(R.id.item_second_name, data.chatRoomName)

                            .with<RoundAngleImageView>(R.id.item_second_img) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.chatRoomImage)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_second) {
                                /**
                                 * <p>启动聊天室会话。</p>
                                 * <p>设置参数 createIfNotExist 为 true，对应到 kit 中调用的接口是
                                 * {@link RongIMClient#joinChatRoom(String, int, RongIMClient.OperationCallback)}.
                                 * 如果聊天室不存在，则自动创建并加入，如果回调失败，则弹出 warning。</p>
                                 * <p>设置参数 createIfNotExist 为 false，对应到 kit 中调用的接口是
                                 * {@link RongIMClient#joinExistChatRoom(String, int, RongIMClient.OperationCallback)}.
                                 * 如果聊天室不存在，则返回错误 {@link io.rong.imlib.RongIMClient.ErrorCode#RC_CHATROOM_NOT_EXIST}，并且会话界面会弹出 warning.
                                 * </p>
                                 *
                                 * @param context          应用上下文。
                                 * @param chatRoomId       聊天室 id。
                                 * @param chatRoomName     聊天室 name。
                                 * @param createIfNotExist 如果聊天室不存在，是否创建。
                                 */
                                RongIM.getInstance().startChatRoomChat(activity, data.chatRoomId, data.chatRoomName,true)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.chatroom_mine)
                .tag(this@TabSecondFragment)
                .headers("token", getString("token"))
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(activity) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        list.clear()
                        list.addItems(response.body().`object`)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }
                })
    }
}
