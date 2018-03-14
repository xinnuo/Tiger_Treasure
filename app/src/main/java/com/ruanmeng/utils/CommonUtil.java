package com.ruanmeng.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

public static boolean checkNetState(Context context) {
    boolean netstate = false;
    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity != null) {
        @SuppressWarnings("deprecation")
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    netstate = true;
                    break;
                }
            }
        }
    }
    return netstate;
}

public static int getScreenWidth(Context context) {
    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();
    DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);
    return outMetrics.widthPixels;
    //return display.getWidth();
}

public static int getScreenHeight(Context context) {
    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();
    DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);
    return outMetrics.heightPixels;
    //return display.getHeight();
}

//dip转像素值
public static int dip2px(Context context, double d) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (d * scale + 0.5f);
}

//像素值转dip
public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
}

/**
 * 是否是手机号
 */
public static boolean isMobileNumber(String mobile) {
    if (mobile.length() != 11) return false;
    // Pattern p = Pattern.compile("^((1[3|5|8][0-9])|(14[5|7])|(17[0|1|3|6|7|8]))\\d{8}$");
    Pattern p = Pattern.compile("^((1[3|5|8][0-9])|(14[5|7])|(17[0-9]))\\d{8}$");
    Matcher m = p.matcher(mobile);
    return m.matches();
}

/**
 * 是否是固话
 */
public static boolean isTel(String tel) {
    // Pattern p = Pattern.compile("^((\\d{7,8})|(0\\d{2,3}-\\d{7,8})|(1[34578]\\d{9}))$"); //固话和匹配手机
    Pattern p = Pattern.compile("^((\\d{7,8})|(0\\d{2,3}-\\d{7,8})|(400-\\d{3}-\\d{4}))$"); ////固话和400固话
        /*String reg = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        Pattern p = Pattern.compile(reg);*/
    Matcher m = p.matcher(tel);
    return m.matches();
}

/**
 * 是否是邮箱
 */
public static boolean isEmail(String strEmail) {
    // String strPattern =
    // "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";

    Pattern p = Pattern.compile(strPattern);
    Matcher m = p.matcher(strEmail);
    return m.matches();
}

/**
 * 是否是网址
 */
public static boolean isWeb(String strWeb) {
    String strPattern = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";

    Pattern p = Pattern.compile(strPattern);
    Matcher m = p.matcher(strWeb);
    return m.matches();
}

/**
 * 实际替换动作
 *
 * @param str     username
 * @param regular 正则
 */
public static String replaceAction(String str, String regular) {
    return str.replaceAll(regular, "*");
}

/**
 * 姓名替换，保留姓氏
 * 如果身姓名为空 或者 null ,返回null ；否则，返回替换后的字符串；
 *
 * @param name 身份证号
 */
public static String nameReplaceWithStar(String name) {

    if (name == null || name.isEmpty()) {
        return null;
    } else {
        return replaceAction(name, "(?<=[\\u4e00-\\u9fa5]{" + (name.length() > 3 ? "2" : "1") + "})[\\u4e00-\\u9fa5](?=[\\u4e00-\\u9fa5]{0})");
    }
}



/**
 * 手机号号替换，保留前三位和后四位
 * 如果身手机号为空 或者 null ,返回null ；否则，返回替换后的字符串；
 *
 * @param phone 手机号
 */
public static String phoneReplaceWithStar(String phone) {

    if (phone == null || phone.isEmpty()) {
        return null;
    } else {
        return replaceAction(phone, "(?<=\\d{3})\\d(?=\\d{4})");
    }
}

/**
 * 身份证号替换，保留前四位和后四位
 * 如果身份证号为空 或者 null ,返回null ；否则，返回替换后的字符串；
 *
 * @param idCard 身份证号
 */
public static String idCardReplaceWithStar(String idCard) {

    if (idCard == null || idCard.isEmpty()) {
        return null;
    } else {
        return replaceAction(idCard, "(?<=\\d{4})\\d(?=\\d{4})");
    }
}

/**
 * 银行卡替换，保留后四位
 * <p>
 * 如果银行卡号为空 或者 null ,返回null ；否则，返回替换后的字符串；
 *
 * @param bankCard 银行卡号
 */
public static String bankCardReplaceWithStar(String bankCard) {

    if (bankCard == null || bankCard.isEmpty()) {
        return null;
    } else {
        return replaceAction(bankCard, "(?<=\\d{0})\\d(?=\\d{4})");
    }
}

/**
 * 功能：身份证的有效验证
 *
 * @param IDStr 身份证号
 * @return 有效：返回"" 无效：返回String信息
 * @throws ParseException
 */
