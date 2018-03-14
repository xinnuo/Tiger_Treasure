package com.ruanmeng.tiger_treasure

import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_chan_psw.*

class ChangePswActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chan_psw)
        init_title("修改密码")
    }

    override fun init_title() {
//        bt_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//        bt_sure.isClickable = false

        et_oldpsw.addTextChangedListener(this)
        et_newpsw.addTextChangedListener(this)
        et_renewpsw.addTextChangedListener(this)
        bt_sure.setOnClickListener {
            if(et_oldpsw.text.toString().isEmpty()){
                toast("请填写旧密码！")
                return@setOnClickListener
            }
            if(et_newpsw.text.toString().isEmpty()){
                toast("请填写新密码！")
                return@setOnClickListener
            }
            if(et_renewpsw.text.toString().isEmpty()){
                toast("请重复输入新密码！")
                return@setOnClickListener
            }
            if (et_newpsw.text.toString() != et_renewpsw.text.toString()) {
                toast("两次密码输入不一致！")
                return@setOnClickListener
            }
            getData()
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.Changepsw)
                .tag(this@ChangePswActivity)
                .headers("token", getString("token"))
                .params("oldpwd", et_oldpsw.text.toString())
                .params("newpwd", et_newpsw.text.toString())
                .params("repeatpwd", et_renewpsw.text.toString())
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        toast(msg)
                        ActivityStack.getScreenManager().popActivities(this@ChangePswActivity::class.java)
                    }
                })
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        if (et_oldpsw.text.isNotBlank()
//                && et_newpsw.text.isNotBlank() && et_renewpsw.text.isNotBlank() && et_newpsw.text.length >= 6 && et_renewpsw.text.length >= 6) {
//            bt_sure.setBackgroundResource(R.drawable.rec_bg_blue)
//            bt_sure.isClickable = true
//        } else {
//            bt_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//            bt_sure.isClickable = false
//        }
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("修改密码")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("修改密码")
    }
}
