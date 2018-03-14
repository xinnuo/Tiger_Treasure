package com.ruanmeng.tiger_treasure

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.amap.api.AMapLocationHelper
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.TranslateAnimation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.maning.mndialoglibrary.MProgressDialog
import com.ruanmeng.base.*
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.utils.DensityUtil
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_location.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.ex.loadmore.SimpleLoadMoreViewCreator
import net.idik.lib.slimadapter.ex.loadmore.SlimMoreLoader
import org.greenrobot.eventbus.EventBus


class LocationActivity : BaseActivity() {
    private lateinit var aMap: AMap
    private var screenMarker: Marker? = null
    private var centerLatLng: LatLng? = null
    private var isFirstLoc = true
    private lateinit var mMProgressDialog: MProgressDialog
    private lateinit var query: PoiSearch.Query
    private lateinit var poiSearch: PoiSearch

    private var list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        location_map.onCreate(savedInstanceState)
        init_title("选择地址")

        if (intent.getStringExtra("lan") != null && intent.getStringExtra("lan").isNotEmpty()) {
            centerLatLng = LatLng(
                    intent.getStringExtra("lan").toDouble(),
                    intent.getStringExtra("lng").toDouble())
            aMap.animateCamera(CameraUpdateFactory.changeLatLng(centerLatLng))

        } else if (getString("location_lat").isNotEmpty()) {
            centerLatLng = LatLng(
                    getString("location_lat").toDouble(),
                    getString("location_lng").toDouble())
            aMap.animateCamera(CameraUpdateFactory.changeLatLng(centerLatLng))
        } else {
            startLocation()
        }

    }

    override fun init_title() {
        super.init_title()
        aMap = location_map.map
        val mUiSettings = aMap.uiSettings

        aMap.isTrafficEnabled = false       //实时交通状况
        aMap.mapType = AMap.MAP_TYPE_NORMAL //矢量地图模式
        aMap.isMyLocationEnabled = true     //触发定位并显示定位层
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18f)) //缩放级别

        mUiSettings.isScaleControlsEnabled = false    //比例尺
        mUiSettings.isZoomControlsEnabled = false     //缩放按钮
        mUiSettings.isCompassEnabled = false          //指南针
        mUiSettings.isMyLocationButtonEnabled = false //定位按钮
        mUiSettings.isTiltGesturesEnabled = false     //倾斜手势
        mUiSettings.isRotateGesturesEnabled = false   //旋转手势
        mUiSettings.setLogoBottomMargin(-50)          //隐藏logo

        val locationStyle = MyLocationStyle()
        locationStyle.apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER) //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动
            interval(5000)
            strokeColor(Color.TRANSPARENT)
            radiusFillColor(Color.TRANSPARENT)
            myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_self))
        }
        aMap.myLocationStyle = locationStyle
        query = PoiSearch.Query("", "120000", "")
        initPoiSearch()

        aMap.setOnMapLoadedListener { addMarkerInScreenCenter() }
        aMap.setOnMyLocationChangeListener { location ->
            //            centerLatLng = LatLng(location.latitude, location.longitude)

            if (location != null) {
                if (isFirstLoc) {
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(centerLatLng))
                    isFirstLoc = false
                }
            }
        }
        aMap.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {

            override fun onCameraChangeFinish(position: CameraPosition) {
                startJumpAnimation()

                window.decorView.postDelayed({
                    runOnUiThread {
                        if (!isFirstLoc) {
                            pageNum = 1
                            getData(pageNum)
                        }
                    }
                }, 300)
            }

            override fun onCameraChange(position: CameraPosition) {}

        })

        location_icon.setOnClickListener {
            if (centerLatLng != null) aMap.animateCamera(CameraUpdateFactory.changeLatLng(centerLatLng))
        }

        empty_hint.text = "对不起，没有搜索到相关数据！"
        recycle_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<PoiItem>(R.layout.item_map_list) { data, injector ->
                    injector.text(R.id.item_map_name, data.title)
                    injector.text(R.id.item_map_hint, data.snippet)

                            .with<TextView>(R.id.item_map_name) { view ->
                                @Suppress("DEPRECATION")
                                if (list.indexOf(data) == 0) view.setTextColor(resources.getColor(R.color.colorAccent))
                                else view.setTextColor(resources.getColor(R.color.black_dark))
                            }

                            .clicked(R.id.item_map) {
                                EventBus.getDefault().post(LocationMessageEvent(
                                        data.latLonPoint.latitude.toString(),
                                        data.latLonPoint.longitude.toString(),
                                        data.cityName + data.adName + data.title,
                                        "选择地址"))

                                putString("location_lat", data.latLonPoint.latitude.toString())
                                putString("location_lng", data.latLonPoint.longitude.toString())
                                putString("location_city", data.cityName)
                                if (et_locationkeyword.text.toString().isEmpty()) {
                                    onBackPressed()
                                } else {
                                    et_locationkeyword.setText("")
                                    fram_map.visibility = View.VISIBLE
                                    centerLatLng = LatLng(data.latLonPoint.latitude, data.latLonPoint.longitude)
                                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(centerLatLng))
                                }

                            }
                }
                .enableLoadMore(object : SlimMoreLoader(baseContext, SimpleLoadMoreViewCreator(baseContext).setNoMoreHint("没有更多数据了...")) {

                    override fun hasMore(): Boolean = true

                    override fun onLoadMore(handler: Handler) {
                        getData(pageNum)
                    }

                })
                .attachTo(recycle_list)
        //搜索
        et_locationkeyword.setOnEditorActionListener { v, actionId, event ->

            /*判断是否是“SEARCH”键*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /*隐藏软键盘*/
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
                query = PoiSearch.Query(et_locationkeyword.text.toString(), "餐饮服务|生活服务|汽车服务|汽车销售|餐饮服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|商务住宅|政府机构及社会团体|科教文化服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施", "")
                initPoiSearch()
                pageNum = 1
                getData(pageNum)
                fram_map.visibility = View.GONE
            }
            return@setOnEditorActionListener false
        }
        search_cancel.setOnClickListener {
            fram_map.visibility = View.VISIBLE
            et_locationkeyword.setText("")
            query = PoiSearch.Query("", "120000", "")
            initPoiSearch()
            pageNum = 1
            getData(pageNum)
        }
    }

    private fun initPoiSearch() {
//        query = PoiSearch.Query("", "120000", "")
        query.pageSize = 100
        poiSearch = PoiSearch(this, query)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {

            override fun onPoiItemSearched(poiItem: PoiItem, code: Int) {}

            override fun onPoiSearched(result: PoiResult?, code: Int) {
                if (code != AMapException.CODE_AMAP_SUCCESS) {
                    empty_view.visibility = View.VISIBLE
                    return
                }

                if (result != null && result.query != null) {
                    if (pageNum == 1) list.clear()
                    list.addItems(result.pois)
                    if (list.size > 0) pageNum++
                    mAdapter.updateData(list).notifyDataSetChanged()

                    empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                } else {
                    empty_view.visibility = View.VISIBLE
                }
                mMProgressDialog.dismiss()

            }

        })
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private fun addMarkerInScreenCenter() {
        val latLng = aMap.cameraPosition.target
        val screenPosition = aMap.projection.toScreenLocation(latLng)
        screenMarker = aMap.addMarker(MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_center)))
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker!!.setPositionByPixels(screenPosition.x, screenPosition.y)
    }

    /**
     * 屏幕中心marker 跳动
     */
    fun startJumpAnimation() {
        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            val latLng = screenMarker!!.position
            val point = aMap.projection.toScreenLocation(latLng)
            point.y -= DensityUtil.dp2px(100f)
            val target = aMap.projection.fromScreenLocation(point)
            //使用TranslateAnimation,填写一个需要移动的目标点
            val animation = TranslateAnimation(target)
            animation.setInterpolator { input ->
                // 模拟重加速度的interpolator
                if (input <= 0.5) (0.5f - 2.0 * (0.5 - input) * (0.5 - input)).toFloat()
                else (0.5f - Math.sqrt(((input - 0.5f) * (1.5f - input)).toDouble())).toFloat()
            }
            //整个移动所需要的时间
            animation.setDuration(500)
            //设置动画
            screenMarker!!.setAnimation(animation)
            //开始动画
            screenMarker!!.startAnimation()
        }
    }


    //搜索结果
    override fun onPoiSearched(result: PoiResult?, code: Int) {
        if (code != AMapException.CODE_AMAP_SUCCESS) {
            empty_view.visibility = View.VISIBLE
            return
        }
        if (result != null && result.query != null) {
            if (pageNum == 1) list.clear()
            list.addItems(result.pois)
            if (list.size > 0) pageNum++
            mAdapter.updateData(list).notifyDataSetChanged()

            empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
        } else {
            empty_view.visibility = View.VISIBLE
        }
    }

    fun startLocation() {
        AMapLocationHelper.getInstance(baseContext).clearCodes()
        AMapLocationHelper.getInstance(baseContext).startLocation(2) { location, isSuccessed, codes ->
            if (isSuccessed && 2 in codes) {
                centerLatLng = LatLng(location.latitude, location.longitude)
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(centerLatLng))
            }
        }
    }


    override fun getData(pindex: Int) {
        query.pageNum = pindex

        val latLng = aMap.cameraPosition.target

        poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(latLng.latitude, latLng.longitude), 5000000)
        poiSearch.searchPOIAsyn()
    }

    override fun onResume() {
        super.onResume()
        location_map.onResume()
        MobclickAgent.onPageStart("定位坐标")
    }

    override fun onPause() {
        super.onPause()
        location_map.onPause()
        MobclickAgent.onPageEnd("定位坐标")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        location_map.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        location_map.onDestroy()
    }

}
