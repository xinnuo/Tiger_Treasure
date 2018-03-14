package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.ProductData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.view.FullyGridLayoutManager
import com.ruanmeng.view.MultiGapDecoration
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_issue_first.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class IssueFirstActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()
    private var selectList_withhttp = ArrayList<LocalMedia>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_first)
        init_title("发布供货信息")
        selectList.add(LocalMedia().apply { compressPath = "" })
        if (intent.getStringExtra("mine") != null) {
            getData()//我的发布-编辑-详情
        } else {
            (issue_imgs.adapter as SlimAdapter).updateData(selectList).notifyDataSetChanged()
        }
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "发布"

        issue_imgs.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 3)
            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
            adapter = SlimAdapter.create()
                    .register<LocalMedia>(R.layout.item_upload_grid) { data, injector ->
                        injector.with<ImageView>(R.id.item_upload_img) { view ->
                            GlideApp.with(baseContext)
                                    .load(data.compressPath)
                                    .placeholder(R.mipmap.uploade) //等待时的图片
                                    .error(R.mipmap.uploade)       //加载失败的图片
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(view)
                        }.clicked(R.id.item_upload_img) {
                            selectList_withhttp.clear()//重新选择
                            val items = ArrayList<LocalMedia>()
                            items.addAll(selectList)

                            PictureSelector.create(this@IssueFirstActivity)
                                    // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                    .openGallery(PictureMimeType.ofImage())
                                    // 主题样式(不设置则为默认样式)
                                    .theme(R.style.picture_customer_style)
                                    // 最大图片选择数量 int
                                    .maxSelectNum(3)
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
                                    .selectionMedia(items.apply {
                                        when (size) {
                                            3 -> if (get(2).compressPath.isEmpty()) removeAt(2)
                                            else -> removeAt(size - 1)
                                        }
                                    })
                                    // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                    .previewEggs(true)
                                    // 小于100kb的图片不压缩
                                    .minimumCompressSize(100)
                                    // 结果回调onActivityResult code
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                    }
        }
        et_ling.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val temp = p0.toString()
                val posDot = temp.indexOf(".")
                if (posDot <= 0) return
                if (temp.length - posDot - 1 > 2) {
                    if (p0 != null) {
                        et_ling.setText(p0.substring(0, posDot + 3))
                        et_ling.setSelection(et_ling.length())
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    /**
     * 我的发布-编辑-详情
     */
    override fun getData() {
        OkGo.post<BaseResponse<ProductData>>(BaseHttp.supply_details)
                .tag(this@IssueFirstActivity)
                .headers("token", getString("token"))
                .params("supplyId", intent.getStringExtra("supplyId"))
                .execute(object : JsonDialogCallback<BaseResponse<ProductData>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<ProductData>>) {
                        val data = response.body().`object`
                        val imgs = data.supplyPics.split(",")
                        et_name.setText(data.supplyTitle)
                        et_miao.setText(data.supplyDescribe)
                        et_count.setText(data.supplyNum + "")
                        issue_date_tv.text = data.expiryDate
                        et_pi.setText(data.tradePrice + "")
                        et_ling.setText(data.retailPrice + "")
                        if (imgs.isNotEmpty()) {
                            imgs.forEach {
                                selectList_withhttp.add(LocalMedia().apply { compressPath = BaseHttp.baseImg + it })
                            }
                        } else {
                            selectList_withhttp.add(LocalMedia().apply { compressPath = "" })
                        }
                        if (selectList_withhttp.size < 3) selectList_withhttp.add(LocalMedia().apply { compressPath = "" })
                        (issue_imgs.adapter as SlimAdapter).updateData(selectList_withhttp).notifyDataSetChanged()
                        if (data.spection1.isNotEmpty()) {
                            ll_param_one.visibility = View.VISIBLE
                            param_name_one.setText(data.spection1.split(":")[0])
                            param_content_one.setText(data.spection1.split(":")[1])
                        }
                        if (data.spection2.isNotEmpty()) {
                            ll_param_two.visibility = View.VISIBLE
                            param_name_two.setText(data.spection2.split(":")[0])
                            param_content_two.setText(data.spection2.split(":")[1])
                        }
                        if (data.spection3.isNotEmpty()) {
                            ll_param_three.visibility = View.VISIBLE
                            param_name_three.setText(data.spection3.split(":")[0])
                            param_content_three.setText(data.spection3.split(":")[1])
                        }

                        if (data.spection4.isNotEmpty()) {
                            ll_param_three.visibility = View.VISIBLE
                            param_name_four.setText(data.spection4.split(":")[0])
                            param_content_four.setText(data.spection4.split(":")[1])
                        }
                        if (data.spection5.isNotEmpty()) {
                            ll_param_five.visibility = View.VISIBLE
                            param_name_five.setText(data.spection5.split(":")[0])
                            param_content_five.setText(data.spection5.split(":")[1])
                        }

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

                    if (selectList.size < 3) selectList.add(LocalMedia().apply { compressPath = "" })
                    (issue_imgs.adapter as SlimAdapter).updateData(selectList).notifyDataSetChanged()
                }
            }
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.issue_date -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                DialogHelper.showDateDialog(baseContext,
                        year_now,
                        year_now + 2,
                        3,
                        "截止时间",
                        true,
                        false) { _, _, _, _, _, date ->
                    issue_date_tv.text = date
                }
            }
            R.id.ll_param -> {
                if (expand_param.visibility == View.GONE) {
                    expand_param.visibility = View.VISIBLE
                } else {
                    expand_param.visibility = View.GONE
                }
            }
            R.id.add_param -> {
                if (ll_param_one.visibility == View.GONE) {
                    ll_param_one.visibility = View.VISIBLE
                    return
                }
                if (ll_param_two.visibility == View.GONE) {
                    ll_param_two.visibility = View.VISIBLE
                    return
                }
                if (ll_param_three.visibility == View.GONE) {
                    ll_param_three.visibility = View.VISIBLE
                    return
                }
                if (ll_param_four.visibility == View.GONE) {
                    ll_param_four.visibility = View.VISIBLE
                    return
                }
                if (ll_param_five.visibility == View.GONE) {
                    ll_param_five.visibility = View.VISIBLE
                    add_param.visibility = View.GONE
                    return
                }
            }
            R.id.param_del_one -> {
                ll_param_one.visibility = View.GONE
                add_param.visibility = View.VISIBLE
                param_name_one.setText("")
                param_content_one.setText("")
            }
            R.id.param_del_two -> {
                ll_param_two.visibility = View.GONE
                add_param.visibility = View.VISIBLE
                param_name_two.setText("")
                param_content_two.setText("")
            }
            R.id.param_del_three -> {
                ll_param_three.visibility = View.GONE
                add_param.visibility = View.VISIBLE
                param_name_three.setText("")
                param_content_three.setText("")
            }
            R.id.param_del_four -> {
                ll_param_four.visibility = View.GONE
                add_param.visibility = View.VISIBLE
                param_name_four.setText("")
                param_content_four.setText("")
            }
            R.id.param_del_five -> {
                ll_param_five.visibility = View.GONE
                add_param.visibility = View.VISIBLE
                param_name_five.setText("")
                param_content_five.setText("")
            }

            R.id.btn_nav_right -> {
                if (et_name.text.isBlank()) {
                    toast("请输入产品名称")
                    et_name.requestFocus()
                    return
                }
                if (et_miao.text.isBlank()) {
                    toast("请输入产品描述")
                    et_miao.requestFocus()
                    return
                }

                if (et_count.text.isBlank()) {
                    toast("请输入产品数量")
                    et_count.requestFocus()
                    return
                }

//                if (issue_date_tv.text.isBlank()) {
//                    toast("请选择截止日期")
//                    return
//                }

//                if (et_pi.text.isBlank()) {
//                    toast("请输入批发价格")
//                    et_pi.requestFocus()
//                    return
//                }
//
                if (et_ling.text.isBlank()) {
                    toast("请输入价格")
                    et_ling.requestFocus()
                    return
                }

                if (intent.getStringExtra("mine") == "" && selectList.size < 2) {
                    toast("请上传产品图片，最多3张")
                    return
                }

                OkGo.post<String>(BaseHttp.supply_add)
                        .tag(this@IssueFirstActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .apply {
                            if (intent.getStringExtra("mine") != "") {
                                params("supplyId", intent.getStringExtra("supplyId"))//编辑的时候把id提交上去
                            }
                            params("supplyDescribe", et_miao.text.trim().toString())
                            params("supplyNum", et_count.text.trim().toString())
                            params("supplyTitle", et_name.text.trim().toString())
//                            params("expiryDate", issue_date_tv.text.toString())
//                            params("tradePrice", et_pi.text.trim().toString())
                            params("retailPrice", et_ling.text.trim().toString())
                            if (param_name_one.text.toString().trim() != "" && param_content_one.text.toString().trim() != "")
                                params("spection1", "${param_name_one.text.toString().trim()}:${param_content_one.text.toString().trim()}", false)
                            if (param_name_two.text.toString().trim() != "" && param_content_two.text.toString().trim() != "")
                                params("spection2", "${param_name_two.text.toString().trim()}:${param_content_two.text.toString().trim()}", false)
                            if (param_name_three.text.toString().trim() != "" && param_content_three.text.toString().trim() != "")
                                params("spection3", "${param_name_three.text.toString().trim()}:${param_content_three.text.toString().trim()}", false)
                            if (param_name_four.text.toString().trim() != "" && param_content_four.text.toString().trim() != "")
                                params("spection4", "${param_name_four.text.toString().trim()}:${param_content_four.text.toString().trim()}", false)
                            if (param_name_five.text.toString().trim() != "" && param_content_five.text.toString().trim() != "")
                                params("spection5", "${param_name_five.text.toString().trim()}:${param_content_five.text.toString().trim()}", false)

                            when (selectList.size) {
                                2 -> params("supplyPic_file1", File(selectList[0].compressPath))
                                3 -> {
                                    params("supplyPic_file1", File(selectList[0].compressPath))
                                    params("supplyPic_file2", File(selectList[1].compressPath))
                                    if (selectList[2].compressPath.isNotEmpty())
                                        params("supplyPic_file3", File(selectList[2].compressPath))
                                }
                            }
                        }
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                MobclickAgent.onEvent(this@IssueFirstActivity, "send_supply_success")

                                EventBus.getDefault().post(RefreshMessageEvent(0, "发布信息"))
                                ActivityStack.getScreenManager().popActivities(this@IssueFirstActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("发布供货信息")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("发布供货信息")
    }

}
