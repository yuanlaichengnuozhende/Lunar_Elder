package com.lunar.common.core.consts;

/**
 * 通用常量信息
 */
public class Consts {

    /**
     * 应用名称 & redis key前缀
     */
    public static final String APP_NAME = "dct:single";

    /**
     * 全局小数位数
     */
    public final static int POINT = 3;

    /**
     * 计算过程小数位数
     */
    public final static int CALC_POINT = 20;

//    /**
//     * 因子小数位数
//     */
//    public final static int FACTOR_POINT = 4;

    /**
     * 审配最大层级
     */
    public final static int AUDIT_LEVEL_MAX = 10;

    /**
     * 默认登录代码，与db保持一致
     */
    public static final String DEFAULT_COMPANY_CODE = "code0001";

    /**
     * 中文字典值
     */
    public final static String ZH_KEY = "zh";

    /**
     * 英文字典值
     */
    public final static String EN_KEY = "en";

    /**
     * 翻译内容（数据翻译使用） i18n:tableName:fieldName:id
     */
    public final static String I18N_FMT = "i18n:%s:%s:%s";

    /**
     * orgCode分隔符
     */
    public final static String ORG_SEPARATOR = "/";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 报告未填写数据
     */
    public static final String UN_WRITE = "【未填写】";

}
