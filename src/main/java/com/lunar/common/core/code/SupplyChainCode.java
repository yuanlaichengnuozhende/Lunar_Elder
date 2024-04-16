package com.lunar.common.core.code;

import java.io.Serializable;

/**
 * 供应链模块返回码 60000起
 *
 * @author szx
 */
public enum SupplyChainCode implements BaseCode, Serializable {

    ORG_PERMS_ERROR(40001, "组织权限错误"),
    SUPPLIER_NAME_EXIST(40002, "供应商名称已存在"),
    SUPPLIER_CODE_EXIST(40003, "供应商编码已存在"),
    SUPPLIER_USCC_EXIST(40004, "该社会信用代码的供应商已存在"),
    PRODUCT_EXIST(40010, "已创建该采购产品"),
//    VERSION_REPEAT(40002, "版本号重复"),
//    EMISSION_STANDARD_EXIST(40003, "该组织排放基准已存在"),
//    SOURCE_CODE_REPEAT(40004, "排放源ID已存在"),
//    YEAR_CHECK_FAILED(40005, "年份数据校验失败"),
//    DEVICE_PERMS_ERROR(40006, "设备权限错误"),
//    DEVICE_CODE_REPEAT(40007, "设备编码重复"),
//    COMPUTATION_EXIST(40008, "年度碳核算已存在"),
//    MODEL_EXIST(40009, "该组织核算模型已存在"),
//    MODEL_NOT_CONFIG(40010, "核算模型未配置"),
//    CALC_ERROR(40011, "计算核算数据错误"),
//    DATA_MISSING(40012, "碳排放数据无值，不允许提交"),
//    AUDIT_COMMENT_MISSING(40013, "请填写审批意见"),
//    REPORT_NAME_REPEAT(40014, "报告名称重复"),
//    CONTROL_PLAN_HAS_REPORT(40015, "数据质量管理已与报告关联，不允许删除"),
//    MODEL_NAME_REPEAT(40016, "核算模型名称已存在"),
//    CALC_DATE_ERROR(40018, "计算日期错误"),
//    SOURCE_IS_BIND(40019, "排放源已关联"),
//    SOURCE_ERROR(40020, "排放源错误"),
//    COMPUTATION_YEAR_MISS(40021, "请先创建该年度的核算"),
//    AUDIT_CONFIG_MISS(40022, "请先设置企业碳排放数据的审批流"),
//    UNIT_CONVERT_ERROR(40023, "单位换算失败"),
//    SERVICE_NAME_REPEAT(40024, "产品或服务名称重复"),
//    REDUCTION_SCENE_NAME_REPEAT(40025, "减排场景名称重复"),


    ;

    private Integer code;
    private String message;

    SupplyChainCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}