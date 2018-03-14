package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.tiger_treasure.R
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_phone_second.*

class PhoneSecondFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_phone_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_phone.addTextChangedListener(this)
        bt_next.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_next.isClickable = false
        fragment_phone_second.setOnClickListener {  }
    }

    fun getMobile() : String = et_phone.text.toString()

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_phone.text.isNotEmpty()) {
            bt_next.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_next.isClickable = true
        } else {
            bt_next.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_next.isClickable = false
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更换手机号（2）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更换手机号（2）")
    }
}
