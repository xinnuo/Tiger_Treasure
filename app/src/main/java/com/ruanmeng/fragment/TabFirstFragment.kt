package com.ruanmeng.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.tiger_treasure.R
import io.rong.imkit.fragment.ConversationListFragment
import io.rong.imlib.model.Conversation

class TabFirstFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_tab_first, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enterFragment()
    }

    /**
     * 加载 会话列表 ConversationListFragment
     *
     * 1、rc_ext_extension_bar.xml            输入框布局文件。它是整个输入框的容器，内部有对各部分组件功能描述。
     * 2、rc_ext_input_edit_text.xml EditText 布局文件。如果想要替换背景，直接修改即可。
     * 3、rc_ext_voice_input.xml              语音输入布局文件。
     * 4、rc_fr_conversation.xml              app:RCStyle="SCE" ，更改默认输入显示形式
     * 5、rc_fr_conversationlist.xml          修改空布局位置。
     */
    private fun enterFragment() {
        val fragment = childFragmentManager.findFragmentById(R.id.conversationlist) as ConversationListFragment
        val uri = Uri.parse("rong://" + activity.applicationInfo.packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")    //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")       //设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false") //设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")     //设置系统会话非聚合显示
                .build()
        fragment.uri = uri
    }
}
