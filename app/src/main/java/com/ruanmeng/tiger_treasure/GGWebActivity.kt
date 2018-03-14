package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_web.*

class GGWebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        changeLeftTitle(intent.getStringExtra("title"), "联系平台")
        init_title()
        when (intent.getStringExtra("title")) {
            "关于我们" -> wv_web.loadUrl("${BaseHttp.html}?type=platform")
            "服务协议" -> wv_web.loadUrl("${BaseHttp.html}?type=service")
            "会员协议" -> wv_web.loadUrl("${BaseHttp.html}?type=vip")
            "帮助与反馈" -> wv_web.loadUrl("${BaseHttp.html}?type=help")
            "帮助中心" -> wv_web.loadUrl("${BaseHttp.html}?type=help")
            "免责声明" -> wv_web.loadUrl("${BaseHttp.html}?type=disclaimer")
            "隐私政策" -> wv_web.loadUrl("${BaseHttp.html}?type=privacy")
            "详情" -> wv_web.loadUrl("${BaseHttp.message_details}?messageInfoId=${intent.getStringExtra("messageInfoId")}&messageId=${intent.getStringExtra("messageId")}")
            else -> wv_web.loadUrl(intent.getStringExtra("url"))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
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
        wv_web.apply {
            //支持javascript
            settings.javaScriptEnabled = true
            // 设置可以支持缩放
            settings.setSupportZoom(true)
            // 自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
//        EventBus.getDefault().post(CityNewEvent("", ""))
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(intent.getStringExtra("title"))
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(intent.getStringExtra("title"))
    }
}
