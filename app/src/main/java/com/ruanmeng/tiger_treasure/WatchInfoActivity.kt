package com.ruanmeng.tiger_treasure

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_watch_info.*
import kotlinx.android.synthetic.main.layout_title_left.*

class WatchInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_info)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "查看资料"

        watch_memo.setOnClickListener { startActivity(WatchMemoActivity::class.java) }
    }
}
