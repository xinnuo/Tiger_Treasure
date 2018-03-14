package com.ruanmeng.tiger_treasure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CityData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.sort.PinyinContactComparator
import com.ruanmeng.utils.DensityUtil
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_contact.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import qdx.stickyheaderdecoration.NormalDecoration
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ContactActivity : BaseActivity() {

    private val list = ArrayList<CityData>()
    private val list_index = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        init_title("通讯录", "创建群聊")

        getData()
    }

    override fun init_title() {
        super.init_title()
        @Suppress("DEPRECATION")
        val drawable = resources.getDrawable(R.mipmap.icon_nav_add)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(drawable, null, null, null)
        contact_list.apply {
            linearLayoutManager = LinearLayoutManager(this@ContactActivity)
            layoutManager = linearLayoutManager

            val decoration = object : NormalDecoration() {
                override fun getHeaderName(pos: Int): String = list[pos].letter
            }
            @Suppress("DEPRECATION")
            decoration.setHeaderContentColor(resources.getColor(R.color.background))
            decoration.setHeaderHeight(DensityUtil.dp2px(30f))
            decoration.setTextSize(DensityUtil.sp2px(14f))
            @Suppress("DEPRECATION")
            decoration.setTextColor(resources.getColor(R.color.gray))
            addItemDecoration(decoration)
        }



        mAdapter = SlimAdapter.create(SlimAdapterEx::class.java)
                .register<CityData>(R.layout.item_contact_list) { data, injector ->
                    val length = if (data.distance.toDouble() < 1000) "${data.distance}m"
                    else "${DecimalFormat(".#").format(data.distance.toDouble() / 1000)}km"
                    injector.gone(R.id.item_contact_check)
                            .visibility(R.id.item_contact_cname, View.VISIBLE)
                            .text(R.id.item_contact_cname, data.compName)
                            .text(R.id.item_contact_name, data.userName)
                            .with<TextView>(R.id.item_contact_cname) { view ->
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
                            .text(R.id.item_contact_distance, length)
                            .visibility(R.id.item_contact_distance, View.VISIBLE)

                            .visibility(
                                    R.id.item_contact_divider1,
                                    if (list.indexOf(data) != list.size - 1
                                            && data.letter != list[list.indexOf(data) + 1].letter)
                                        View.GONE
                                    else View.VISIBLE)

                            .with<ImageView>(R.id.item_contact_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .placeholder(R.mipmap.default_user) //等待时的图片
                                        .error(R.mipmap.default_user)       //加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_contact) {
                                //融云刷新用户信息
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.enterpriserId,
                                        data.userName,
                                        Uri.parse(BaseHttp.baseImg + data.userhead)))
                                //融云单聊
                                RongIM.getInstance().startPrivateChat(baseContext, data.enterpriserId, data.userName)
                            }
                            //点击头像跳转企业家详情页
                            .clicked(R.id.item_contact_img) {
                                val intent = Intent(baseContext, PersonActivity::class.java)
                                intent.putExtra("accountInfoId", data.enterpriserId)
                                startActivity(intent)
                            }
                }
                .attachTo(contact_list)

        val letters = arrayOf(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
        list_index.addAll(letters)
        index_layout.setIndexBarHeightRatio(0.9f)
        index_layout.indexBar.setIndexsList(list_index)
        index_layout.indexBar.setIndexChangeListener { name ->
            for (item in list) {
                if (name == item.letter) {
                    linearLayoutManager.scrollToPositionWithOffset(list.indexOf(item), 0)
                    return@setIndexChangeListener
                }
            }
        }

        tvRight.setOnClickListener {
            val intent = Intent(baseContext, ContactAddActivity::class.java)
            intent.putExtra("list", list)
            startActivity(intent)
        }

        et_contctkeyword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_contctkeyword.updateIconClear()
                if (p0.toString().isEmpty()) {//清空
                    getData()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        et_contctkeyword.setOnEditorActionListener { v, actionId, event ->
            /*判断是否是“SEARCH”键*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /*隐藏软键盘*/
//                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                if (imm.isActive) imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
                getData()
            }
            return@setOnEditorActionListener false
        }
    }

    override fun getData() {
        OkGo.post<BaseResponse<ArrayList<CityData>>>(BaseHttp.enterpriser2_list)
                .tag(this@ContactActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userName", et_contctkeyword.text.trim().toString())
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityData>>>(baseContext, true) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CityData>>>) {
                        if (et_contctkeyword.text.trim().toString().isNotEmpty()) {
                            ll_zfh.visibility = View.GONE
                        } else {
                            ll_zfh.visibility = View.VISIBLE
                        }
                        seperateLists(response.body().`object`)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                })
    }


    private fun seperateLists(mlist: List<CityData>?) {
        list.clear()
        if (mlist != null && mlist.isNotEmpty()) {
            mlist.forEach { if (it.letter.isEmpty()) it.letter = "#" }
            Collections.sort(mlist, PinyinContactComparator())
            list.addAll(mlist)
        }
    }


    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.ll_zfh -> startActivity(GovtContactActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("通讯录")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("通讯录")
    }
}
