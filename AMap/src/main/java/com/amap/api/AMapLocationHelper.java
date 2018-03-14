package com.amap.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-09-22 10:00
 */
public class AMapLocationHelper {

    private static AMapLocationHelper instance;

    private ArrayList<Integer> CODES = new ArrayList<>();
    private Activity context;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocationCallback callback;

    private AMapLocationHelper(Activity context) {
        this.context = context;
        initLocation();
    }

    public static AMapLocationHelper getInstance(Activity ctx) {
        if (instance == null)
            instance = new AMapLocationHelper(ctx);
        return instance;
    }

    public void addCode(int code) {
        if (!CODES.contains(code)) CODES.add(code);
    }

    public void removeCode(int code) {
        if (CODES.contains(code)) CODES.remove(code);
    }

    public void clearCodes() {
        CODES.clear();
    }

    public AMapLocationHelper setDuration(int millisecond) {
        locationOption.setOnceLocation(false);
        locationOption.setOnceLocationLatest(false);
        locationOption.setInterval(millisecond);
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        return this;
    }

    /**
     * 开始定位
     */
    public void startLocation(int code, LocationCallback callback){
        this.callback = callback;
        addCode(code);
        //启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (locationClient != null) locationClient.stopLocation();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /*
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        //设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);                //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);                                              //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);                                           //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setNeedAddress(true);                                            //可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);                                           //可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);                                     //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP); //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);                                          //可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true);                                               //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true);                                    //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                StringBuffer sb = new StringBuffer();
                // errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度: " + location.getLongitude() + "\n");
                    sb.append("纬    度: " + location.getLatitude() + "\n");
                    sb.append("精    度: " + location.getAccuracy() + "米" + "\n");
                    sb.append("提 供 者: " + location.getProvider() + "\n");

                    sb.append("速    度: " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度: " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数: " + location.getSatellites() + "\n");
                    sb.append("国    家: " + location.getCountry() + "\n");
                    sb.append("省      : " + location.getProvince() + "\n");
                    sb.append("市      : " + location.getCity() + "\n");
                    sb.append("城市编码: " + location.getCityCode() + "\n");
                    sb.append("区      : " + location.getDistrict() + "\n");
                    sb.append("区 域 码: " + location.getAdCode() + "\n");
                    sb.append("地    址: " + location.getAddress() + "\n");
                    sb.append("兴 趣 点: " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

                    callback.doWork(location, true, CODES.toArray());
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错 误 码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");

                    callback.doWork(location, false, CODES.toArray());
                }
                sb.append("***定位质量报告***").append("\n");
                sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
                sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                Log.i("AMapLocation", sb.toString());
            } else {
                callback.doWork(null, false, CODES.toArray());
            }
        }
    };

    /**
     * 时间格式化
     */
    public static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) strPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
        sdf.applyPattern(strPattern);
        return sdf.format(l);
    }

    /**
     * 获取GPS状态的字符串
     */
    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    public interface LocationCallback {
        void doWork(AMapLocation location, boolean isSuccessed, Object... codes);
    }

    /**
     * 启动高德App进行导航
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/27,13:58
     * <h3>UpdateTime</h3> 2016/6/27,13:58
     * <h3>CreateAuthor</h3>
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @param sourceApplication 必填 第三方调用应用名称。如 amap
     * @param poiname           非必填 POI 名称
     * @param lat               必填 纬度
     * @param lon               必填 经度
     * @param dev               必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
     * @param style             必填 导航方式(0 速度快; 1 费用少; 2 路程短; 3 不走高速；4 躲避拥堵；5 不走高速且避免收费；6 不走高速且躲避拥堵；7 躲避收费和拥堵；8 不走高速躲避收费和拥堵))
     */
    public static void goToNaviActivity(
            Context context,
            String sourceApplication,
            String poiname,
            String lat,
            String lon,
            String dev,
            String style) {
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                .append(sourceApplication);
        if (!TextUtils.isEmpty(poiname)) {
            stringBuffer.append("&poiname=").append(poiname);
        }
        stringBuffer.append("&lat=").append(lat)
                .append("&lon=").append(lon)
                .append("&dev=").append(dev)
                .append("&style=").append(style);

        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);
    }

    /**
     * 根据包名判断是否有安装该App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
}
