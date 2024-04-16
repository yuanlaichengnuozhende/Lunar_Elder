package com.lunar.common.core.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * 字符串工具类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || CollectionUtils.isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置: ? 表示单个字符; * 表示一层路径内的任意字符串，不可跨层级; ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * 判断是否为空，并且不是空白字符
     *
     * @param str 要判断的value
     * @return 结果
     */
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String lowerFirst(String str) {
        char[] cs = str.toCharArray();

        // A-Z
        if (cs[0] >= 65 && cs[0] <= 90) {
            cs[0] += 32;
            return String.valueOf(cs);
        }

        return str;
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String upperFirst(String str) {
        char[] cs = str.toCharArray();

        // a-z
        if (cs[0] >= 97 && cs[0] <= 122) {
            cs[0] -= 32;
            return String.valueOf(cs);
        }

        return str;
    }

    public static void main(String[] args) {
        System.out.println(lowerFirst("gsdfFsdf"));

        System.out.println(upperFirst("GsdfFsdf"));
    }

}