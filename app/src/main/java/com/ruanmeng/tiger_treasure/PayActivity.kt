package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.cuieney.rxpay_annotation.WX
import com.cuieney.sdk.rxpay.RxPay
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.*
import com.ruanmeng.model.TicketMessageEvent
import com.ruanmeng.model.VipPayData
import com.ruanmeng.model.VipPriceData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_pay.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@WX(packageName = "com.ruanmeng.tiger_treasure")
class PayActivity : BaseActivity() {

    private val list = ArrayList<VipPriceData>()
    private var invoiceId = ""
    private var vippriceId = ""
    private var index = ""
    private var time = ""
    private var data = VipPayData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        init_title("选择支付方式")
        getData()

        EventBus.getDefault().register(this@PayActivity)

//        @Suppress("UNCHECKED_CAST")
//        val items = intent.getSerializableExtra("items") as ArrayList<VipPriceData>
//        list.addAll(items)
//        (recycle_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
        //只有一个的时候默认选中  不可取消
//        if (list.size == 1) {
//            list[0].isChecked = true
//            vippriceId = list[0].vippriceId
//            time = list[0].priceUnit
//            pay_total.text = list[0].price
//            pay_name.text = intent.getStringExtra("title") + "(${list.get(0).price}元/年)"
//        }

    }

    override fun init_title() {
        super.init_title()

//        recycle_list.apply {
//            layoutManager = FullyLinearLayoutManager(baseContext)
//            adapter = SlimAdapter.create()
//                    .register<VipPriceData>(R.layout.item_pay_list) { data, injector ->
//                        injector.text(R.id.item_pay_time, data.priceUnit)
//                                .text(R.id.item_pay_price, data.price)
//
//                                .visibility(R.id.item_pay_divider, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
//
//                                .with<CheckBox>(R.id.item_pay_check, { view ->
//                                    view.isChecked = data.isChecked
//                                })
//
//                                .clicked(R.id.item_pay) {
//                                    //只有一个的时候默认选中  不可取消
//                                    if(list.size>1){
//                                        list.forEach { it.isChecked = false }
//                                        data.isChecked = true
//                                        time=data.priceUnit
//                                        vippriceId = data.vippriceId
//                                        pay_total.text = data.price
//                                        (recycle_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
//                                    }
//                                }
//                    }
//                    .attachTo(this@apply)
//        }

        pay_switch.setOnCheckedChangeListener { _, isChecked ->
            expand_ticket.toggle()
            if (!isChecked) invoiceId = ""
        }

        pay_ticket.setOnClickListener { startActivity(TicketActivity::class.java) }

        pay_deal.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "会员协议")
            startActivity(intent)
        }

        pay_sure.setOnClickListener {
            //            if (vippriceId.isEmpty()) {
//                toast("请选择会员类型")
//                return@setOnClickListener
//            }

            if (pay_switch.isChecked && invoiceId.isEmpty()) {
                toast("请选择发票")
                return@setOnClickListener
            }

            if (pay_group.checkedRadioButtonId == -1) {
                toast("请选择支付方式")
                return@setOnClickListener
            }

            if (pay_wx.isChecked) {
                OkGo.post<String>(BaseHttp.pay_sub)
                        .tag(this@PayActivity)
                        .headers("token", getString("token"))
                        .params("expirDate", data.expirDate)
                        .params("payMoney", data.payMoney)
                        .params("vipTypeId", data.vipTypeId)
                        .params("busType", "VIP")
                        .params("payType", "WxPay")
                        .params("invoiceId", invoiceId)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getJSONObject("object")

                                RxPay(this@PayActivity).requestWXpay(obj)
                                        .subscribe({ isDone ->
                                            if (isDone) {
                                                ShowTiShi()
                                                //更新会员级别
                                                putString("vipCode", data.vipTypeId)
                                                if (data.vipTypeId.equals("VIP_DEFAULT")) {
                                                    MobclickAgent.onEvent(this@PayActivity, "open_vip_success")
                                                } else if (data.vipTypeId.equals("VIP_SLIVER")) {
                                                    MobclickAgent.onEvent(this@PayActivity, "open_vip2_success")
                                                } else if (data.vipTypeId.equals("VIP_GOLD")) {
                                                    MobclickAgent.onEvent(this@PayActivity, "open_vip3_success")
                                                }
                                                var payMah = HashMap<String, String>()
                                                payMah.put("金额", data.payMoney)
                                                payMah.put("所属企业", "暂未添加")
                                                payMah.put("付款人", getString("userName"))
                                                payMah.put("手机号", getString("telephone"))
                                                payMah.put("日期", SimpleDateFormat("yyyy-MM-dd").format(Date()))
                                                MobclickAgent.onEvent(this@PayActivity, "wxpay_success", payMah)
                                                // EventBus.getDefault().post(RefreshMessageEvent(intent.getIntExtra("positon", -1), "开通会员"))

//                                                ActivityStack.getScreenManager().popActivities(
//                                                        this@PayActivity::class.java,
//                                                        MemberMineActivity::class.java)
                                            } else {
                                                toast("支付失败")
                                                var payMah = HashMap<String, String>()
                                                payMah.put("错误信息", obj.toString())
                                                MobclickAgent.onEvent(this@PayActivity, "wxpay_fail", payMah)
                                            }
                                        }) { throwable -> OkLogger.printStackTrace(throwable) }
                            }

                        })
            }

            if (pay_zfb.isChecked) {
                OkGo.post<String>(BaseHttp.pay_sub)
                        .tag(this@PayActivity)
                        .headers("token", getString("token"))
                        .params("expirDate", data.expirDate)
                        .params("payMoney", data.payMoney)
                        .params("vipTypeId", data.vipTypeId)
                        .params("busType", "VIP")
                        .params("payType", "Alipay")
                        .params("invoiceId", invoiceId)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getString("object")

                                RxPay(this@PayActivity).requestAlipay(obj)
                                        .subscribe({ isDone ->
                                            if (isDone) {
//                                                toast("支付成功")
                                                //更新会员级别
                                                putString("vipCode", data.vipTypeId)
                                                if (data.vipTypeId.equals("VIP_DEFAULT")) {
                                                    MobclickAgent.onEvent(this@PayActivity, "open_vip_success")
                                                } else if (data.vipTypeId.equals("VIP_SLIVER")) {
                                                    MobclickAgent.onEvent(this@PayActivity, "open_vip2_success")
                                                } else if (data.vipTypeId.equals("VIP_GOLD")) {
                                                    MobclickAgent.onEvent(this@PayActivity, "open_vip3_success")
                                                }
                                                var payMah = HashMap<String, String>()
                                                payMah.put("金额", data.payMoney)
                                                payMah.put("所属企业", "暂未添加")
                                                payMah.put("付款人", getString("userName"))
                                                payMah.put("手机号", getString("telephone"))
                                                payMah.put("日期", SimpleDateFormat("yyyy-MM-dd").format(Date()))
                                                MobclickAgent.onEvent(this@PayActivity, "alipay_success", payMah)
                                                // EventBus.getDefault().post(RefreshMessageEvent(intent.getIntExtra("positon", -1), "开通会员"))
                                                runOnUiThread({
                                                    run {
                                                        ShowTiShi()
//                                                        toast("支付成功")
                                                    }
                                                })
//                                                ActivityStack.getScreenManager().popActivities(
//                                                        this@PayActivity::class.java,
//                                                        MemberMineActivity::class.java)
                                            } else {
                                                toast("支付失败")
                                                var payMah = HashMap<String, String>()
                                                payMah.put("错误信息", obj.toString())
                                                MobclickAgent.onEvent(this@PayActivity, "alipay_fail", payMah)
                                            }
                                        })
                                        { throwable -> OkLogger.printStackTrace(throwable) }
                            }

                        })
            }
        }
    }


    override fun getData() {
        OkGo.post<BaseResponse<VipPayData>>(BaseHttp.vip_pay)
                .tag(this@PayActivity)
                .headers("token", getString("token"))
                .params("vipTypeId", intent.getStringExtra("vipTypeId"))
                .params("vipIsPay", intent.getStringExtra("vipIsPay"))
                .execute(object : JsonDialogCallback<BaseResponse<VipPayData>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<VipPayData>>) {
                        data = response.body().`object`
                        pay_total.text = response.body().`object`.payMoney
                        pay_name.text = response.body().`object`.info
                        if (response.body().`object`.shengyu.toInt() > 0) {
                            text_hintprice.text = "已为您折扣${response.body().`object`.shengyu}元"
                        }
                    }

                })
    }

    /**
     *成功提示
     */
    fun ShowTiShi() {
        // 加载布局
        val inflater = LayoutInflater
                .from(this@PayActivity)
        val view = inflater.inflate(R.layout.pop_payseccess, null)
        val tv_pay_pop = view.findViewById(R.id.tv_pay_pop) as TextView
        val tv_ikonw = view.findViewById(R.id.tv_ikonw) as TextView
        tv_pay_pop.text = "您购买的" + time + pay_name.text.toString() + "服务已经生效，具体特权详见“我的-我的会员”查看"
        // 创建popupwindow对话框
        val pop = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        pop.isFocusable = true// 必须有，因为要获得焦点进行输入
        // 设置此参数，点击外边可消失
        pop.setBackgroundDrawable(BitmapDrawable())
        // 点击其他除了popupwindow对话框的地方就可使popupwindow对话框消失
        pop.isOutsideTouchable = true
        //        pop.showAtLocation(liFr, Gravity.CENTER, 0, 0);// 显示位置以某控件为中心
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            val a = IntArray(2)
            view_top.getLocationInWindow(a)
            pop.showAtLocation(this@PayActivity.window.decorView, Gravity.NO_GRAVITY, 0, a[1] + view_top.height)
        } else {
            pop.showAsDropDown(view_top)
        }
        tv_ikonw.setOnClickListener {
            pop.dismiss()
            ActivityStack.getScreenManager().popActivities(
                    this@PayActivity::class.java,
                    MemberMineActivity::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().unregister(this@PayActivity)
    }

    @Subscribe
    fun onMessageEvent(event: TicketMessageEvent) {
        when (event.name) {
            "发票信息" -> {
                invoiceId = event.id
                pay_ticket.setRightString("已选择")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("支付界面")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("支付界面")
    }
}
