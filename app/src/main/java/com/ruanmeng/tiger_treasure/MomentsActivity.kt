package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.*
import com.ruanmeng.model.MomentsData
import com.ruanmeng.model.MomentsModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DensityUtil
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.ObjectUtils
import com.ruanmeng.view.BottomSheetEditDialog
import com.ruanmeng.view.FullyLinearLayoutManager
import com.ruanmeng.view.NineGridLayout
import com.umeng.analytics.MobclickAgent
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MomentsActivity : BaseActivity() {

    private val list = ArrayList<MomentsData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moments)
        init_title("企业圈")

        EventBus.getDefault().register(this@MomentsActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    @SuppressLint("InflateParams")
    override fun init_title() {
        super.init_title()
        ivRight.setImageResource(R.mipmap.icon_nav_photo)
        ivRight.setPadding(
                DensityUtil.dp2px(10f),
                DensityUtil.dp2px(10f),
                DensityUtil.dp2px(10f),
                DensityUtil.dp2px(10f))

        swipe_refresh.apply {
            @Suppress("DEPRECATION")
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setOnChildScrollUpCallback { _, _ -> recycle_list.canScrollVertically(-1) } //解决嵌套滑动冲突
            setOnRefreshListener { getData(1) }
        }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(baseContext).inflate(R.layout.header_moments, null)
                    val header_img = view.findViewById<ImageView>(R.id.moments_img)

                    GlideApp.with(baseContext)
                            .load(BaseHttp.baseImg + getString("userhead"))
                            .placeholder(R.mipmap.default_user)
                            .error(R.mipmap.default_user)
                            .dontAnimate()
                            .into(header_img)

                    header_img.setOnClickListener {
                        val intent = Intent(baseContext, PersonActivity::class.java)
                        intent.putExtra("accountInfoId", getString("token"))
                        startActivity(intent)
                    }
                    addHeader(view)
                    register<MomentsData>(R.layout.item_moments_list) { data, injector ->
                        injector.text(R.id.item_moments_name, data.userName)
                                .text(R.id.item_moments_time, data.showDate)
                                .text(R.id.item_moments_title,ObjectUtils.unicode2String(data.cricleContent))
                                .text(R.id.item_moments_companyname, data.compName)
//                                .text(R.id.item_moments_watch, if (data.collecon == "1") "取消" else "关注")
                                .visibility(R.id.item_moments_peoples, if (data.thumbsList!!.isNotEmpty()) View.VISIBLE else View.GONE)
//                                .visibility(
//                                        R.id.li_moments_watch,
//                                        if (data.accountInfoId == getString("token")) View.GONE else View.VISIBLE)
                                .gone( R.id.li_moments_watch)
                                .apply {
                                    if (data.collecon == "1") {
                                        background(R.id.item_moments_watch, R.mipmap.focus_2)
                                    } else {
                                        background(R.id.item_moments_watch, R.mipmap.focus)
                                    }
                                    if (data.thumbs == "1") {
                                        background(R.id.item_moments_zan, R.mipmap.zan_2)
                                    } else {
                                        background(R.id.item_moments_zan, R.mipmap.zan_1)
                                    }

                                }

                                .with<NineGridLayout>(R.id.item_moments_nine) { layout ->
                                    if (data.cricleImages.isNotEmpty()) {
                                        layout.visibility = View.VISIBLE
                                        layout.loadUriList(data.cricleImages.split(","))
                                        layout.setOnClickImageListener { position, view, _, urlList ->
                                            // 图片点击事件
                                            MNImageBrowser.showImageBrowser(baseContext, view, position, urlList)
                                        }
                                    } else layout.visibility = View.GONE
                                }

                                .with<ImageView>(R.id.item_moments_img) { view ->
                                    GlideApp.with(baseContext)
                                            .load(BaseHttp.baseImg + data.userhead)
                                            .placeholder(R.mipmap.default_user) //等待时的图片
                                            .error(R.mipmap.default_user)       //加载失败的图片
                                            .centerCrop()
                                            .dontAnimate()
                                            .into(view)
                                }

                                .with<TagFlowLayout>(R.id.item_moments_peoples) { view ->
                                    val items = ArrayList<String>()
                                    items.add("赞 ")
                                    data.thumbsList!!.forEach { items.add(it.userName + "，") }
                                    val str = items[items.size - 1].replace("，", "")
                                    items[items.size - 1] = str

                                    view.adapter = object : TagAdapter<String>(items) {
                                        @Suppress("DEPRECATION")
                                        override fun getView(parent: FlowLayout, position: Int, item: String): View {

                                            val tv_name = LayoutInflater.from(baseContext).inflate(
                                                    R.layout.item_moments_flow,
                                                    view,
                                                    false) as TextView
                                            if (position == 0) tv_name.setTextColor(resources.getColor(R.color.gray))
                                            else tv_name.setTextColor(resources.getColor(R.color.colorAccent))
                                            tv_name.text = item
                                            return tv_name
                                        }
                                    }

                                    view.setOnTagClickListener { _, position, _ ->
                                        if (position > 0) {
                                            val intent = Intent(baseContext, PersonActivity::class.java)
                                            intent.putExtra("accountInfoId", data.thumbsList!![position - 1].accountInfoId)
                                            startActivity(intent)
                                        }
                                        return@setOnTagClickListener true
                                    }
                                }

                                .with<RecyclerView>(R.id.item_moments_list) { view ->
                                    view.apply {
                                        layoutManager = FullyLinearLayoutManager(baseContext)
                                        adapter = SlimAdapter.create()
                                                .register<MomentsData>(R.layout.item_moments_inner) { item, injector ->
                                                    injector.text(R.id.item_inner_title1, item.replyUserName)
                                                            .text(R.id.item_inner_content, ObjectUtils.unicode2String(item.replyContent))
                                                            .text(R.id.item_inner_title2, item.replyToUserName)

                                                            .visibility(
                                                                    R.id.item_inner_divider,
                                                                    if (data.circleReplyList!!.indexOf(item) != data.circleReplyList!!.size - 1) View.GONE else View.VISIBLE)
                                                            .visibility(R.id.item_inner_ll, if (item.replyToAccountInfoId == item.replyAccountInfoId) View.GONE else View.VISIBLE)

                                                            .clicked(R.id.item_inner_title1) {
                                                                val intent = Intent(baseContext, PersonActivity::class.java)
                                                                intent.putExtra("accountInfoId", item.replyAccountInfoId)
                                                                startActivity(intent)
                                                            }

                                                            .clicked(R.id.item_inner_title2) {
                                                                val intent = Intent(baseContext, PersonActivity::class.java)
                                                                intent.putExtra("accountInfoId", item.replyToAccountInfoId)
                                                                startActivity(intent)
                                                            }

                                                            .clicked(R.id.item_inner) {
                                                                if (item.replyToAccountInfoId == getString("token")) {
                                                                    showSheetDialog(list.indexOf(data), data.circleId, item.replyAccountInfoId, item.replyUserName)
                                                                }
                                                            }
                                                }
                                                .attachTo(view)
                                    }

                                    val items = ArrayList<Any>()
                                    items.addItems(data.circleReplyList)
                                    (view.adapter as SlimAdapter).updateData(items).notifyDataSetChanged()
                                }

                                .clicked(R.id.item_moments_img) {
                                    val intent = Intent(baseContext, PersonActivity::class.java)
                                    intent.putExtra("accountInfoId", data.accountInfoId)
                                    startActivity(intent)
                                }

                                .clicked(R.id.li_moments_zan) {
                                    OkGo.post<String>(BaseHttp.circle_thumb)
                                            .tag(this@MomentsActivity)
                                            .headers("token", getString("token"))
                                            .params("cricleId", data.circleId)
                                            .execute(object : StringDialogCallback(baseContext) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                    toast(msg)

                                                    if (data.thumbs == "1") {
                                                        data.thumbs = "0"
                                                        injector.background(R.id.item_moments_zan, R.mipmap.zan_1)
                                                        var pos = 0
                                                        val mlist = ArrayList<MomentsData>()
                                                        mlist.addItems(data.thumbsList)
                                                        mlist.forEach {
                                                            if (it.replyAccountInfoId == getString("token"))
                                                                pos = mlist.indexOf(it)
                                                        }
                                                        mlist.removeAt(pos)
                                                        data.thumbsList = mlist

                                                        mAdapterEx.updateData(list).notifyDataSetChanged()
                                                    } else {
                                                        data.thumbs = "1"
                                                        injector.background(R.id.item_moments_zan, R.mipmap.zan_2)
                                                        val mlist = ArrayList<MomentsData>()
                                                        mlist.addItems(data.thumbsList)
                                                        mlist.add(MomentsData().apply {
                                                            userName = getString("userName")
                                                            accountInfoId = getString("token")
                                                        })
                                                        data.thumbsList = mlist

                                                        mAdapterEx.updateData(list).notifyDataSetChanged()
                                                    }
                                                }

                                            })
                                }

                                .clicked(R.id.item_moments_comment) {
                                    showSheetDialog(list.indexOf(data), data.circleId, getString("token"), getString("userName"))
                                }

                                .clicked(R.id.li_moments_watch) {
                                    OkGo.post<String>(BaseHttp.collecon_gz_sub)
                                            .tag(this@MomentsActivity)
                                            .headers("token", getString("token"))
                                            .params("targetType", "AccountInfo")
                                            .params("targetId", data.accountInfoId)
                                            .execute(object : StringDialogCallback(baseContext) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                    toast(msg)
                                                    data.collecon = if (data.collecon == "1") "0" else "1"
                                                    if (data.collecon == "1") {
                                                        injector.background(R.id.item_moments_watch, R.mipmap.focus_2)
                                                    } else {
                                                        injector.background(R.id.item_moments_watch, R.mipmap.focus)
                                                    }
//                                                    injector.text(R.id.item_moments_watch, if (data.collecon == "1") "取消" else "关注")
                                                }

                                            })
                                }
                    }
                    attachTo(recycle_list)
                }

        ivRight.setOnClickListener { startActivity(ReleaseActivity::class.java) }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<MomentsModel>>(BaseHttp.circle_list)
                .tag(this@MomentsActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JsonDialogCallback<BaseResponse<MomentsModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<MomentsModel>>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().`object`.rows)
                            if (count(response.body().`object`.rows) > 0) pageNum++
                            if(response.body().`object`.params.send=="1"){
                                ivRight.visibility = View.VISIBLE
                            }else{
                                ivRight.visibility = View.GONE
                            }
                        }

                        mAdapterEx.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }

    @SuppressLint("InflateParams")
    private fun showSheetDialog(position: Int, cricleId: String, replyToId: String, replyToName: String) {
        val dialog = BottomSheetEditDialog(baseContext)

        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_moments_input, null) as View
        val et_content = view.findViewById(R.id.moments_hint) as EditText
        val tv_send = view.findViewById(R.id.moments_send) as TextView
        et_content.hint = "回复 " + replyToName

        tv_send.setOnClickListener {
            dialog.dismiss()

            OkGo.post<String>(BaseHttp.circle_reply)
                    .tag(this@MomentsActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("replyContent", et_content.text.toString())
                    .params("replyAccountInfoId", getString("token"))
                    .params("replyToAccountInfoId", replyToId)
                    .params("cricleId", cricleId)
                    .execute(object : StringDialogCallback(baseContext, false) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            toast("回复成功")

                            (list[position].circleReplyList as ArrayList).add(MomentsData().apply {
                                replyAccountInfoId = getString("token")
                                replyContent = ObjectUtils.string2Unicode(et_content.text.toString())
                                replyToAccountInfoId = replyToId
                                replyToUserName = replyToName
                                replyUserName = getString("userName")
                            })
                            mAdapterEx.updateData(list).notifyDataSetChanged()
                        }

                    })
        }

        et_content.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }

        dialog.setContentView(view)
        dialog.setOnShowListener({ KeyboardHelper.showSoftInput(this@MomentsActivity) })
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().unregister(this@MomentsActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "企业圈" -> {
                swipe_refresh.isRefreshing = true
                getData(1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业圈")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业圈")
    }
}
