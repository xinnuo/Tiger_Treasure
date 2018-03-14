package com.ruanmeng.tiger_treasure

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.MessageData
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_release_message.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by PYM2017 on 2018/1/24.
 */
class ReleaseMessageActivity : BaseActivity() {

    private var ggImg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release_message)
        init_title("发布公告")

        if (intent.getStringExtra("messageInfoId").isNotEmpty()) {
            init_title("编辑公告")
            getEditData()
        }

    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.ll_stop_date -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)
                DialogHelper.showDateDialog(baseContext,
                        year_now,
                        year_now + 2,
                        3,
                        "截止时间",
                        true,
                        false) { _, _, _, _, _, date ->
                    text_gg_date.text = date
                }
            }
            R.id.img_gg -> {
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .compress(true)
                        .compressSavePath(cacheDir.absolutePath)
                        .forResult(PictureConfig.TYPE_IMAGE)
            }
            R.id.bt_done -> {
                release()
            }
        }
    }

    fun release() {
        OkGo.post<BaseResponse<String>>(BaseHttp.release_government_list)
                .tag(this@ReleaseMessageActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("title", gg_edit_title.text.toString())
                .params("content", gg_edit_content.text.toString())
                .params("expiryDate", text_gg_date.text.toString())
                .apply {
                    if (ggImg.isNotEmpty()) params("coverImg_file", File(ggImg))
                    if (intent.getStringExtra("messageInfoId").isNotEmpty())
                        params("messageInfoId", intent.getStringExtra("messageInfoId"))
                }
                .execute(object : JsonDialogCallback<BaseResponse<String>>(this) {
                    override fun onSuccess(response: Response<BaseResponse<String>>?) {
                        toast("发布成功")
                        MobclickAgent.onEvent(this@ReleaseMessageActivity, "send_cooperation_success")
                        EventBus.getDefault().post(RefreshMessageEvent(2, "发布信息"))
                        ActivityStack.getScreenManager().popActivities(this@ReleaseMessageActivity::class.java)
                    }

                })

    }

    fun getEditData() {
        OkGo.post<BaseResponse<MessageData>>(BaseHttp.message_details)
                .tag(this@ReleaseMessageActivity)
                .isMultipart(true)
                .params("messageInfoId", intent.getStringExtra("messageInfoId"))
                .execute(object : JsonDialogCallback<BaseResponse<MessageData>>(this) {
                    override fun onSuccess(response: Response<BaseResponse<MessageData>>) {
                        val data = response.body().`object`
                        gg_edit_title.setText(data.title)
                        gg_edit_content.setText(data.content)
                        text_gg_date.text = data.expiryDate
                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.coverImg)
                                .dontAnimate()
                                .centerCrop()
                                .into(img_gg)
                    }

                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureConfig.TYPE_IMAGE && resultCode == Activity.RESULT_OK) {
            var selectList = ArrayList<LocalMedia>()
            selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
            if (selectList[0].isCompressed) {
                ggImg = selectList[0].compressPath
                GlideApp.with(baseContext)
                        .load(ggImg)
                        .dontAnimate()
                        .centerCrop()
                        .into(img_gg)
            }
        }
    }
}