package com.ruanmeng.tiger_treasure

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.BaseResponse
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.CompanyData
import com.ruanmeng.model.LocationMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_gov_modify.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*

/**
 * 政府号信息修改界面
 * Created by PYM2017 on 2018/1/26.
 */
class GovernmentModifyActivity : BaseActivity() {

    private var compLat = ""
    private var compLng = ""
    private var govLogo = ""
    private var govImg = ""
    private var companyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gov_modify)
        init_title("我的单位")
        EventBus.getDefault().register(this)

        getData()

    }

    override fun getData() {
        OkGo.post<BaseResponse<CompanyData>>(BaseHttp.company_details)
                .tag(this@GovernmentModifyActivity)
                .headers("token", getString("token"))
//                .params("companyId", intent.getStringExtra("companyId"))
                .execute(object : JacksonDialogCallback<BaseResponse<CompanyData>>(baseContext, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BaseResponse<CompanyData>>) {
                        val data = response.body().`object`
                        companyId = data.companyId
                        gov_name.setText(data.compName)
                        gov_phone.setText(data.compTel)
                        gov_address.setText(data.address)
                        gov_location.text = data.compLat + "，" + data.compLng
                        compLat = data.compLat
                        compLng = data.compLng
                        gov_business.setText(data.compService)
                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.compLogo)
                                .placeholder(R.mipmap.uploade) // 等待时的图片
                                .error(R.mipmap.uploade)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(gov_logo)

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.copmImage)
                                .placeholder(R.mipmap.uploade) // 等待时的图片
                                .error(R.mipmap.uploade)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(gov_image)
                    }
                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.btn_save_gov -> {
                save()
            }
            R.id.gov_location -> {
                intent.setClass(baseContext, LocationActivity::class.java)
                intent.putExtra("lan", if(compLat=="0") "" else compLat )
                intent.putExtra("lng", if(compLng=="0") "" else compLng)
                startActivity(intent)
            }
            R.id.gov_logo -> {
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .compress(true)
                        .compressSavePath(cacheDir.absolutePath)
                        .forResult(1)
            }
            R.id.gov_image -> {
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .theme(R.style.picture_customer_style)
                        .maxSelectNum(1)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                        .compress(true)
                        .compressSavePath(cacheDir.absolutePath)
                        .forResult(2)
            }
        }
    }

    fun save() {
        OkGo.post<String>(BaseHttp.company_update)
                .tag(this)
                .isMultipart(true)
                .apply {
                    params("companyId", companyId)
                    params("compName", gov_name.text.toString().trim())
                    params("compTel", gov_phone.text.toString().trim())
                    params("address", gov_address.text.toString().trim())
                    if (gov_address.text.toString().trim().isNotEmpty()
                            && compLat.isNotEmpty() && compLng.isNotEmpty()) {
                        params("compLat", compLat)
                        params("compLng", compLng)
                    }
                    params("compService", gov_business.text.toString())
                    if (govLogo.isNotEmpty()) params("compLogo_file", File(govLogo))
                    if (govImg.isNotEmpty()) params("copmImage_file", File(govImg))
                }
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        toast(msg)

                        ActivityStack.getScreenManager().popActivities(this@GovernmentModifyActivity::class.java)
                    }

                })
    }


    @Subscribe
    fun onMessageEvent(event: LocationMessageEvent) {
        when (event.type) {
            "选择地址" -> {
                gov_location.text = event.lat + "," + event.lng
                compLat = event.lat
                compLng = event.lng
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 || requestCode == 2) {
                var selectList = ArrayList<LocalMedia>()
                selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                if (selectList[0].isCompressed) {
                    if (requestCode == 1) govLogo = selectList[0].compressPath else govImg = selectList[0].compressPath
                    GlideApp.with(baseContext)
                            .load(if (requestCode == 1) govLogo else govImg)
                            .dontAnimate()
                            .centerCrop()
                            .into(if (requestCode == 1) gov_logo else gov_image)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}