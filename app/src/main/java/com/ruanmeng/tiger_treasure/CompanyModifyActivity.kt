package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.BaseBean
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CompanyData
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyGridLayoutManager
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_company_modify.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*

class CompanyModifyActivity : BaseActivity() {

    private var compLat = ""
    private var companyId = ""
    private var compLng = ""
    private var address = ""
    private var compType = ""
    private var areaId = ""
    private var industryId = ""
    private var staffNum = ""
    private var compLogo_img = ""
    private var copmImage_img = ""
    private var compLicense_img = ""//重新上传的营业执照
    private var compLicCurrent = ""//从服务器拿到的营业执照
    private var lat = ""
    private var lng = ""

    private val list = ArrayList<String>()
    private val list_key = ArrayList<String>()
    private val list_value = ArrayList<String>()
    private var selectList = ArrayList<LocalMedia>()
    private val list_yyzz = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_modify)
        init_title("我的公司")
        EventBus.getDefault().register(this@CompanyModifyActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
//        btRight.visibility = View.VISIBLE
//        btRight.text = "申请变更"
        company_range.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 4, GridLayoutManager.VERTICAL, false)
            adapter = SlimAdapter.create()
                    .register<String>(R.layout.item_tab_grid) { data, injector ->
                        injector.text(R.id.item_tab_name, data)
                                .clicked(R.id.item_tab_del, {
                                    list.remove(data)
                                    (company_range.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                                })
                    }
                    .attachTo(company_range)
        }

        et_sign.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /*隐藏软键盘*/
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)

                if (et_sign.text.isNotBlank()) {
                    list.add(et_sign.text.trim().toString())
                    (company_range.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()

                    et_sign.setText("")
                }
            }
            return@setOnEditorActionListener false
        }

        company_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.company_check1 -> staffNum = "SF_1"
                R.id.company_check2 -> staffNum = "SF_2"
                R.id.company_check3 -> staffNum = "SF_3"
                R.id.company_check4 -> staffNum = "SF_4"
                R.id.company_check5 -> staffNum = "SF_5"
            }
        }