public static boolean IDCardValidate(String IDStr) throws ParseException {
    String errorInfo; // 记录错误信息
    String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2"};
    String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
    String Ai = "";
    // ================ 号码的长度 15位或18位 ================
    if (IDStr.length() != 15 && IDStr.length() != 18) {
        errorInfo = "身份证号码长度应该为15位或18位";
        Log.e("IDCard", errorInfo);
        return false;
    }
    // ================ 数字 除最后一位都为数字 ================
    if (IDStr.length() == 18) Ai = IDStr.substring(0, 17);
    else if (IDStr.length() == 15)
        Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
    if (!isNumeric(Ai)) {
        errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字";
        Log.e("IDCard", errorInfo);
        return false;
    }
    // ================ 出生年月是否有效 ================
    String strYear = Ai.substring(6, 10);// 年份
    String strMonth = Ai.substring(10, 12);// 月份
    String strDay = Ai.substring(12, 14);// 月份
    if (!isDataFormat(strYear + "-" + strMonth + "-" + strDay)) {
        errorInfo = "身份证生日无效";
        Log.e("IDCard", errorInfo);
        return false;
    }
    GregorianCalendar gc = new GregorianCalendar();
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
    try {
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            errorInfo = "身份证生日不在有效范围";
            Log.e("IDCard", errorInfo);
            return false;
        }
    } catch (NumberFormatException | ParseException e) {
        e.printStackTrace();
    }
    if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
        errorInfo = "身份证月份无效";
        Log.e("IDCard", errorInfo);
        return false;
    }
    if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
        errorInfo = "身份证日期无效";
        Log.e("IDCard", errorInfo);
        return false;
    }
    // ================ 地区码时候有效 ================
    @SuppressWarnings("rawtypes")
    Hashtable h = GetAreaCode();
    if (h.get(Ai.substring(0, 2)) == null) {
        errorInfo = "身份证地区编码错误";
        Log.e("IDCard", errorInfo);
        return false;
    }
    // ================ 判断最后一位的值 ================
    int TotalmulAiWi = 0;
    for (int i = 0; i < 17; i++) {
        TotalmulAiWi = TotalmulAiWi
                + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                * Integer.parseInt(Wi[i]);
    }
    int modValue = TotalmulAiWi % 11;
    String strVerifyCode = ValCodeArr[modValue];
    Ai = Ai + strVerifyCode;

    if (IDStr.length() == 18) {
        if (!Ai.equals(IDStr.toLowerCase())) {
            errorInfo = "身份证无效，不是合法的身份证号码";
            Log.e("IDCard", errorInfo);
            return false;
        }
    }

    return true;
}

/**
 * 功能：设置地区编码
 *
 * @return Hashtable 对象
 */
@SuppressWarnings({"rawtypes", "unchecked"})
private static Hashtable GetAreaCode() {
    Hashtable hashtable = new Hashtable();
    hashtable.put("11", "北京");
    hashtable.put("12", "天津");
    hashtable.put("13", "河北");
    hashtable.put("14", "山西");
    hashtable.put("15", "内蒙古");
    hashtable.put("21", "辽宁");
    hashtable.put("22", "吉林");
    hashtable.put("23", "黑龙江");
    hashtable.put("31", "上海");
    hashtable.put("32", "江苏");
    hashtable.put("33", "浙江");
    hashtable.put("34", "安徽");
    hashtable.put("35", "福建");
    hashtable.put("36", "江西");
    hashtable.put("37", "山东");
    hashtable.put("41", "河南");
    hashtable.put("42", "湖北");
    hashtable.put("43", "湖南");
    hashtable.put("44", "广东");
    hashtable.put("45", "广西");
    hashtable.put("46", "海南");
    hashtable.put("50", "重庆");
    hashtable.put("51", "四川");
    hashtable.put("52", "贵州");
    hashtable.put("53", "云南");
    hashtable.put("54", "西藏");
    hashtable.put("61", "陕西");
    hashtable.put("62", "甘肃");
    hashtable.put("63", "青海");
    hashtable.put("64", "宁夏");
    hashtable.put("65", "新疆");
    hashtable.put("71", "台湾");
    hashtable.put("81", "香港");
    hashtable.put("82", "澳门");
    hashtable.put("91", "国外");
    return hashtable;
}

/**
 * 功能：判断字符串是否为数字
 *
 * @param str 字符串
 * @return 布尔值
 */
public static boolean isNumeric(String str) {
    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher isNum = pattern.matcher(str);
    return isNum.matches();
}

/**
 * 验证日期字符串
 *
 * @param str 日期
 */
public static boolean isDataFormat(String str) {
    boolean flag = false;
    //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
    String regxStr = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
    Pattern pattern1 = Pattern.compile(regxStr);
    Matcher isNo = pattern1.matcher(str);
    if (isNo.matches()) flag = true;
    return flag;
}

}
