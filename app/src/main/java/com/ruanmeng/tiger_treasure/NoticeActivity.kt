package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.utils.DialogHelper

class NoticeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        init_title("公告", "联系平台")
    }

    @Suppress("DEPRECATION")
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


    }
}
