package com.ruanmeng.tiger_treasure

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import android.view.View
import android.widget.CompoundButton
import com.lzy.extend.BaseResponse
import com.lzy.extend.JsonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.RongCloudContext
import com.ruanmeng.base.*
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.model.CityNewData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.*
import com.umeng.analytics.MobclickAgent
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.download
import com.vector.update_app_kotlin.updateApp
import io.rong.imkit.RongIM
import io.rong.imkit.manager.IUnReadMessageObserver
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import q.rorbin.badgeview.QBadgeView

class MainActivity : BaseActivity() {

    private var isConnected = false
    private var badgeView: QBadgeView? = null
    private val list = ArrayList<Any>()
    private var isForce = false
    private var vipLockDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()
        main_check1.performClick()
        checkUpdate()
        checkVipLock()
    }

    //app异常崩溃后，防止fragment重叠现象
    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        if (getBoolean("isLogin") && getString("rongToken") != "") {
            if (!isConnected) connect(getString("rongToken"))
        }
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)

        if (getString("accountType") == "App_Manager")
            main_check2.visibility = View.GONE
    }


    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
            if (buttonView == main_check2) {
                (fragment as MainSecondFragment).getData()
            }
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MainFirstFragment()
            R.id.main_check2 -> MainSecondFragment()
            R.id.main_check3 -> MainThirdFragment()
            else -> MainFirstFragment()
        }

        override fun getCount(): Int = 3
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.first_city -> {
                getData("")
            }
            R.id.first_search -> startActivity(SearchActivity::class.java)
            R.id.first_msg -> startActivity(MessageActivity::class.java)
            R.id.first_more -> {
                val intent = Intent(baseContext, MessageActivity::class.java)
                intent.putExtra("isNotice", true)
                startActivity(intent)
            }
            R.id.first_qi -> {
                main_check2.performClick()
                MobclickAgent.onEvent(this@MainActivity, "home_one_click")
            }
            R.id.first_vip -> {
                startActivity(MemberMineActivity::class.java)
                MobclickAgent.onEvent(this@MainActivity, "home_vip_click")
            }
            R.id.first_huo -> {
                startActivity(MemberActivity::class.java)
                MobclickAgent.onEvent(this@MainActivity, "home_three_click")
            }
            R.id.first_quan -> {
                startActivity(CircleActivity::class.java)
                MobclickAgent.onEvent(this@MainActivity, "home_four_click")
            }

            R.id.second_left -> {
                startActivity(ContactActivity::class.java)
                MobclickAgent.onEvent(this, "addressbook_click")
            }
            R.id.second_right -> {
                startActivity(MomentsActivity::class.java)
                MobclickAgent.onEvent(this, "pyq_click")
            }
        }
    }


    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                toast("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                onBackPressed()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getUnReadCount() {
        RongIM.getInstance().addUnReadMessageCountChangedObserver(IUnReadMessageObserver { count ->
            if (count > 0) {
                if (badgeView == null)
                    badgeView = QBadgeView(baseContext)
                badgeView!!.visibility = View.VISIBLE
                if (getString("accountType") != "App_Manager") {//非管家
                    badgeView!!.apply {
                        bindTarget(main_group)
                        badgeNumber = count
                        setGravityOffset(CommonUtil.getScreenWidth(baseContext) / 2f - 50, 0f, false)
                        setBadgeTextSize(10f, true)
                        setOnDragStateChangedListener { dragState, badge, targetView -> }
                    }
                } else {
                    badgeView!!.apply {
                        bindTarget(main_group)
                        badgeNumber = count
                        setGravityOffset(CommonUtil.getScreenWidth(baseContext) / 3f - 150, 0f, false)
                        setBadgeTextSize(10f, true)
                        setOnDragStateChangedListener { dragState, badge, targetView -> }
                    }
                }
            } else {
                if (badgeView != null)
                    badgeView!!.visibility = View.INVISIBLE
            }
        }, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.PRIVATE)
    }

    private fun connect(token: String) {
        /**
         * IMKit SDK调用第二步,建立与服务器的连接
         *
         * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #init(Context)} 之后调用。</p>
         * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
         * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
         *
         * @param token    从服务端获取的用户身份令牌（Token）。
         * @param callback 连接回调。
         * @return RongIM  客户端核心类的实例。
         */
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            /*
             * 连接融云成功，返回当前 token 对应的用户 id
             */
            override fun onSuccess(userid: String) {
                isConnected = true
                OkLogger.i("融云连接成功， 用户ID：" + userid)
                OkLogger.i(RongIMClient.getInstance().currentConnectionStatus.message)
                RongCloudContext.getInstance().connectedListener()
                getUnReadCount()
            }

            /*
             * 连接融云失败 errorCode 错误码，可到官网 查看错误码对应的注释
             */
            override fun onError(errorCode: RongIMClient.ErrorCode) {
                OkLogger.e("融云连接失败，错误码：" + errorCode.message)
            }

            /*
             * Token 错误。可以从下面两点检查
             * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
             * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
             */
            override fun onTokenIncorrect() {
                OkLogger.e("融云token错误！！！")
            }
        })
    }

    /**
     * 区域列表
     */
    fun getData(areaId: String) {
        OkGo.post<BaseResponse<ArrayList<CityNewData>>>(BaseHttp.area_mine_list)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .params("areaId", areaId)
                .execute(object : JsonDialogCallback<BaseResponse<ArrayList<CityNewData>>>(baseContext, false) {
                    override fun onSuccess(response: Response<BaseResponse<ArrayList<CityNewData>>>) {
                        //返回100   在跳转
                        list.clear()
                        list.addItems(response.body().`object`)
                        if (list.size > 0) {
                            startActivity(AreaListActivity::class.java)
                        } else {
//                            toast("")
                        }

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
                        .setConstraint(obj.optString("force") == "1")
            }
            hasNewApp { updateApp, updateAppManager -> showDownloadDialog(updateApp, updateAppManager) }
            noNewApp { /*toast("当前已是最新版本！") */ }
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
                    onStart { HProgressDialogUtils.showHorizontalProgressDialog(this@MainActivity, "下载进度", false) }
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
            if (!updateApp.isConstraint) negativeButton("暂不升级") { dismiss() }
            show()
        }
    }


    private fun checkVipLock() {
        if (getString("vipLock").equals("1")) {
            vipLockDialog = DialogHelper.showRenewalDialog(this, "温馨提示", "您的会员已到期，请续费", "续费", false) {
                startActivity(MemberMineActivity::class.java)
            }
        }
    }

}