//        btRight.setOnClickListener {
//            DialogHelper.showDialog(
//                    this,
//                    "提示",
//                    "企业信息变更需要重新上传营业执照，是否申请变更？",
//                    "取消",
//                    "确定",
//                    null) {
//                PictureSelector.create(this)
//                        .openGallery(PictureMimeType.ofImage())
//                        .theme(R.style.picture_customer_style)
//                        .maxSelectNum(1)
//                        .minSelectNum(1)
//                        .imageSpanCount(4)
//                        .compress(true)
//                        .compressSavePath(cacheDir.absolutePath)
//                        .selectionMedia(selectList.apply { clear() })
//                        .forResult(PictureConfig.TYPE_IMAGE)
//            }
//        }
    }

    override fun onStart() {
        super.onStart()
        com_ip.post(Runnable {
            kotlin.run {
                var param: LinearLayout.LayoutParams = com_email.layoutParams as LinearLayout.LayoutParams;
                param.width = com_ip.width
                com_email.layoutParams = param

                var param2: LinearLayout.LayoutParams = text_quyu_me.layoutParams as LinearLayout.LayoutParams;
                param2.width = com_ip.width
                text_quyu_me.layoutParams = param
            }
        })
    }

    override fun getData() {
        OkGo.post<BaseResponse<CompanyData>>(BaseHttp.company_details)
                .tag(this@CompanyModifyActivity)
                .headers("token", getString("token"))
//                .params("companyId", intent.getStringExtra("companyId"))
                .execute(object : JacksonDialogCallback<BaseResponse<CompanyData>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CompanyData>>) {

                        val data = response.body().`object`

                        company_qi.setText(data.compName)
                        company_fa.setText(data.legalMan)
                        company_zhu.setText(data.regAddress)
                        company_area.text = data.areaName
                        companyId = data.companyId
//                        company_jing.text = data.address
                        lat = data.compLat;
                        lng = data.compLng
                        company_jing.text = data.compLat + "，" + data.compLng
                        company_ying.setText(data.address)
                        company_nature.text = data.compTypeName
                        company_job.text = data.industryName
                        company_cheng.setText(data.registerDate)
                        when (data.staffNum) {
                            "SF_1" -> company_group.check(R.id.company_check1)
                            "SF_2" -> company_group.check(R.id.company_check2)
                            "SF_3" -> company_group.check(R.id.company_check3)
                            "SF_4" -> company_group.check(R.id.company_check4)
                            "SF_5" -> company_group.check(R.id.company_check5)
                            else -> company_group.check(-1)
                        }

                        company_zi.setText(data.regCap)
                        company_url.setText(data.compUrl)
                        company_email.setText(data.compEmail)
                        company_phone.setText(data.compTel)
                        et_com_scope.setText(data.compService)
                        et_comp_content.setText(data.copmProfile)

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.compLogo)
                                .placeholder(R.mipmap.uploade) // 等待时的图片
                                .error(R.mipmap.uploade)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(company_logo)

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.copmImage)
                                .placeholder(R.mipmap.uploade) // 等待时的图片
                                .error(R.mipmap.uploade)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(company_image)
                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.copmLic)
                                .placeholder(R.mipmap.uploade)
                                .error(R.mipmap.uploade)
                                .centerCrop()
                                .dontAnimate()
                                .into(enterprise_img)
                        compLicCurrent = BaseHttp.baseImg + data.copmLic


                        if (data.compService.isNotEmpty()) {
                            list.addAll(data.compService.split(","))
                            (company_range.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                        }

                        if (data.applyUpdate == "1") {
                            btRight.text = "申请中"
                            btRight.isEnabled = false
                            btRight.setBackgroundColor(resources.getColor(R.color.no_click))
                        }
                    }

                })
    }


    /**
     * 保存数据
     */
    fun save() {
        if (company_qi.text.isEmpty()) return

        if (company_url.text.isNotBlank()
                && !CommonUtil.isWeb(company_url.text.trim().toString())) {
            toast("企业网址格式错误，请重新输入")
            company_url.setText("")
            company_url.requestFocus()
            return
        }

        if (company_email.text.isNotBlank()
                && !CommonUtil.isEmail(company_email.text.trim().toString())) {
            toast("邮箱格式错误，请重新输入")
            company_email.setText("")
            company_email.requestFocus()
            return
        }

        if (company_phone.text.isEmpty() || company_phone.text.isNotBlank()
                && !CommonUtil.isTel(company_phone.text.trim().toString())) {
            toast("固定电话格式错误，请重新输入")
            company_phone.setText("")
            company_phone.requestFocus()
            return
        }

        OkGo.post<String>(BaseHttp.company_update)
                .tag(this@CompanyModifyActivity)
                .isMultipart(true)
                .apply {
                    if (areaId.isNotEmpty()) params("areaId", areaId)
                    if (address.isNotEmpty()) {
                        params("compLat", compLat)
                        params("compLng", compLng)
//                            params("address", company_ying.text.trim().toString())
                    }
                    params("compName", company_qi.text.trim().toString())
                    params("legalMan", company_fa.text.trim().toString())
                    params("registerDate", company_cheng.text.trim().toString())
                    params("regCap", company_zi.text.trim().toString())
                    params("regAddress", company_zhu.text.trim().toString())

                    params("address", company_ying.text.trim().toString())
                    if (compType.isNotEmpty()) params("compType", compType)
                    if (industryId.isNotEmpty()) params("industryId", industryId)
                    if (staffNum.isNotEmpty()) params("staffNum", staffNum)
                    if (compLogo_img.isNotEmpty()) params("compLogo_file", File(compLogo_img))
                    if (copmImage_img.isNotEmpty()) params("copmImage_file", File(copmImage_img))
                    if (compLicense_img.isNotEmpty()) params("copmLic_file", File(compLicense_img))

                    if (list.isNotEmpty()) {
                        val builder = StringBuilder()
                        list.forEach { builder.append(it).append(",") }
                        val str = builder.toString()
                        params("compService", str.subSequence(0, str.length - 1).toString())
                    }


                    params("compUrl", company_url.text.trim().toString())
                    params("compEmail", company_email.text.trim().toString())
                    params("compTel", company_phone.text.trim().toString())
                    //业务范围
                    if (et_com_scope.text.toString().isNotEmpty()) params("compService", et_com_scope.text.toString())
                    if (et_comp_content.text.toString().isNotEmpty()) params("copmProfile", et_comp_content.text.toString())
                    params("companyId", companyId)
                }
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        toast(msg)

                        ActivityStack.getScreenManager().popActivities(this@CompanyModifyActivity::class.java)
                    }

                })

    }

    /**
     * 申请企业变更
     */
    fun applyComChange() {

        OkGo.post<String>(BaseHttp.company_update)
                .tag(this@CompanyModifyActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .apply {
                    params("applyUpdate", 1)
                    params("copmLic_file", File(compLicense_img))
                    params("companyId", companyId)
                }
                .execute(object : StringDialogCallback(baseContext) {
                    override fun onSuccessResponse(response: Response<String>?, msg: String?, msgCode: String?) {
                        if (msg != null) {
                            toast(msg)
                        }
                        ActivityStack.getScreenManager().popActivities(this@CompanyModifyActivity::class.java)
                    }

                })

    }


    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.company_jing_ll -> {
                intent.setClass(baseContext, LocationActivity::class.java)
                intent.putExtra("lan", lat + "")
                intent.putExtra("lng", lng + "")
                startActivity(intent)
            }
            R.id.company_area_ll -> {
                intent.setClass(baseContext, ProvinceListActivity::class.java)
                intent.putExtra("isArea", true)
                startActivity(intent)
            }
            R.id.li_comp_type -> {
                OkGo.post<BaseBean<Map<String, String>>>(BaseHttp.compnature_list)
                        .tag(this@CompanyModifyActivity)
                        .execute(object : JsonDialogCallback<BaseBean<Map<String, String>>>(baseContext, true) {

                            override fun onSuccess(response: Response<BaseBean<Map<String, String>>>) {

                                @Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
                                val map_nature = (response.body() as LinkedHashMap<String, Any>)["object"] as LinkedHashMap<String, String>

                                list_key.clear()
                                list_value.clear()
                                map_nature.entries.forEach {
                                    list_key.add(it.key)
                                    list_value.add(it.value)
                                }

                                DialogHelper.showItemDialog(baseContext, "企业性质", list_value) { position, name ->
                                    compType = list_key[position]
                                    company_nature.text = name
                                }
                            }

                        })
            }
            R.id.enterprise_job_ll -> { //行业选择
                OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.industry_list)
                        .tag(this@CompanyModifyActivity)
                        .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                            override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                                DialogHelper.showWayDialog(baseContext, "所属行业", response.body().`object`) { id, name ->
                                    company_job.text = name
                                    industryId = id
                                }
                            }

                        })
            }
            R.id.enterprise_img -> {
                if (list_yyzz.isEmpty()) {
                    list_yyzz.add("查看大图")
                    list_yyzz.add("重新上传")
                }
                DialogHelper.showTypeBottomDialog(this, "请选择", list_yyzz) { position, name ->
                    when (position) {
                        0 -> {
                            var list = ArrayList<String>()
                            list.add(compLicCurrent)
                            MNImageBrowser.showImageBrowser(baseContext, enterprise_img, 0, list)
                        }
                        1 -> {
                            PictureSelector.create(this)
                                    .openGallery(PictureMimeType.ofImage())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .imageSpanCount(4)
                                    .compress(true)
                                    .compressSavePath(cacheDir.absolutePath)
                                    .selectionMedia(selectList.apply { clear() })
                                    .forResult(PictureConfig.TYPE_IMAGE)
                        }
                    }

                }
            }
            R.id.company_add -> {
                if (et_sign.text.isNotBlank()) {
                    list.add(et_sign.text.trim().toString())
                    (company_range.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
                    et_sign.setText("")
                }
            }
            R.id.company_logo -> {
                PictureSelector.create(this@CompanyModifyActivity)
                        // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .openGallery(PictureMimeType.ofImage())
                        // 主题样式(不设置则为默认样式)
                        .theme(R.style.picture_customer_style)
                        // 最大图片选择数量 int
                        .maxSelectNum(1)
                        // 最小选择数量 int
                        .minSelectNum(1)
                        // 每行显示个数 int
                        .imageSpanCount(4)
                        // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .selectionMode(PictureConfig.MULTIPLE)
                        // 是否可预览图片 true or false
                        .previewImage(true)
                        // 是否可预览视频 true or false
                        .previewVideo(false)
                        // 是否显示拍照按钮 true or false
                        .isCamera(true)
                        // 图片列表点击 缩放效果 默认true
                        .isZoomAnim(true)
                        // 自定义拍照保存路径,可不填
                        .setOutputCameraPath(Const.SAVE_FILE)
                        // 是否压缩 true or false
                        .compress(true)
                        // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .glideOverride(160, 160)
                        // 压缩图片保存地址
                        .compressSavePath(cacheDir.absolutePath)
                        // 是否显示gif图片 true or false
                        .isGif(false)
                        // 是否开启点击声音 true or false
                        .openClickSound(false)
                        // 是否传入已选图片 List<LocalMedia> list
                        .selectionMedia(selectList.apply { clear() })
                        // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .previewEggs(true)
                        // 小于100kb的图片不压缩
                        .minimumCompressSize(100)
                        // 结果回调onActivityResult code
                        .forResult(PictureConfig.REQUEST_CAMERA)
            }
            R.id.company_image -> {
                PictureSelector.create(this@CompanyModifyActivity)
                        // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .openGallery(PictureMimeType.ofImage())
                        // 主题样式(不设置则为默认样式)
                        .theme(R.style.picture_customer_style)
                        // 最大图片选择数量 int
                        .maxSelectNum(1)
                        // 最小选择数量 int
                        .minSelectNum(1)
                        // 每行显示个数 int
                        .imageSpanCount(4)
                        // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .selectionMode(PictureConfig.MULTIPLE)
                        // 是否可预览图片 true or false
                        .previewImage(true)
                        // 是否可预览视频 true or false
                        .previewVideo(false)
                        // 是否显示拍照按钮 true or false
                        .isCamera(true)
                        // 图片列表点击 缩放效果 默认true
                        .isZoomAnim(true)
                        // 自定义拍照保存路径,可不填
                        .setOutputCameraPath(Const.SAVE_FILE)
                        // 是否压缩 true or false
                        .compress(true)
                        // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .glideOverride(160, 160)
                        // 是否裁剪 true or false
                        .enableCrop(true)
                        // int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .withAspectRatio(4, 3)
                        // 是否显示uCrop工具栏，默认不显示 true or false
                        .hideBottomControls(true)
                        // 压缩图片保存地址
                        .compressSavePath(cacheDir.absolutePath)
                        // 裁剪框是否可拖拽 true or false
                        .freeStyleCropEnabled(false)
                        // 是否圆形裁剪 true or false
                        .circleDimmedLayer(false)
                        // 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropFrame(true)
                        // 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .showCropGrid(true)
                        // 是否显示gif图片 true or false
                        .isGif(false)
                        // 是否开启点击声音 true or false
                        .openClickSound(false)
                        // 是否传入已选图片 List<LocalMedia> list
                        .selectionMedia(selectList.apply { clear() })
                        // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .previewEggs(true)
                        // 小于100kb的图片不压缩
                        .minimumCompressSize(100)
                        // 结果回调onActivityResult code
                        .forResult(PictureConfig.CHOOSE_REQUEST)
            }
            R.id.btn_save -> {
                save()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    if (selectList[0].isCompressed) {
                        copmImage_img = selectList[0].compressPath
                        GlideApp.with(baseContext)
                                .load(copmImage_img)
                                .dontAnimate()
                                .centerCrop()
                                .into(company_image)
                    }
                }

                PictureConfig.REQUEST_CAMERA -> {
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    if (selectList[0].isCompressed) {
                        compLogo_img = selectList[0].compressPath
                        GlideApp.with(baseContext)
                                .load(compLogo_img)
                                .dontAnimate()
                                .centerCrop()
                                .into(company_logo)
                    }
                }
                PictureConfig.TYPE_IMAGE -> {
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    if (selectList[0].isCompressed) {
                        compLicense_img = selectList[0].compressPath
                        GlideApp.with(baseContext)
                                .load(compLicense_img)
                                .dontAnimate()
                                .centerCrop()
                                .into(enterprise_img)
                    }
                }
            }
        }
    }


    override fun finish() {
        EventBus.getDefault().unregister(this@CompanyModifyActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: LocationMessageEvent) {
        when (event.type) {
            "选择地址" -> {
                company_jing.text = event.lat + "," + event.lng
                company_ying.requestFocus()

                address = event.address
                compLat = event.lat
                compLng = event.lng
            }
            "选择城镇" -> {
                company_area.text = event.address

                areaId = event.lat
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("我的公司")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("我的公司")
    }
}
