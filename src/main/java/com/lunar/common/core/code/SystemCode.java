package com.lunar.common.core.code;

import java.io.Serializable;

/**
 * 基础模块返回码 20000起
 *
 * @author szx
 */
public enum SystemCode implements BaseCode, Serializable {

    DEFAULT_PASSWORD(20001, "当前为默认密码，请修改"),
    MISSING_USERNAME(20002, "请输入用户名"),
    MISSING_PASSWORD(20003, "请输入密码"),
    USER_NOT_FOUND(20004, "用户名或密码错误"),
    USER_STATUS_ERROR(20005, "账号状态异常，请联系管理员"),
    USER_NAME_EXIST(20006, "用户名已存在"),
    OLD_PASSWORD_ERROR(20007, "旧密码不正确"),
    SAME_PASSWORD(20008, "新旧密码不能相同"),
    NEW_PASSWORD_ERROR(20009, "密码格式不正确"),
    ROLE_NAME_EXIST(20010, "角色名称已存在"),
    PRESET_ROLE_CANNOT_EDIT(20011, "预置角色不允许编辑"),
    PRESET_ROLE_CANNOT_DELETE(20012, "预置角色不允许删除"),
    ROLE_BIND_USER(20013, "请先取消用户的该角色后再进行删除角色操作"),
    ORG_NAME_EXIST(20021, "组织名称已存在"),
    ORG_ROOT_CANNOT_ADD(20022, "不允许创建根组织"),
    ORG_ROOT_CANNOT_DELETE(20023, "不允许删除根组织"),
    ORG_PARENT_NOT_FOUND(20024, "上级组织不存在"),
    ORG_PARENT_ERROR(20025, "上级组织错误"),
    ORG_HAS_CHILD(20026, "请先删除所有下级组织再进行删除组织操作"),
    ORG_HAS_USER(20027, "请先删除组织下所有用户，再进行删除组织操作"),
    ORG_ERROR(20028, "组织机构错误"),
    ORG_SINGLE_CANNOT_ADD(20029, "单体组织不能创建子组织"),
    ORG_CODE_EXIST(20030, "组织编码已存在"),
    ORG_ADDR_EXIST(20031, "组织简称已存在"),
    SAME_PASSWORD_USERNAME(20032, "密码不能和用户名一样"),

    COMPANY_NOT_FOUND(20032, "公司不存在"),
    COMPANY_CODE_EXIST(20033, "登录代码已存在"),
    COMPANY_NAME_EXIST(20034, "组织名称已存在"),
    COMPANY_CODE_NOT_FOUND(20035, "登录码、用户名或密码错误"),
    LOGIN_ERROR_TOO_MANY(20036, "连续错误登录次数过多，请稍后再试"),
    USCC_EXIST(20037, "社会信用代码已存在"),

    DICT_DATA_EXIST(20051, "字典分类标识已存在"),
    DICT_DATA_NAME_EXIST(20052, "字典分类名称已存在"),
    DICT_ENUM_EXIST(20061, "字典枚举值标识已存在"),
    DICT_ENUM_NAME_EXIST(20062, "字典枚举值名称已存在"),
    DICT_TYPE_EXIST(20071, "字典标识已存在"),
    DICT_TYPE_NAME_EXIST(20072, "字典标识名称已存在"),

    UNIT_CONVERSION_EXIST(20080, "该条单位换算数据已存在"),

    FACTOR_CO2E(20100, "请填写二氧化碳当量"),

    AUDIT_EXIST(20200, "组织审批已存在"),
    AUDIT_NOT_FOUND(20201, "组织审批不存在"),
    AUDIT_MAX(20202, "超过审批层级配置"),


    ;

    private Integer code;
    private String message;

    SystemCode(Integer code, String message) {
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