package com.ruanmeng.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.adapter.TabFragmentAdapter
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.tiger_treasure.R
import com.ruanmeng.tiger_treasure.WebActivity
import com.ruanmeng.utils.Tools
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.fragment_main_second.*

class MainSecondFragment : BaseFragment() {

    private val list_slider = ArrayList<CommonData>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        getData()
    }

    override fun init_title() {
        val fragments = ArrayList<Fragment>()
        val titles = listOf("聊天列表", "热门话题")

        fragments.add(TabFirstFragment())
        fragments.add(TabSecondFragment())

        second_tab.apply {
            post { Tools.setIndicator(this, 50, 50) }
            removeAllTabs()
        }
        second_pager.adapter = TabFragmentAdapter(childFragmentManager, titles, fragments)
        // 为TabLayout设置ViewPager
        second_tab.setupWithViewPager(second_pager)
    }

    override fun getData() {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.slider_list)
                .tag(this@MainSecondFragment)
                .headers("token", getString("token"))
                .params("sliderType", "2")
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(activity) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {
                        list_slider.clear()
                        list_slider.addItems(response.body().`object`.rows)

                        val mLoopAdapter = LoopAdapter(activity, second_banner)
                        second_banner.apply {
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
                        if (imgs.size < 2) {
                            second_banner.pause()
                            second_banner.setHintViewVisibility(false)
                        } else {
                            second_banner.resume()
                            second_banner.setHintViewVisibility(true)
                        }
                    }

                })
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业家")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业家")
    }
}
