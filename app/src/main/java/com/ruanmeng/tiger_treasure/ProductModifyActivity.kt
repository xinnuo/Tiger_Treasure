package com.ruanmeng.tiger_treasure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_product_modify.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import java.io.File

class ProductModifyActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_modify)
        init_title("编辑产品")

        selectList.add(LocalMedia().apply { compressPath = "" })
        (product_imgs.adapter as SlimAdapter).updateData(selectList).notifyDataSetChanged()
    }

    override fun init_title() {
        super.init_title()
        btRight.visibility = View.VISIBLE
        btRight.text = "保存"

        et_productmiao.setText(intent.getStringExtra("name"))
        et_price.setText(intent.getStringExtra("price"))
        btRight.setOnClickListener {
            if (et_productmiao.text.isBlank()) {
                toast("请输入产品描述！")
                et_productmiao.requestFocus()
                return@setOnClickListener
            }
            if (et_price.text.isEmpty()) {
                toast("请输入产品价格！")
                et_price.requestFocus()
                return@setOnClickListener
            }
            if (selectList.size < 2) {
                toast("请上传产品图片，最多3张")
                return@setOnClickListener
            }
            OkGo.post<String>(BaseHttp.supply_add)
                    .tag(this@ProductModifyActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .apply {
                        params("supplyDescribe", et_productmiao.text.trim().toString())
                        params("retailPrice", et_price.text.trim().toString())
                        params("supplyId", intent.getStringExtra("supplyId"))//只有编辑时候上传该字段

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
                            EventBus.getDefault().post(RefreshMessageEvent(0, "编辑产品"))//刷新广播   名字自定义
                            ActivityStack.getScreenManager().popActivities(this@ProductModifyActivity::class.java)//finish
                        }

                    })
        }

        product_imgs.apply {
            layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
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
                        }

                                .clicked(R.id.item_upload_img) {
                                    val items = ArrayList<LocalMedia>()
                                    items.addAll(selectList)

                                    PictureSelector.create(this@ProductModifyActivity)
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
                    (product_imgs.adapter as SlimAdapter).updateData(selectList).notifyDataSetChanged()
                }
            }
        }
    }
}
