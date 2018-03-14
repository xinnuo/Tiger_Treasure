package com.ruanmeng.utils;

public class ObjectUtils {
    /**
     * 检验字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() <= 0 || str == "" || str.equals("null") || str.equals("undefined")) {
            return true;
        }
        return false;
    }

    /**
     * 检验字符串是否为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {
        String result = "";
        if (ObjectUtils.isNotEmpty(unicode)) {
            StringBuffer string = new StringBuffer();
            String[] hex = unicode.split("\\\\u");
            for (int i = 1; i < hex.length; i++) {
                // 转换出每一个代码点
                int data = Integer.parseInt(hex[i], 16);
                // 追加成string
                string.append((char) data);
            }
            result = string.toString();
        }
        return result;
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }


}
