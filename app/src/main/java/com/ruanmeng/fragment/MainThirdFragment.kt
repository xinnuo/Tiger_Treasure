package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.tiger_treasure.*
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imkit.manager.IUnReadMessageObserver
import io.rong.imlib.model.CSCustomServiceInfo
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.fragment_main_third.*
import kotlinx.android.synthetic.main.fragment_main_third.view.*
import kotlinx.android.synthetic.main.layout_title_blue.*
import org.json.JSONArray
import org.json.JSONObject
import q.rorbin.badgeview.QBadgeView

class MainThirdFragment : BaseFragment() {
    //调用这个方法切换时不会释放掉Fragment
    val list = java.util.ArrayList<CommonData>()
    private var badgeView: QBadgeView? = null
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null) {
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_main_third, container, false)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()


    }

    override fun init_title() {
        iv_nav_back.visibility = View.INVISIBLE
        blue_nav_title.text = "个人中心"

        third_info.setOnClickListener {
            val intent = Intent(activity, InfoActivity::class.java)
            intent.putExtra("interestList", list)
            startActivity(intent)
        }
        third_collect.setOnClickListener { startActivity(CollectActivity::class.java) }
        third_message.setOnClickListener { startActivity(MessageActivity::class.java) }
        third_step.setOnClickListener { startActivity(StepActivity::class.java) }
        third_manager.setOnClickListener { startActivity(ManagerActivity::class.java) }
        third_gong.setOnClickListener {
            if (getString("isGov") != "1") {
                val intent = Intent(activity, CompanyModifyActivity::class.java)
                startActivity(intent)
            } else {
                startActivity(GovernmentModifyActivity::class.java)
            }
        }

        third_hui.setOnClickListener {
            startActivity(VipActivity::class.java) }
        third_chan.setOnClickListener {
            startActivity(ProductMineActivity::class.java)
        }
        third_xun.setOnClickListener { startActivity(ProductMineActivity::class.java) }//BusinessActivity
        third_scan.setOnClickListener { startActivity(ScanActivity::class.java) }
        third_setting.setOnClickListener { startActivity(SettingActivity::class.java) }
        third_customer.setOnClickListener {
            //客服
            val csBuilder = CSCustomServiceInfo.Builder()
            val csInfo = csBuilder.nickName(getString("userName")).build()
            RongIM.getInstance().startCustomerServiceChat(this.activity, "KEFU151427735680534", "客服", csInfo)
        }
        third_jie.setOnClickListener {
            val intent = Intent(activity, WebActivity::class.java)
            intent.putExtra("title", "平台介绍")
            startActivity(intent)
        }

        third_watch.setOnClickListener {
            val intent = Intent(activity, WatchingActivity::class.java)
            intent.putExtra("title", "我的关注")
            startActivity(intent)
        }
        third_watched.setOnClickListener {
            val intent = Intent(activity, WatchingActivity::class.java)
            intent.putExtra("title", "关注我的")
            startActivity(intent)
        }

        third_tel.setOnClickListener {
            if (getString("compTel").isNotEmpty()) {
                DialogHelper.showDialog(activity, "企业电话", "拨打企业电话 " + getString("compTel"), "取消", "呼叫") {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString("compTel")))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }

        if (getString("accountType") == "App_Manager") {
            third_watching.visibility = View.GONE
            third_manager.visibility = View.GONE
            third_xun.visibility = View.VISIBLE
            third_scan.visibility = View.GONE

            //如果是管家，监听企业业务咨询消息数，显示红点
            RongIM.getInstance().addUnReadMessageCountChangedObserver(IUnReadMessageObserver { count ->
                if (count > 0) {
                    if (badgeView == null) badgeView = QBadgeView(activity)

                    badgeView!!.apply {
                        bindTarget(zixuntext)
                        badgeNumber = count
                        setBadgeTextSize(10f, true)
                        setOnDragStateChangedListener { dragState, badge, targetView -> }
                    }
                    badgeView!!.visibility = View.VISIBLE
                } else {
                    if (badgeView != null) {
                        badgeView!!.visibility = View.GONE
                    }
                }
            }, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.PRIVATE)
        }

        if (getString("isGov") == "1") {//政府号
            third_hui.visibility = View.GONE
            third_vip.visibility = View.GONE
            third_date.visibility = View.GONE
        }

    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.accountinfo_details)
                .tag(this@MainThirdFragment)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(activity, false) {
                    @SuppressLint("SetTextI18n") override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body()).getJSONObject("object")
                        list.clear()
                        val arr = JSONArray(obj.getString("chartRooms"))
                        (0 until arr.length()).mapTo(list) {
                            CommonData().apply {
                                chatRoomId = arr.getJSONObject(it).getString("chatRoomId")
                                chatRoomName = arr.getJSONObject(it).getString("chatRoomName")
                            }
                        }
                        putString("sex", obj.getString("sex"))
                        putString("vipTypeName", obj.getString("vipTypeName"))
                        putString("expiryDate", obj.getString("expiryDate"))
                        putString("scNum", obj.getString("scNum"))
                        putString("msgNum", obj.getString("msgNum"))
                        putString("footPrintNum", obj.getString("footPrintNum"))
                        putString("userhead", obj.getString("userhead"))
                        putString("userName", obj.getString("userName"))
                        putString("age", obj.getString("age"))
                        putString("positionName", obj.getString("positionName"))
                        putString("compTel", obj.getString("telephone"))
                        putString("telephoneIsOpen", obj.getString("telephoneIsOpen"))
                        if (third_img.getTag(R.id.third_img) == null) {
                            GlideApp.with(activity).load(BaseHttp.baseImg + getString("userhead")).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).dontAnimate().into(third_img)
                            third_img.setTag(R.id.third_img, getString("userhead"))
                        } else {
                            if (third_img.getTag(R.id.third_img) != getString("userhead")) {
                                GlideApp.with(activity).load(BaseHttp.baseImg + getString("userhead")).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).dontAnimate().into(third_img)
                                third_img.setTag(R.id.third_img, getString("userhead"))
                            }
                        }
                        third_name.text = getString("userName")
                        third_job.text = getString("positionName")
                        third_gender.setImageResource(when (getString("sex")) {
                            "0" -> R.mipmap.icon_sex_woman
                            else -> R.mipmap.icon_sex_man
                        })
                        if (getString("vipTypeName").isNotEmpty()) {
                            third_vip.visibility = View.VISIBLE
                            third_date.visibility = View.VISIBLE
                            third_vip.text = getString("vipTypeName")
                            third_date.text = "会员到期日：" + getString("expiryDate")
                        } else {
                            third_vip.visibility = View.INVISIBLE
                            third_date.visibility = View.INVISIBLE
                        }
                        third_collect.text = "收藏 ${getString("scNum")}"
                        third_message.text = "消息 ${getString("msgNum")}"
                        third_step.text = "足迹 ${getString("footPrintNum")}"
                        third_tel.setRightString(getString("compTel"))

                        if (getString("isGov") == "1") {//政府号
                            third_gong.setLeftString("我的单位")
                            third_hui.visibility = View.GONE
                            third_vip.visibility = View.GONE
                            third_date.visibility = View.GONE
                        }
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("个人中心")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("个人中心")
    }
}
