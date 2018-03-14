package com.ruanmeng.fragment

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.BaseResponse
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.HorizontalAdapter
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.tiger_treasure.*
import com.ruanmeng.view.RoundAngleImageView
import com.ruanmeng.view.SwitcherTextView
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_main_first.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_first.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import q.rorbin.badgeview.QBadgeView
import java.text.DecimalFormat

class MainFirstFragment : BaseFragment() {

    private val list = ArrayList<Any>()
    private val list_slider = ArrayList<CommonData>()
    private val list_notice = ArrayList<CommonData>()
    private val list_like = ArrayList<CommonData>()
    private var msgNum = 0
    private var badgeView: QBadgeView? = null

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_first, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainFirstFragment)

        if (getString("areaName").isNotEmpty()) {
            first_city.text = getString("areaName")
            swipe_refresh.isRefreshing = true
            getData()
        } else {
            /* AMapLocationHelper.getInstance(activity).startLocation(1) { location, isSuccessed, codes ->
                 if (isSuccessed && 1 in codes) {
                     first_city.text = location.city.replace("市", "")
                     putString("location_city", first_city.text.toString())

                     swipe_refresh.isRefreshing = true
                     getData()
                 } else startActivity(CityListActivity::class.java)
             }*/
        }
        //第一次安装且有数据 则弹出赠送会员的提示框
        if (getString("lastLoginDate").isEmpty() && getString("vipInfo").isNotEmpty()) {
            Handler().postDelayed({ ShowTiShi() }, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        //普通会员的时候城市不能点击  右边箭头不让显示
        if (getString("vipCode") == "VIP_DEFAULT") {
            imv_city_mark.visibility = View.GONE
        } else {
            imv_city_mark.visibility = View.VISIBLE
        }
        if (getString("areaName").isNotEmpty()) {
            getData()
        }
        MobclickAgent.onPageStart("首页")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("首页")
    }

    override fun init_title() {

        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(activity, swipe_refresh)
        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.header_first) { _, injector ->
                    injector.visibility(R.id.first_qi, if (getString("accountType") == "App_Manager") View.GONE else View.VISIBLE)
                            .visibility(R.id.first_vip, if (getString("isGov") == "1") View.GONE else View.VISIBLE) //政府号隐藏vip
                            .with<RollPagerView>(R.id.first_banner) { view ->
                                val mLoopAdapter = LoopAdapter(this@MainFirstFragment.activity, view)
                                view.apply {
                                    setAdapter(mLoopAdapter)
                                    setOnItemClickListener { position ->
                                        //轮播图点击事件
                                        if (list_slider.get(position).jumpType.equals("2")) {//跳转web
                                            val intent = Intent(activity, WebActivity::class.java)
                                            intent.putExtra("title", list_slider.get(position).sliderTitle)
                                            intent.putExtra("url", list_slider[position].content)
                                            startActivity(intent)
                                        } else if (list_slider.get(position).jumpType.equals("3")) {//文本解析

                                        }
                                    }
                                }

                                val imgs = ArrayList<String>()
                                list_slider.mapTo(imgs) { it.sliderImg }
                                mLoopAdapter.setImgs(imgs)
                                if (imgs.size <= 1) {
                                    view.pause()
                                    view.setHintViewVisibility(false)
                                } else {
                                    view.resume()
                                    view.setHintViewVisibility(true)
                                }
                            }

                            .with<SwitcherTextView>(R.id.first_notice, { view ->
                                if (list_notice.size > 0) {
                                    view.setData(list_notice, { position ->
                                        val intent = Intent(activity, GGWebActivity::class.java)
                                        intent.putExtra("title", "详情")
                                        intent.putExtra("messageInfoId", list_notice[position].messageInfoId)
                                        startActivity(intent)
                                    }, view)
                                }
                            })

                            .clicked(R.id.first_around) {
                                val intent = Intent(activity, EnterpriseListActivity::class.java)
                                startActivity(intent)
                            }
                }
                .register<NearData>(R.layout.item_first_list) { data, injector ->
                    injector.visible(R.id.item_first_length)
                            .gone(R.id.item_first_type)
                            .text(R.id.item_first_company, data.compName)
                            .with<TextView>(R.id.item_first_company) { view ->
                                if (data.vipTypeId.equals("VIP_SLIVER")) {
                                    val drawable = getResources().getDrawable(R.mipmap.vip_center)
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                                    view.setCompoundDrawables(null, null, drawable, null)
                                } else if (data.vipTypeId.equals("VIP_GOLD")) {
                                    val drawable = getResources().getDrawable(R.mipmap.vip_most)
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                                    view.setCompoundDrawables(null, null, drawable, null)
                                } else
                                    view.setCompoundDrawables(null, null, null, null)
                            }
                            .text(R.id.item_first_title, data.compTypeName)
                            .text(R.id.item_first_addr, data.address)
                            .text(R.id.item_first_scan, "浏览 ${data.llNum}")
                            .text(R.id.item_first_watch, "收藏 ${data.gzNum}")
                            .text(R.id.item_first_length,
                                    if (data.distance.toDouble() < 1000) "${data.distance}m"
                                    else "${DecimalFormat(".#").format(data.distance.toDouble() / 1000)}km")

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.indexOf(3) - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.indexOf(3) - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_first_logo) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.compLogo)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .with<RoundAngleImageView>(R.id.item_first_img) { view ->
                        GlideApp.with(activity)
                                .load(BaseHttp.baseImg + data.copmImage)
                                .placeholder(R.mipmap.default_product) // 等待时的图片
                                .error(R.mipmap.default_product)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(view)
                    }

                            .clicked(R.id.item_first_11) {
                                val intent = Intent(activity, EnterpriseDetailActivity::class.java)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .register<Int>(R.layout.item_first_divider) { _, injector ->
                    injector.with<MultiSnapRecyclerView>(R.id.first_horizontal) { view ->
                        view.apply {
                            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                            adapter = HorizontalAdapter(activity, list_like)
                        }
                    }

                            .clicked(R.id.first_hot) {
                                val intent = Intent(activity, EnterpriseListActivity::class.java)
                                intent.putExtra("isHot", "1")
                                startActivity(intent)
                            }
                }
                .register<HotData>(R.layout.item_first_list) { data, injector ->
                    injector.invisible(R.id.item_first_length)
                            .gone(R.id.item_first_type)
                            .text(R.id.item_first_company, data.compName)
                            .text(R.id.item_first_title, data.compNatureName)
                            .text(R.id.item_first_addr, data.address)
                            .with<TextView>(R.id.item_first_company) { view ->
                                if (data.vipTypeId.equals("VIP_SLIVER")) {
                                    val drawable = getResources().getDrawable(R.mipmap.vip_center)
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                                    view.setCompoundDrawables(null, null, drawable, null)
                                } else if (data.vipTypeId.equals("VIP_GOLD")) {
                                    val drawable = getResources().getDrawable(R.mipmap.vip_most)
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                                    view.setCompoundDrawables(null, null, drawable, null)
                                } else
                                    view.setCompoundDrawables(null, null, null, null)
                            }
                            .text(R.id.item_first_scan, "浏览 ${data.llNum}")
                            .text(R.id.item_first_watch, "收藏 ${data.gzNum}")

                            .with<ImageView>(R.id.item_first_logo) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.compLogo)
                                        .placeholder(R.mipmap.default_logo) // 等待时的图片
                                        .error(R.mipmap.default_logo)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }
                            .with<ImageView>(R.id.item_first_img) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.copmImage)
                                        .placeholder(R.mipmap.default_product) // 等待时的图片
                                        .error(R.mipmap.default_product)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first_11) {
                                val intent = Intent(activity, EnterpriseDetailActivity::class.java)
                                intent.putExtra("companyId", data.companyId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<BaseResponse<HomeModel>>(BaseHttp.index)
                .tag(this@MainFirstFragment)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("areaName", getString("areaName"))
                .params("areaId", getString("areaId"))
                .execute(object : JacksonDialogCallback<BaseResponse<HomeModel>>(activity) {

                    override fun onSuccess(response: Response<BaseResponse<HomeModel>>) {
                        list.clear()
                        list_slider.clear()
                        list_notice.clear()
                        list_like.clear()

                        list.add("轮播图")
                        list.addItems(response.body().`object`.nearCompList)
                        list.add(3)
                        list.addItems(response.body().`object`.hotCompList)


                        list_slider.addItems(response.body().`object`.sliderList)
                        list_notice.addItems(response.body().`object`.sysMessageList)
                        list_like.addItems(response.body().`object`.recentViewCompList)

                        if (response.body().`object`.msgNum.isNotEmpty()) {
                            msgNum = response.body().`object`.msgNum.toInt()

                            if (msgNum > 0) {
                                if (badgeView == null)
                                    badgeView = QBadgeView(activity)

                                badgeView!!.apply {
                                    bindTarget(first_msg)
                                    badgeNumber = msgNum
                                    setBadgeTextSize(10f, true)
                                    setOnDragStateChangedListener { dragState, badge, targetView -> }
                                }
                            } else {
                                if (badgeView != null) {
                                    badgeView!!.visibility = View.GONE
                                }
                            }
                        }
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        if (swipe_refresh != null)
                            swipe_refresh.isRefreshing = false
                    }

                })
    }

    /**
     *第一次安装   会赠送一个月会员成功提示
     */
    private fun ShowTiShi() {
        // 加载布局
        val inflater = LayoutInflater
                .from(activity)
        val view = inflater.inflate(R.layout.pop_payseccess, null)
        val tv_pay_pop = view.findViewById(R.id.tv_pay_pop) as TextView
        val tv_ikonw = view.findViewById(R.id.tv_ikonw) as TextView
        tv_pay_pop.text = getString("vipInfo")
//        tv_pay_pop.text = "自今日起赠送您的企业一年普通会员服务，具体特权详见“我的-我的会员”查看"
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
            view_main.getLocationInWindow(a)
            pop.showAtLocation(activity.window.decorView, Gravity.NO_GRAVITY, 0, a[1] + view_main.height)
        } else {
            pop.showAsDropDown(view_main)
        }
        tv_ikonw.setOnClickListener {
            pop.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@MainFirstFragment)
    }

    @Subscribe
    fun onMessageEvent(event: CityMessageEvent) {
        first_city.text = getString("areaName")
        swipe_refresh.isRefreshing = true
        getData()
    }

    @Subscribe
    fun onMessageEvent(event: CityNewEvent) {
        first_city.text = event.name
        putString("areaId", event.id)
        putString("areaName", event.name)
        swipe_refresh.isRefreshing = true
        getData()
    }

}
