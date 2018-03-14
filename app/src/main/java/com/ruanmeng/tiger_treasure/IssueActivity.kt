package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity

class IssueActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)
        init_title("发布信息")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_gong -> startActivity(IssueFirstActivity::class.java)
            R.id.bt_buy -> startActivity(IssueSecondActivity::class.java)
            R.id.bt_he -> startActivity(IssueThirdActivity::class.java)
        }
    }
}
