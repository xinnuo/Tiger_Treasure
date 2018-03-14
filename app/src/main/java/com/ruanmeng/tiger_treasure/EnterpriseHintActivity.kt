package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_enterprise_hint.*
import kotlinx.android.synthetic.main.layout_header.*

class EnterpriseHintActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_hint)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()
        divider.visibility = View.GONE
        enterprise_hint.text = intent.getStringExtra("info")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_done -> ActivityStack.getScreenManager().popAllActivityExcept(LoginActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业认证（提示审核）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业认证（提示审核）")
    }
}
