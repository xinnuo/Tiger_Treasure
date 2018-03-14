package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_forget_psw.*

class ForgetPswActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_psw)
        init_title("忘记密码")
    }

    override fun init_title() {
        super.init_title()
        bt_fsure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_fsure.isClickable = false
        et_fpsw.addTextChangedListener(this)
        et_sfpsw.addTextChangedListener(this)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id){
            R.id.bt_fsure->{
                if(et_fpsw.text.toString()!= et_sfpsw.text.toString()){
                    toast("两次密码输入不一致")
                    return
                }
                OkGo.post<String>(BaseHttp.Forgetpsw)
                        .tag(this@ForgetPswActivity)
                        .params("mobile",intent.getStringExtra("mTel"))
                        .params("smscode", intent.getStringExtra("mYzm"))
                        .params("newpwd", et_sfpsw.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                ActivityStack.getScreenManager().popActivities(
                                        this@ForgetPswActivity::class.java,
                                        GetYZMActivity::class.java)
                            }

                        })

            }
        }
    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_fpsw.text.isNotBlank()
                && et_sfpsw.text.isNotBlank()&&et_fpsw.text.toString().length>=6&&et_sfpsw.text.toString().length>=6) {
            bt_fsure.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_fsure.isClickable = true
        } else {
            bt_fsure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_fsure.isClickable = false
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("忘记密码（输入新密码）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("忘记密码（输入新密码）")
    }
}
