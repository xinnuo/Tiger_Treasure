package com.ruanmeng.tiger_treasure

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import com.ruanmeng.adapter.GuideAdapter
import com.ruanmeng.base.*
import com.umeng.analytics.MobclickAgent
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : BaseActivity() {

    private var isReady: Boolean = false

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (isReady) {
                //关于首次进入引导页
                if (!getBoolean("isFirst")) startGuide()
                else {
                    startActivity(if (getBoolean("isLogin")) MainActivity::class.java else LoginActivity::class.java)
                    onBackPressed()
                }
            } else {
                isReady = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隐藏状态栏（全屏）
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //取消全屏
        // window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_guide)
        transparentStatusBar(false)

        window.decorView.postDelayed({ handler.sendEmptyMessage(0) }, 2000)

        AndPermission.with(this@GuideActivity)
                .permission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(object : PermissionListener {
                    override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                        handler.sendEmptyMessage(0)
                    }

                    override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                        toast("请求权限被拒绝")
                        onBackPressed()
                    }
                })
                .start()
        //关于首次进入引导页
        guide_in.setOnClickListener {
            putBoolean("isFirst", true)

            startActivity(if (getBoolean("isLogin")) MainActivity::class.java else LoginActivity::class.java)
            onBackPressed()
        }
    }

    //关于首次进入引导页
    private fun startGuide() {
        val mLoopAdapter = GuideAdapter(baseContext)
        guide_banner.apply {
            setAdapter(mLoopAdapter)
            setHintViewVisibility(false)
            setOnItemSelectListener { position ->
                guide_in.visibility = if (position == 2) View.VISIBLE else View.INVISIBLE
            }
        }

        val imgs = listOf(R.mipmap.guide_01, R.mipmap.guide_02, R.mipmap.guide_03)
        mLoopAdapter.setImgs(imgs)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("启动页")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("启动页")
    }
}