package com.ruanmeng.tiger_treasure

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.luck.picture.lib.decoration.RecycleViewDivider
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.StewardAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.toast
import com.ruanmeng.model.StewardData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_enterprise_info.*
import java.io.File

class EnterpriseInfoActivity : BaseActivity(), StewardAdapter.OnEdittextFillListener {

    override fun onEdittextFill(position: Int, type: String?, text: String) {
        if (position < stewardList.size) {
            when (type) {
                "name" -> stewardList.get(position).name = text
                "phone" -> stewardList.get(position).phone = text
                "title" -> stewardList.get(position).title = text
            }

        }
    }


    private var stewardList = ArrayList<StewardData>()
    private var adapter = StewardAdapter(this, stewardList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise_info)
        init_title("个人资料认证")
        initAdapter()
    }

    override fun init_title() {
        super.init_title()

        bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_submit.isClickable = false

        et_name.addTextChangedListener(this)
        et_phone.addTextChangedListener(this)
//        et_mastername.addTextChangedListener(this)
//        et_masterphone.addTextChangedListener(this)
//        et_masterjob.addTextChangedListener(this)
    }

    fun initAdapter() {
        recycle_steward.load_Linear(this, null)
        recycle_steward.adapter = adapter
        recycle_steward.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, resources.getColor(R.color.divider)))
        adapter.setEdittextFillListener(this)

    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_submit -> {
                if (!CommonUtil.isMobileNumber(et_phone.text.toString())) {
                    et_phone.setText("")
                    et_phone.requestFocus()
                    toast("手机号格式不正确，请重新输入")
                    return
                }
//                if (IsMaster == 1 && !CommonUtil.isMobileNumber(et_masterphone.text.toString())) {
//                    et_phone.setText("")
//                    et_phone.requestFocus()
//                    toast("手机号格式不正确，请重新输入")
//                    return
//                }

                OkGo.post<String>(BaseHttp.company_reg)
                        .tag(this@EnterpriseInfoActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("compName", intent.getStringExtra("compName"))
                        .params("compCode", intent.getStringExtra("compCode"))
                        .params("legalMan", intent.getStringExtra("legalMan"))
                        .params("industryId", intent.getStringExtra("industryId"))
                        .params("compType", intent.getStringExtra("compType"))//企业性质
                        .apply {
                            if (intent.getStringExtra("compLogo_img").isNotEmpty()) {
                                this.params("compLogo_file", File(intent.getStringExtra("compLogo_img")))
                            }
                            if (intent.getStringExtra("registerDate").isNotEmpty()) params("registerDate", intent.getStringExtra("registerDate"))
                            if (intent.getStringExtra("staffNum").isNotEmpty()) params("staffNum", intent.getStringExtra("staffNum"))
                            if (intent.getStringExtra("regCap").isNotEmpty()) params("regCap", intent.getStringExtra("regCap"))
                            if (intent.getStringExtra("compUrl").isNotEmpty()) params("compUrl", intent.getStringExtra("compUrl"))
                            if (intent.getStringExtra("compEmail").isNotEmpty()) params("compEmail", intent.getStringExtra("compEmail"))
                            if (intent.getStringExtra("compTel").isNotEmpty()) params("compTel", intent.getStringExtra("compTel"))
                            if (intent.getStringExtra("copmImage_file").isNotEmpty()) params("copmImage_file", intent.getStringExtra("copmImage_file"))
                            if (intent.getStringExtra("compService").isNotEmpty()) params("compService", intent.getStringExtra("compService"))
                        }

                        .params("copmLic_file", File(intent.getStringExtra("compLic_img")))
                        .params("areaId", intent.getStringExtra("areaId"))
                        .params("address", intent.getStringExtra("address"))
                        .params("compLat", intent.getStringExtra("compLat"))
                        .params("compLng", intent.getStringExtra("compLng"))
                        .params("copmProfile", intent.getStringExtra("compProfile"))
//                        .params("userName", et_name.text.trim().toString())
                        .params("telephone", et_phone.text.trim().toString())
                        .params("enterpriseName", et_name.text.trim().toString())//企业家姓名
                        .apply {
                            if (stewardList.isNotEmpty()) {
                                var managerName: String = ""
                                var accountName: String = ""
                                var positionName: String = ""
                                for (item in stewardList) {
                                    managerName += item.name + ","
                                    accountName += item.phone + ","
                                    positionName += item.title + ","
                                }
                                Log.e("管家姓名", managerName.substring(0, managerName.length - 1))
                                Log.e("管家手机号", accountName.substring(0, accountName.length - 1))
                                Log.e("管家职务", positionName.substring(0, positionName.length - 1))
                                this.params("managerName", managerName.substring(0, managerName.length - 1))
                                this.params("accountName", accountName.substring(0, accountName.length - 1))
                                this.params("positionName", positionName.substring(0, positionName.length - 1))
                            }
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast("提交成功")
                                MobclickAgent.onEvent(this@EnterpriseInfoActivity, "register_success")

                                intent.setClass(baseContext, EnterpriseHintActivity::class.java)
                                intent.putExtra("info", msg)
                                intent.putExtra("title", "企业资料认证")
                                startActivity(intent)

                                ActivityStack.getScreenManager().popActivities(
                                        this@EnterpriseInfoActivity::class.java,
                                        EnterpriseActivity::class.java,
                                        CompanyInputActivity::class.java)
                            }

                            override fun onError(response: Response<String>?) {
                                super.onError(response)
                                MobclickAgent.onEvent(this@EnterpriseInfoActivity, "register_fail")
                            }

                        })
            }
            R.id.li_setmaster -> {//是否设置管家
//                li_master.toggle()
//                    if (!cb_setmaster.isChecked) {
//                    IsMaster = 1
//                    cb_setmaster.isChecked = true
//                    if (et_name.text.isNotBlank()
//                            && et_phone.text.isNotBlank() && et_mastername.text.isNotBlank() && et_masterjob.text.isNotBlank() && et_masterphone.text.isNotBlank()) {
//                        bt_submit.setBackgroundResource(R.drawable.rec_bg_blue)
//                        bt_submit.isClickable = true
//                    } else {
//                        bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//                        bt_submit.isClickable = false
//                    }
//                } else {
//                    IsMaster = 0
//                    cb_setmaster.isChecked = false
//                    if (et_name.text.isNotBlank()
//                            && et_phone.text.isNotBlank()) {
//                        bt_submit.setBackgroundResource(R.drawable.rec_bg_blue)
//                        bt_submit.isClickable = true
//                    } else {
//                        bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
//                        bt_submit.isClickable = false
//                    }
//                }
            }
            R.id.add_steward -> {
                if (stewardList.isEmpty() || stewardList[stewardList.size - 1].name.isNotEmpty()
                        && stewardList[stewardList.size - 1].phone.isNotEmpty()
                        && stewardList[stewardList.size - 1].title.isNotEmpty()) {
                    stewardList.add(StewardData())
                    adapter.notifyDataSetChanged()
                } else {
                    toast("请完善信息")
                }
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_phone.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_submit.isClickable = false
        }

    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业认证（填写企业家、管家）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业认证（填写企业家、管家）")
    }
}
