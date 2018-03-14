package com.ruanmeng.tiger_treasure

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.layout_title_left.*

class BusinessReplyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_reply)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        super.init_title()
        left_nav_title.text = "回复咨询"
    }
}
