package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import com.lzy.extend.StringDialogUncheckCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.share.BaseHttp
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_circle_detail.*
import kotlinx.android.synthetic.main.layout_title_left.*

class CircleDetailActivity : BaseActivity() {

    private var companyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circle_detail)
        setToolbarVisibility(false)
        init_title()

        circle_web.loadUrl( "${BaseHttp.financial_details}?financialId=${intent.getStringExtra("financialId")}")
        companyId = intent.getStringExtra("companyId")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        left_nav_title.text = "详情"

        circle_name.text = intent.getStringExtra("compName")
        GlideApp.with(baseContext)
                .load(BaseHttp.baseImg + intent.getStringExtra("compLogo"))
                .placeholder(R.mipmap.default_logo) // 等待时的图片
                .error(R.mipmap.default_logo)       // 加载失败的图片
                .centerCrop()
                .dontAnimate()
                .into(circle_logo)

        circle_web.apply {
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

        circle_company.setOnClickListener {
            if (companyId.isNotEmpty()) {
                val intent = Intent(baseContext, EnterpriseDetailActivity::class.java)
                intent.putExtra("companyId", companyId)
                startActivity(intent)
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.financial_details)
                .tag(this@CircleDetailActivity)
                .params("financialId", intent.getStringExtra("financialId"))
                .execute(object : StringDialogUncheckCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>) {

                        circle_web.loadDataWithBaseURL(BaseHttp.baseImg, response.body(), "text/html", "utf-8", "")
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("金融圈详情")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("金融圈详情")
    }
}
