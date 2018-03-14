package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_member_message.*

class FeedBackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_message)
        init_title("意见反馈", "联系平台")
        init_title()
    }

    override fun init_title() {
        super.init_title()
        val drawable = resources.getDrawable(R.mipmap.icon_call)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(drawable, null, null, null)
        tvRight.setTextColor(resources.getColor(R.color.colorAccent))
        tvRight.setOnClickListener {
            if (getString("platform").isNotEmpty()) {
                DialogHelper.showDialog(
                        baseContext,
                        "平台电话",
                        "拨打平台电话 " + getString("platform"),
                        "取消",
                        "呼叫") {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString("platform")))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
        bt_done.setOnClickListener {
            if (et_content.text.isEmpty()) {
                toast("请输入您的留言！")
                return@setOnClickListener
            }
            getData()
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.feedback)
                .tag(this@FeedBackActivity)
                .headers("token", getString("token"))
                .params("content", et_content.text.toString())
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        toast(msg)
                        ActivityStack.getScreenManager().popActivities(this@FeedBackActivity::class.java)
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("意见反馈")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("意见反馈")
    }
}
