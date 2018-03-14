package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.model.TicketMessageEvent
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import kotlinx.android.synthetic.main.activity_modify_name.*
import org.greenrobot.eventbus.EventBus

class ChangeGroupNameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_group_name)
        init_title("修改群名称")
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "确定"
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))
        btRight.setOnClickListener {
            if (et_name.text.isBlank()) {
                toast("请输入姓名")
                return@setOnClickListener
            }
            RongIM.getInstance().setDiscussionName(intent.getStringExtra("discussionId"), et_name.text.toString(), object : RongIMClient.OperationCallback() {

                override fun onSuccess() {
                    toast("您已成功设置群名片")
                    EventBus.getDefault().post(TicketMessageEvent(et_name.text.toString(), "更改讨论组名称"))
                    ActivityStack.getScreenManager().popActivities(this@ChangeGroupNameActivity::class.java)
                }
                override fun onError(errorCode: RongIMClient.ErrorCode) {
                    OkLogger.e("设置群名片失败，错误码：" + errorCode.message)
                }

            })
        }

    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("修改群名称")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("修改群名称")
    }

}