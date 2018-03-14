package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.fragment.PhoneFirstFragment
import com.ruanmeng.fragment.PhoneFourthFragment
import com.ruanmeng.fragment.PhoneSecondFragment
import com.ruanmeng.fragment.PhoneThirdFragment
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import org.json.JSONObject

class ModifyPhoneActivity : BaseActivity() {

    private lateinit var first: PhoneFirstFragment
    private lateinit var second: PhoneSecondFragment
    private lateinit var third: PhoneThirdFragment
    private lateinit var fourth: PhoneFourthFragment

    private var YZM = ""
    private var mTel = ""
    private var isDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_phone)
        init_title("手机号")

        first = PhoneFirstFragment()
        second = PhoneSecondFragment()
        third = PhoneThirdFragment()
        fourth = PhoneFourthFragment()

        supportFragmentManager
                .beginTransaction()
                .add(R.id.phone_container, first)
                .commit()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_change -> {
                tvTitle.text = "更换手机号"

                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.push_left_in,
                                R.anim.push_left_out,
                                R.anim.push_right_in,
                                R.anim.push_right_out)
                        .add(R.id.phone_container, second)
                        .commit()
            }
            R.id.bt_next -> {
                if (!CommonUtil.isMobileNumber(second.getMobile())) {
                    toast("手机号码格式错误，请重新输入")
                    return
                }

                OkGo.post<String>(BaseHttp.get_smscode)
                        .tag(this@ModifyPhoneActivity)
                        .params("mobile", second.getMobile())
                        .params("accountType", getString("accountType"))
                        .params("smsKey", "hOWt3hiakXHrePCqDKUsPz5T6f7j8P")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = second.getMobile()

                                supportFragmentManager
                                        .beginTransaction()
                                        .setCustomAnimations(
                                                R.anim.push_left_in,
                                                R.anim.push_left_out,
                                                R.anim.push_right_in,
                                                R.anim.push_right_out)
                                        .add(R.id.phone_container, third)
                                        .commit()
                            }

                        })
            }
            R.id.bt_back -> aonBackPressed()
        }
    }

    fun getMobile() = mTel
    fun getYZM() = YZM
    fun getDone() {
        isDone = true
    }
    fun changeResult() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.push_left_in,
                        R.anim.push_left_out,
                        R.anim.push_right_in,
                        R.anim.push_right_out)
                .add(R.id.phone_container, fourth)
                .commit()
    }

     fun aonBackPressed() {
        putBoolean("isLogin", false)
        putString("token", "")
        putString("accountType", "")
        putString("hasCompany", "")

        val intent = Intent(baseContext, LoginActivity::class.java)
        intent.putExtra("offLine", true)
        startActivity(intent)

        ActivityStack.getScreenManager().popAllActivityExcept(LoginActivity::class.java)

        super.onBackPressed()
    }
}
