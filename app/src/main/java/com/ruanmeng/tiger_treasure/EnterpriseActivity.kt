package com.ruanmeng.tiger_treasure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.toast
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_enterprise.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class EnterpriseActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()
    private var compLogo_img = ""
    private var compLic_img = ""
    private var company_img = ""
    private var address = ""
    private var compLat = ""
    private var compLng = ""
    private var areaLat = ""
    private var areaLng = ""
    private var areaId = ""
    private var compCode = ""
    private var industryId = ""
    private var staffNum = ""
    private var EnterpriseType = ""
    private val list_key = java.util.ArrayList<String>()
    private val list_value = java.util.ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprise)
        init_title("企业信息")
        getData()
        EventBus.getDefault().register(this@EnterpriseActivity)
    }

    override fun init_title() {
        super.init_title()
        enterprise_name.setText(intent.getStringExtra("compName"))
        enterprise_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.enterprise_check1 -> staffNum = "SF_1"
                R.id.enterprise_check2 -> staffNum = "SF_2"
                R.id.enterprise_check3 -> staffNum = "SF_3"
                R.id.enterprise_check4 -> staffNum = "SF_4"
                R.id.enterprise_check5 -> staffNum = "SF_5"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        text_ip.post(Runnable {
            kotlin.run {
                var param: LinearLayout.LayoutParams = text_email.layoutParams as LinearLayout.LayoutParams
                param.width = text_ip.width
                text_email.layoutParams = param

                var param2: LinearLayout.LayoutParams = text_quyu.layoutParams as LinearLayout.LayoutParams
                param2.width = text_ip.width
                text_quyu.layoutParams = param2
            }
        })
    }


    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.enterprise_logo -> {
                PictureSelector.create(this@EnterpriseActivity)
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
            R.id.enterprise_img -> {
                if (compLic_img.isEmpty()) {
                    PictureSelector.create(this@EnterpriseActivity)
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
                            // 是否显示拍照按钮 true or false
                            .isCamera(true)
                            // 自定义拍照保存路径,可不填
                            .setOutputCameraPath(Const.SAVE_FILE)
                            // 是否压缩 true or false
                            .compress(true)
                            // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                            .glideOverride(160, 160)
                            // 压缩图片保存地址
                            .compressSavePath(cacheDir.absolutePath)
                            // 结果回调onActivityResult code
                            .forResult(PictureConfig.CHOOSE_REQUEST)
                } else {
                    var list = ArrayList<String>()
                    list.add(compLic_img)
                    MNImageBrowser.showImageBrowser(baseContext, enterprise_img, 0, list)
                }
            }
            R.id.company_eimage -> {
                PictureSelector.create(this@EnterpriseActivity)
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
                        .forResult(88)
            }
            R.id.enterprise_address_ll -> {
                //不选择区域不能选择坐标
                if (enterprise_area.text.toString().isEmpty()) {
                    toast("请选择区域")
                    return
                }
                intent.setClass(baseContext, LocationActivity::class.java)
                intent.putExtra("lan", areaLat)
                intent.putExtra("lng", areaLng)
                startActivity(intent)
            }
            R.id.enterprise_area_ll -> {
                intent.setClass(baseContext, ProvinceListActivity::class.java)
                intent.putExtra("isArea", true)
                startActivity(intent)
            }
        /*  R.id.enterprise_job_ll->
          OkGo.post<BaseResponse<ArrayList<CommonData>>>(BaseHttp.industry_list)
                    .tag(this@EnterpriseActivity)
                    .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CommonData>>>(baseContext, true) {

                        override fun onSuccess(response: Response<BaseResponse<ArrayList<CommonData>>>) {
                            DialogHelper.showWayDialog(
                                    baseContext,
                                    "所属行业",
                                    response.body().`object`) { id, name ->
                                enterprise_job.text = name
                                industryId = id
                            }
                        }

                    })
            */
            R.id.delete_yyzz -> {
                delete_yyzz.visibility = View.GONE
                compLic_img = ""
                enterprise_img.setImageResource(R.mipmap.uploade)
            }
            R.id.bt_done -> {
                if (et_type.text.toString().isEmpty()) {
                    toast("请选择企业类型")
                    return
                }
                if (areaId.isEmpty()) {
                    toast("请选择所在区域")
                    return
                }

                if (compLat.isEmpty()) {
                    toast("请选择经营地址")
                    return
                }
                if (enterprise_phone.text.isEmpty()) {
                    toast("请填写企业联系方式")
                    return
                }
                if (compLic_img.isEmpty()) {
                    toast("请上传企业营业执照")
                    return
                }

                intent.setClass(baseContext, EnterpriseInfoActivity::class.java)
                intent.putExtra("compName", enterprise_name.text.trim().toString())
                intent.putExtra("legalMan", et_name.text.trim().toString())
                intent.putExtra("registerDate", enterprise_time.text.trim().toString())
                intent.putExtra("regAddress", enterprise_zaddress.text.trim().toString())
                intent.putExtra("compType", et_type.text.trim().toString())
                intent.putExtra("compLogo_img", compLogo_img)
                intent.putExtra("compLic_img", compLic_img)
                intent.putExtra("address", address + et_address.text.trim().toString())
                intent.putExtra("areaId", areaId)
                intent.putExtra("compLat", compLat)
                intent.putExtra("compLng", compLng)
                intent.putExtra("compProfile", et_content.text.trim().toString())
                intent.putExtra("compCode", compCode)
                intent.putExtra("industryId", industryId)
                intent.putExtra("compType", EnterpriseType)
                intent.putExtra("staffNum", staffNum)
                intent.putExtra("regCap", enterprise_zi.text.toString())
                intent.putExtra("compUrl", enterprise_url.text.toString())
                intent.putExtra("compEmail", enterprise_email.text.toString())
                intent.putExtra("compTel", enterprise_phone.text.toString())
                intent.putExtra("compService", et_scope.text.toString())
                intent.putExtra("copmImage_file", company_img)

                startActivity(intent)

            }
            R.id.li_comp_type -> {//企业性质
//                OkGo.post<BaseBean<Map<String, String>>>(BaseHttp.compnature_list)
//                        .tag(this@EnterpriseActivity)
//                        .execute(object : JsonDialogCallback<BaseBean<Map<String, String>>>(baseContext, true) {
//
//                            override fun onSuccess(response: Response<BaseBean<Map<String, String>>>) {
//
//                                @Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
//                                val map_nature = (response.body() as LinkedHashMap<String, Any>)["object"] as LinkedHashMap<String, String>
//
//                                list_key.clear()
//                                list_value.clear()
//                                map_nature.entries.forEach {
//                                    list_key.add(it.key)
//                                    list_value.add(it.value)
//                                }
//
//                                DialogHelper.showItemDialog(baseContext, "企业性质", list_value) { position, name ->
//                                    EnterpriseType=list_key[position]
//                                    et_type.text = name
//                                }
//                            }
//
//                        })

            }
        }

    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.company_info)
                .tag(this@EnterpriseActivity)
                .isMultipart(true)
                .params("keyword", enterprise_name.text.trim().toString())
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val data = JSONObject(response.body()).getJSONObject("object")
                        et_name.setText(data.getString("legalMan"))
                        enterprise_name.setText(data.getString("compName"))
                        enterprise_time.setText(data.getString("registerDate"))
                        enterprise_statue.setText(data.getString("compStatus"))
                        enterprise_zaddress.setText(data.getString("regAddress"))
                        et_type.setText(data.getString("compTypeText"))
                        enterprise_job.text = data.getString("industryName")

                        enterprise_zi.setText(data.getString("regCap"))
                        enterprise_url.setText(data.getString("compUrl"))
                        enterprise_email.setText(data.getString("compEmail"))
                        enterprise_phone.setText(data.getString("compTel"))
                        et_scope.setText(data.getString("scope"))

                        compCode = data.getString("compCode")
                        industryId = data.getString("industryId")
                    }

                })
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
                        compLic_img = selectList[0].compressPath
                        GlideApp.with(baseContext)
                                .load(compLic_img)
                                .dontAnimate()
                                .centerCrop()
                                .into(enterprise_img)
                        delete_yyzz.visibility = View.VISIBLE
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
                                .into(enterprise_logo)
                    }
                }
                88 -> {
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    if (selectList[0].isCompressed) {
                        company_img = selectList[0].compressPath
                        GlideApp.with(baseContext)
                                .load(company_img)
                                .dontAnimate()
                                .centerCrop()
                                .into(company_eimage)
                    }
                }
            }
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@EnterpriseActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: LocationMessageEvent) {
        when (event.type) {
            "选择地址" -> {
                et_address.requestFocus()

                address = event.address
                compLat = event.lat
                compLng = event.lng
                enterprise_address.text = compLat + "," + compLng
            }
            "选择城镇" -> {
                enterprise_area.text = event.address
                var location = event.lng
                //117.013469,30.553697
                var str = location.split(",")
                areaLat = str[1]
                areaLng = str[0]
                areaId = event.lat

            }
        }
    }



    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("企业认证（确认信息）")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("企业认证（确认信息）")
    }
}
