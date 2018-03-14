package com.ruanmeng.tiger_treasure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import com.umeng.analytics.MobclickAgent
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_no_search_result.*
import kotlinx.android.synthetic.main.layout_title_blue.*
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONArray


class SearchActivity : BaseActivity() {

    private var list_old = ArrayList<String>()
    private var list = ArrayList<CommonData>()
    private var list_str = ArrayList<String>()
    var items = arrayOf("产品", "企业")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        blue_nav_title.text = "搜索"
        updateHistory()
        if (intent.getStringExtra("member") != null) {//供货搜索
            search_cancel.visibility = View.VISIBLE
            li_search_botton.visibility = View.GONE
            et_keyword.imeOptions = EditorInfo.IME_ACTION_SEARCH//设置搜索软键盘
        } else {
            search_cancel.visibility = View.GONE
            li_search_botton.visibility = View.VISIBLE
        }
        //搜索
        search_cancel.setOnClickListener {
            if (et_keyword.text.toString().isBlank()) {
                toast("请输入关键字")
                return@setOnClickListener
            }
            /*隐藏软键盘*/
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            if (et_keyword.text.toString().trim() != "") {
                val array = if (getString("history") == "") JSONArray() else JSONArray(getString("history"))
                if (!array.toString().contains(et_keyword.text.toString().trim()))
                    array.put(et_keyword.text.toString().trim())
                putString("history", array.toString())

                list.clear()
                (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                getGHData(et_keyword.text.toString().trim(), true)
            }
        }
        search_product.setOnClickListener {
            if (et_keyword.text.toString().isBlank()) {
                toast("请输入关键字")
                return@setOnClickListener
            }
            /*隐藏软键盘*/
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            if (et_keyword.text.toString().trim() != "") {
                val array = if (getString("history") == "") JSONArray() else JSONArray(getString("history"))
                if (!array.toString().contains(et_keyword.text.toString().trim()))
                    array.put(et_keyword.text.toString().trim())
                putString("history", array.toString())

                list.clear()
                (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                OkGo.getInstance().cancelTag(this@SearchActivity)//确保每个时段只请求一个接口
                getData(et_keyword.text.toString().trim(), true, "1")//产品 1  企业  2
            }
        }
        search_company.setOnClickListener {
            if (et_keyword.text.toString().isBlank()) {
                toast("请输入关键字")
                return@setOnClickListener
            }
            /*隐藏软键盘*/
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            if (et_keyword.text.toString().trim() != "") {
                val array = if (getString("history") == "") JSONArray() else JSONArray(getString("history"))
                if (!array.toString().contains(et_keyword.text.toString().trim()))
                    array.put(et_keyword.text.toString().trim())
                putString("history", array.toString())

                list.clear()
                (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                OkGo.getInstance().cancelTag(this@SearchActivity)//确保每个时段只请求一个接口
                getData(et_keyword.text.toString().trim(), true, "2")//产品 1  企业  2
            }
        }
        search_history.setOnTagClickListener { _, position, _ ->
            et_keyword.setText(list_old[position])
            et_keyword.setSelection(et_keyword.text.length)
//            getData(list_old[position], true, "1")//产品 1  企业  2)

            return@setOnTagClickListener true
        }
        search_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_search_list) { data, injector ->
                    injector.gone(R.id.item_search_del)
                            .text(R.id.item_search_name, data.name)

                            .with<ImageView>(R.id.item_search_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.image)
                                        .placeholder(R.mipmap.default_product2) // 等待时的图片
                                        .error(R.mipmap.default_product2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_search, {
                                if (intent.getStringExtra("member") != null) {//供货搜索
                                    when (data.type) {
                                        "1" -> {
                                            val intent = Intent(baseContext, MemberFirstActivity::class.java)
                                            intent.putExtra("supplyId", data.id)
                                            startActivity(intent)
                                        }
                                        "2" -> {
                                            val intent = Intent(baseContext, MemberSecondActivity::class.java)
                                            intent.putExtra("purchasingId", data.id)
                                            startActivity(intent)
                                        }
                                        "3" -> {
                                            val intent = Intent(baseContext, MemberThirdActivity::class.java)
                                            intent.putExtra("cooperateId", data.id)
                                            startActivity(intent)
                                        }
                                    }
                                } else {
                                    when (data.type) {
                                        "2" -> {
                                            val intent = Intent(baseContext, EnterpriseDetailActivity::class.java)
                                            intent.putExtra("companyId", data.id)
                                            startActivity(intent)
                                        }
                                        "1" -> {
                                            val intent = Intent(baseContext, MemberFirstActivity::class.java)
                                            intent.putExtra("supplyId", data.id)
                                            startActivity(intent)
                                        }
                                    }
                                }
                            })
                }
                .attachTo(search_list)

        et_keyword.addTextChangedListener(this@SearchActivity)
        et_keyword.setOnEditorActionListener { v, actionId, _ ->
            /*判断是否是“SEARCH”键*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (et_keyword.text.toString().isBlank()) {
                    toast("请输入关键字")
                } else {
                    /*隐藏软键盘*/
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (imm.isActive) imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)

                    if (et_keyword.text.toString().trim() != "") {
                        val array = if (getString("history") == "") JSONArray() else JSONArray(getString("history"))
                        if (!array.toString().contains(et_keyword.text.toString().trim()))
                            array.put(et_keyword.text.toString().trim())
                        putString("history", array.toString())

                        list.clear()
                        (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                        getGHData(et_keyword.text.toString().trim(), true)
                    }
                }
            }

            return@setOnEditorActionListener false
        }

        search_clear.setOnClickListener {
            list_old.clear()
            putString("history", "")
            updateHistory()
        }
    }

    fun setData() {
        list_str.clear()
        list_str.add("产品")
        list_str.add("企业")
    }


    private fun updateHistory() {
        if (getString("history") != "")
            list_old = Tools.jsonArrayToList(getString("history"))

        search_history.adapter = object : TagAdapter<String>(list_old) {

            override fun getView(parent: FlowLayout, position: Int, t: String): View {
                val tv_content = LayoutInflater.from(baseContext).inflate(R.layout.item_search_history, search_history, false) as TextView
                tv_content.text = t
                return tv_content
            }

        }
    }

    /**
     * 首页搜索
     */
    private fun getData(key: String, isRefresh: Boolean, searchType: String) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.index_search)
                .tag(this@SearchActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("keyword", key)
                .params("searchType", searchType)//产品 1  企业  2
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, isRefresh) {

                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        sv_search_hint.visibility = View.GONE
                        empty_view.visibility = View.GONE
                        list.clear()
                        list.addItems(response.body().`object`)

                        (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()

                        if (searchType.equals("1")) {
                            MobclickAgent.onEvent(this@SearchActivity, "home_search_product")
                        } else {
                            MobclickAgent.onEvent(this@SearchActivity, "home_search_company")
                        }
                    }

                    override fun onFinish() {
                        super.onFinish()

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    /**
     * 供货搜索
     */
    private fun getGHData(key: String, isRefresh: Boolean) {
        OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.supply_search)
                .tag(this@SearchActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("keyword", key)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, isRefresh) {
                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                        sv_search_hint.visibility = View.GONE
                        empty_view.visibility = View.GONE
                        list.clear()
                        list.addItems(response.body().`object`)
                        (search_list.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString().trim() == "") {
            updateHistory()
            sv_search_hint.visibility = View.VISIBLE
            empty_view.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("首页/供货搜索")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("首页/供货搜索")
    }
}
