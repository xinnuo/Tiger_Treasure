package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity

class WatchMemoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_memo)
        init_title("修改备注名")
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "保存"
    }
}
