package com.lunar.common.core.utils;

import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.consts.Consts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 数字工具
 *
 * @author szx
 * @date 2022/01/26 17:36
 */
public class NumUtil {

    public static boolean notNullOrZero(Integer val) {
        return !isNullOrZero(val);
    }

    public static boolean notNullOrZero(Long val) {
        return !isNullOrZero(val);
    }

    public static boolean notNullOrZero(Double val) {
        return !isNullOrZero(val);
    }

    public static boolean isNullOrZero(Integer val) {
        return val == null || val == 0;
    }

    public static boolean isNullOrZero(Long val) {
        return val == null || val == 0L;
    }

    public static boolean isNullOrZero(Double val) {
        return val == null || val == 0d;
    }

    public static boolean anyNullOrZero(Double... val) {
        for (Double aDouble : val) {
            if (isNullOrZero(aDouble)) {
                return true;
            }
        }
        return false;
    }

    public static boolean allNullOrZero(Double... val) {
        for (Double aDouble : val) {
            if (notNullOrZero(aDouble)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 值不为空且在(0, max)区间
     *
     * @param val
     * @return
     */
    public static boolean validOpenDouble(Double max, Double val) {
        if (val == null) {
            return false;
        }

        return val > 0 && val < max;
    }

    /**
     * 值不为空且在[0, max]区间
     *
     * @param val
     * @return
     */
    public static boolean validCloseDouble(Double max, Double val) {
        if (val == null) {
            return false;
        }
        return val >= 0 && val <= max;
    }

    /**
     * 值无效
     *
     * @param val
     * @return
     */
    public static boolean invalidOpenDouble(Double max, Double val) {
        return !validOpenDouble(max, val);
    }

    /**
     * 值无效
     *
     * @param val
     * @return
     */
    public static boolean invalidCloseDouble(Double max, Double val) {
        return !validCloseDouble(max, val);
    }

    /**
     * 任一数字无效
     *
     * @param val
     * @return
     */
    public static boolean anyInvalidOpenDouble(Double max, Double... val) {
        for (Double aDouble : val) {
            if (invalidOpenDouble(max, aDouble)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 任一数字无效
     *
     * @param val
     * @return
     */
    public static boolean anyInvalidCloseDouble(Double max, Double... val) {
        for (Double aDouble : val) {
            if (invalidCloseDouble(max, aDouble)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 二进制v的第n位置1
     *
     * @param v
     * @param n
     * @return
     */
    public static int setBit(int v, int n) {
        int t = (1 << (n - 1)) | v;
        return t;
    }

    /**
     * 二进制v的第n位置0
     *
     * @param v
     * @param n
     * @return
     */
    public static int unsetBit(int v, int n) {
        int t = ~(1 << (n - 1)) & v;
        return t;
    }

    /**
     * 格式化数字。末尾补0
     *
     * @param point 小数位数
     * @param num
     * @return
     */
    public static String format(int point, Number num) {
        if (num == null) {
            return null;
        }

        String pattern = "0";
        if (point > 0) {
            pattern = StringUtils.rightPad("0.", point + 2, '0');
        }

        DecimalFormat df = new DecimalFormat(pattern);
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(num);
    }

    /**
     * 取整数
     *
     * @param num
     * @return
     */
    public static String toIntStr(Number num) {
        return format(0, num);
    }

    /**
     * 格式化数字，去除末尾的0
     *
     * @param point 小数位数
     * @param num
     * @return
     */
    public static String formatClearZero(int point, Number num) {
        if (num == null) {
            return null;
        }

        String pattern = "0";
        if (point > 0) {
            pattern = StringUtils.rightPad("0.", point + 2, '#');
        }

        DecimalFormat df = new DecimalFormat(pattern);
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(num);
    }

    /**
     * 格式化数字，去除末尾的0
     *
     * @param num
     * @return
     */
    public static String formatClearZero(Number num) {
        return formatClearZero(Consts.POINT, num);
    }

    /**
     * 格式化double，返回double
     *
     * @param point
     * @param num
     * @return
     */
    public static Double formatDouble(int point, Number num) {
        if (num == null) {
            return null;
        }

        BigDecimal b = new BigDecimal(num.toString());
        return b.setScale(point, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 格式化，返回double
     *
     * @param num
     * @return
     */
    public static Double formatDouble(Double num) {
        return formatDouble(Consts.POINT, num);
    }

    /**
     * 格式化double，返回double
     *
     * @param num
     * @return
     */
    public static Double formatDouble(Number num) {
        return formatDouble(Consts.POINT, num);
    }

    /**
     * 全局默认小数位数。格式化数字。不展示无效的0
     *
     * @param num
     * @return
     */
    public static String format(Number num) {
        return formatClearZero(Consts.POINT, num);
    }

    /**
     * 数字转string，禁用科学计数法
     *
     * @param number
     * @return
     */
    public static String plainString(Number number) {
        if (number == null) {
            return "";
        }

        return new BigDecimal(number.toString()).stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {
//        System.out.println(anyInvalidOpenDouble(99999999d,234.3d, 2343245.3d, 255d));
//        DecimalFormat df = new DecimalFormat("0.00");
//        df.setRoundingMode(RoundingMode.HALF_UP);
//        System.out.println(df.format(345435450.445));
//
//        System.out.println(format(2, 3454563223.324));
//
//        BigDecimal bigDecimal = new BigDecimal(String.valueOf(345435450.445)).setScale(2, RoundingMode.HALF_UP);
//        System.out.println(bigDecimal.toPlainString());

        System.out.println(formatClearZero(2, 234.0d));
        System.out.println(format(2, 234.0d));

        System.out.println(toIntStr(8943583945345.5d));

        System.out.println(formatDouble(3, 123.3454d));
        System.out.println(formatDouble(3, 123.3455d));
        System.out.println(formatDouble(3, 123.34006d));
        System.out.println(formatDouble(2, 123.3454d));
        System.out.println(formatDouble(2, null));

        System.out.println(anyNullOrZero(1d, 0d, 2d));

        System.out.println(allNullOrZero(null, 1d, 0d));

    }

}
