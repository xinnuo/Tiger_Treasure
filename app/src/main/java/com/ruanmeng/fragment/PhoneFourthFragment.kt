package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.tiger_treasure.R
import com.umeng.analytics.MobclickAgent

class PhoneFourthFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_phone_fourth, container, false)

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("更换手机号（4）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("更换手机号（4）")
    }
}
