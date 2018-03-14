package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.PersonMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyGridLayoutManager
import com.umeng.analytics.MobclickAgent
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_info.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class InfoActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()
    private var userhead_img = ""
    private val list = ArrayList<String>()
    val list_interest = java.util.ArrayList<CommonData>()
    private var userName = ""
    private var age = "0"
    private var sex = "1"
    private var positionName = ""
    private var telephoneIsOpen = 0
    val str_builder = StringBuilder()
    private var names = ""
    var interestList = java.util.ArrayList<CommonData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        init_title("个人资料")
        interestList = intent.getSerializableExtra("interestList") as ArrayList<CommonData>
        EventBus.getDefault().register(this@InfoActivity)
        if (interestList.size > 0) {
            interestList.forEach { str_builder.append(it.chatRoomName).append("、") }
            names = if (str_builder.lastIndexOf("、") == str_builder.length - 1) {
                str_builder.substring(0, str_builder.length - 1)
            } else {
                str_builder.toString()
            }
            list.clear()
            list.addAll(names.split("、"))
            (company_interestrange.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
        }
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.INVISIBLE
        btRight.text = "保存"
        if (getString("accountType") == "App_Manager") {
            info_interest.visibility = View.GONE
        }
        info_img_ll.setOnClickListener {
            PictureSelector.create(this@InfoActivity)
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
                    .withAspectRatio(1, 1)
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
        info_name.setOnClickListener { startActivity(ModifyNameActivity::class.java) }
        info_age.setOnClickListener { startActivity(ModifyAgeActivity::class.java) }
        info_job.setOnClickListener { startActivity(ModifyDutyActivity::class.java) }
        info_phone.setOnClickListener { startActivity(ModifyPhoneActivity::class.java) }
        info_address.setOnClickListener { startActivity(ModifyAddressActivity::class.java) }
        //兴趣爱好
        info_interest.setOnClickListener {
            val intent = Intent(baseContext, InterestingActivity::class.java)
            intent.putExtra("interestList", interestList)
            startActivity(intent)
        }
        userName = getString("userName")
        if (getString("age").isNotEmpty()) age = getString("age")
        if (getString("sex").isNotEmpty()) sex = getString("sex")
        positionName = getString("positionName")

        GlideApp.with(baseContext)
                .load(BaseHttp.baseImg + getString("userhead"))
                .placeholder(R.mipmap.default_user)
                .error(R.mipmap.default_user)
                .dontAnimate()
                .into(enterprise_img)
        info_name.setRightString(getString("userName"))
        info_sex.setRightString(
                when (getString("sex")) {
                    "0" -> "女"
                    else -> "男"
                }
        )
        info_age.setRightString(
                when (getString("age")) {
                    "" -> "0岁"
                    else -> getString("age") + "岁"
                })
        info_job.setRightString(getString("positionName"))
        info_phone.setRightString(getString("telephone"))
        when (getString("telephoneIsOpen")) {
            "1" -> {
                telephoneIsOpen = 1
                info_switch.isChecked = true
            }
            else -> {
                telephoneIsOpen = 0
                info_switch.isChecked = false
            }
        }

        info_sex.setOnClickListener {
            DialogHelper.showItemDialog(baseContext, "选择性别", Arrays.asList("男", "女")) { position, name ->
                info_sex.setRightString(name)
                sex = if (position == 0) "1" else "0"

                updateMessage()
            }
        }
        info_switch.setOnCheckedChangeListener { _, isChecked ->
            telephoneIsOpen = if (isChecked) 1 else 0

            updateMessage()
        }

        btRight.setOnClickListener {
            updateMessage()
        }
        //个人爱好
        company_interestrange.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 4, GridLayoutManager.VERTICAL, false)
            adapter = SlimAdapter.create()
                    .register<String>(R.layout.item_tab_grid) { data, injector ->
                        injector.text(R.id.item_tab_name, data)
                        .gone(R.id.item_tab_del)

                    }
                    .attachTo(company_interestrange)
        }
    }

    private fun updateMessage() {
        OkGo.post<String>(BaseHttp.accountinfo_update)
                .tag(this@InfoActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .apply {
                    if (userhead_img.isNotEmpty()) params("userhead_file", File(userhead_img))
                    if (userName.isNotEmpty()) params("userName", userName)
                    params("age", age)
                    params("sex", sex)
                    params("positionName", positionName)
                    params("telephoneIsOpen", telephoneIsOpen)
                }
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
//                        toast(msg)

                        val obj = JSONObject(response.body()).getJSONObject("object")
                        if (!obj.isNull("userhead")) putString("userhead", obj.getString("userhead"))

                        RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                getString("token"),
                                getString("userName"),
                                Uri.parse(BaseHttp.baseImg + getString("userhead"))))
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
                        userhead_img = selectList[0].compressPath
                        GlideApp.with(baseContext)
                                .load(userhead_img)
                                .dontAnimate()
                                .into(enterprise_img)

                        updateMessage()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().unregister(this@InfoActivity)
    }

    @Subscribe
    fun onMessageEvent(event: PersonMessageEvent) {
        when (event.type) {
            1 -> {
                userName = event.name
                info_name.setRightString(event.name)
            }
            2 -> {
                age = event.name
                info_age.setRightString(event.name + "岁")
            }
            3 -> {
                positionName = event.name
                info_job.setRightString(event.name)
            }
            4 -> {
                getData()
//                list.clear()
//                list.addAll(event.name.split("、"))
//                (company_interestrange.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()
            }
        }
    }
    override fun getData() {
        OkGo.post<String>(BaseHttp.accountinfo_details)
                .tag(this@InfoActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body()).getJSONObject("object")
                        //兴趣爱好
                        interestList.clear()
                        val arr = JSONArray(obj.getString("chartRooms"))
                        (0 until arr.length()).mapTo(interestList) {
                            CommonData().apply {
                                chatRoomId = arr.getJSONObject(it).getString("chatRoomId")
                                chatRoomName = arr.getJSONObject(it).getString("chatRoomName")
                            }
                        }

                    }

                })
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("个人资料修改")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("个人资料修改")
    }
}