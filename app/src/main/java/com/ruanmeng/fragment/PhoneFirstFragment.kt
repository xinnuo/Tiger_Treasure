package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.getString
import com.ruanmeng.tiger_treasure.R
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_phone_first.*

class PhoneFirstFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_phone_first, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_phone.text = getString("telephone")
        bt_change.text = "下一步"
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更换手机号（1）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更换手机号（1）")
    }
}
