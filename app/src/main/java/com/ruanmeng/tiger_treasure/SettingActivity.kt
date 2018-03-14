package com.ruanmeng.tiger_treasure

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import com.luck.picture.lib.tools.PictureFileUtils
import com.lzy.extend.BaseResponse
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.*
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.utils.DeviceConfig.context
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.utils.AppUpdateUtils.getVersionName
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.download
import com.vector.update_app_kotlin.updateApp
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_version.setRightString(getVersionName(this))
        bt_quit.setOnClickListener {
            DialogHelper.showDialog(
                    baseContext,
                    "退出登录",
                    "确定要退出登录吗？",
                    "取消",
                    "退出",
                    null) {

                val intent = Intent(baseContext, LoginActivity::class.java)
                intent.putExtra("offLine", true)
                startActivity(intent)
            }
        }
        /**意见反馈*/
        setting_help.setOnClickListener {
            val intent = Intent(baseContext, FeedBackActivity::class.java)
            startActivity(intent)
        }
        setting_send.setOnClickListener {
            var sdk = android.os.Build.VERSION.SDK         //SDK号
            var model = android.os.Build.MODEL             //手机型号
            var release = android.os.Build.VERSION.RELEASE //系统版本号
            val brand = Build.BRAND                        //手机厂商

            when(brand.toLowerCase()) {
                "huawei", "honor" -> gotoHuaweiPermission()
                "redmi", "xiaomi" -> gotoMiuiPermission()
                "meizu" -> gotoMeizuPermission()
                else -> getAppDetailSettingIntent(baseContext)
            }
        }
        third_jie.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "关于我们")
            startActivity(intent)
        }
        /**隐私政策*/
        set_privacy.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "隐私政策")
            startActivity(intent)
        }
        /**隐私政策*/
        set_disclaimer.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "免责声明")
            startActivity(intent)
        }
        setting_version.setOnClickListener {
            checkUpdate()
        }
        //缓存
        setting_clear.setRightString(GlideCacheUtil.getInstance().getCacheSize(this@SettingActivity))
        setting_clear.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("清空缓存")
                    .setMessage("确定要清空缓存吗？")
                    .setPositiveButton("清空") { dialog, _ ->
                        dialog.dismiss()

                        GlideCacheUtil.getInstance().clearImageAllCache(baseContext)
                        PictureFileUtils.deleteCacheDirFile(baseContext)
                        setting_clear.setRightString("0.0B")
                    }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
        //修改密码
        setting_changepsw.setOnClickListener{
            val intent = Intent(baseContext, ChangePswActivity::class.java)
            intent.putExtra("title", "修改密码")
            startActivity(intent)
        }
    }

    private fun getAppDetailSettingIntent(context: Context) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")//华为权限管理
            intent.component = comp
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<BaseResponse<CommonModel>>(BaseHttp.message_list)
                .tag(this@SettingActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("messageType", mPosition)
                .execute(object : JacksonDialogCallback<BaseResponse<CommonModel>>(baseContext) {

                    override fun onSuccess(response: Response<BaseResponse<CommonModel>>) {

                    }


                })
    }
    private fun checkUpdate() {
        //下载路径
        val path = Environment.getExternalStorageDirectory().absolutePath + Const.SAVE_FILE
        //自定义参数
        val params = HashMap<String, String>()

        updateApp(BaseHttp.version, OkGoUpdateHttpUtil()) {
            //设置请求方式，默认get
            isPost = true
            //添加自定义参数
            setParams(params)
            //设置apk下砸路径
            targetPath = path
        }.check {
            onBefore { showLoadingDialog() }
            parseJson {
                val obj = JSONObject(it).getJSONObject("object")
                val version_new = Integer.parseInt(obj.optString("versionNo").replace(".", ""))
                val version_old = Integer.parseInt(Tools.getVersion(baseContext).replace(".", ""))

                UpdateAppBean()
                        //（必须）是否更新Yes,No
                        .setUpdate(if (version_new > version_old) "Yes" else "No")
                        //（必须）新版本号，
                        .setNewVersion(obj.optString("versionNo"))
                        //（必须）下载地址
                        .setApkFileUrl(obj.optString("url"))
                        // .setApkFileUrl("https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/app-debug.apk

//                ")
                        //（必须）更新内容
                        .setUpdateLog(obj.optString("content"))
                        //是否强制更新，可以不设置
                        .setConstraint(false)
            }
            hasNewApp { updateApp, updateAppManager -> showDownloadDialog(updateApp, updateAppManager) }
            noNewApp { toast("当前已是最新版本！") }
            onAfter { cancelLoadingDialog() }
        }
    }

    /**
     * 自定义对话框
     */
    private fun showDownloadDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        dialog("版本更新", "是否升级到${updateApp.newVersion}版本？\n\n${updateApp.updateLog}") {
            positiveButton("升级") {
                //                updateAppManager.download()
                updateAppManager.download {
                    onStart { HProgressDialogUtils.showHorizontalProgressDialog(this@SettingActivity, "下载进度", false) }
                    onProgress { progress, _ -> HProgressDialogUtils.setProgress(Math.round(progress * 100)) }
                    onFinish {
                        HProgressDialogUtils.cancel()
                        true
                    }
                    onError {
                        toast(it)
                        HProgressDialogUtils.cancel()
                    }
                }

            }
            cancellable(!updateApp.isConstraint)
            negativeButton("暂不升级") { dismiss() }
            show()
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private fun gotoMiuiPermission() {
        try { // MIUI 8
            val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            localIntent.putExtra("extra_pkgname", context.packageName)
            context.startActivity(localIntent)
        } catch (e: Exception) {
            try { // MIUI 5/6/7
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                localIntent.putExtra("extra_pkgname", context.packageName)
                context.startActivity(localIntent)
            } catch (e1: Exception) { // 否则跳转到应用详情
                getAppDetailSettingIntent(baseContext)
            }

        }

    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private fun gotoMeizuPermission() {
        try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            getAppDetailSettingIntent(baseContext)
        }

    }

    /**
     * 华为的权限管理页面
     */
    private fun gotoHuaweiPermission() {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity") //华为权限管理
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            getAppDetailSettingIntent(baseContext)
        }

    }
    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart("设置")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd("设置")
    }

}
