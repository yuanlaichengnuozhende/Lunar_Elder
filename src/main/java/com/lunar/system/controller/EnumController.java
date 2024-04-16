package com.lunar.system.controller;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.enums.ApprovalStatus;
import com.lunar.common.core.enums.AuditStatus;
import com.lunar.common.core.enums.AuditType;
import com.lunar.common.core.enums.BaseEnum;
import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.ConfigType;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.FileStatus;
import com.lunar.common.core.enums.GHGCategory;
import com.lunar.common.core.enums.GasType;
import com.lunar.common.core.enums.ISOCategory;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.core.enums.StandardType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.response.EnumResp;
import com.lunar.common.core.utils.EnumUtil;
import com.lunar.system.enums.RoleType;
import com.lunar.system.enums.UserStatus;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.enums.*;
import com.lunar.common.core.response.EnumResp;
import com.lunar.common.core.utils.EnumUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 枚举
 */
@Slf4j
@RestController
@Validated
@Api(tags = "枚举")
@RequestMapping("/enums")
public class EnumController {

    private static final Map<String, Class<? extends BaseEnum>> ENUMS_MAP = new CaseInsensitiveMap<>();

    static {
        ENUMS_MAP.put("EnableStatus", EnableStatus.class);
        ENUMS_MAP.put("AuditType", AuditType.class);
        ENUMS_MAP.put("BizModule", BizModule.class);
        ENUMS_MAP.put("ConfigType", ConfigType.class);
        ENUMS_MAP.put("FileStatus", FileStatus.class);
        ENUMS_MAP.put("GasType", GasType.class);
        ENUMS_MAP.put("ModuleType", ModuleType.class);
        ENUMS_MAP.put("OperType", OperType.class);
        ENUMS_MAP.put("OptionType", OptionType.class);
        ENUMS_MAP.put("OrgType", OrgType.class);
        ENUMS_MAP.put("RoleType", RoleType.class);
        ENUMS_MAP.put("UserStatus", UserStatus.class);
        ENUMS_MAP.put("AuditStatus", AuditStatus.class);
        ENUMS_MAP.put("ApprovalStatus", ApprovalStatus.class);
        ENUMS_MAP.put("GHGCategory", GHGCategory.class);
        ENUMS_MAP.put("ISOCategory", ISOCategory.class);
        ENUMS_MAP.put("StandardType", StandardType.class);
    }

    private static final String NOTES =
        "EnableStatus - 启用状态; \t\n"
            + "AuditType - 审批内容; \t\n"
            + "BizModule - 功能模块; \t\n"
            + "ConfigType - 配置类型; \t\n"
            + "FileStatus - 文件状态; \t\n"
            + "GasType - 温室气体类型; \t\n"
            + "ModuleType - 模块类型; \t\n"
            + "OperType - 操作类型; \t\n"
            + "OptionType - 多级结构复选框; \t\n"
            + "OrgType - 组织类型; \t\n"
            + "RoleType - 角色类型; \t\n"
            + "UserStatus - 用户状态; \t\n"
            + "AuditStatus - 审核状态; \t\n"
            + "ApprovalStatus - 审核状态（只包含待审核、审核通过、审核不通过）; \t\n"
            + "GHGCategory - GHG分类; \t\n"
            + "ISOCategory - ISO分类; \t\n"
            + "StandardType - 标准类别; \t\n"

        ;

    @ApiOperation(value = "查询枚举值（忽略大小写）", notes = NOTES)
    @ApiImplicitParam(name = "enumName", value = "枚举名", required = true, dataType = "string", paramType = "query")
    @GetMapping("/{enumName}")
    public ApiResult<List<EnumResp>> enums(@PathVariable("enumName") String enumName) {
        Class<? extends BaseEnum> enumClass = ENUMS_MAP.get(enumName);
        if (enumClass == null) {
            throw new ServiceException("枚举类不存在");
        }

        List<EnumResp> enumsResp = EnumUtil.getEnumsResp(enumClass);

//        // 子枚举
//        if (enumClass == FactorUnitMClass.class) {
//            enumsResp.forEach(x -> {
//                List<FactorUnitM> list = FactorUnitM.getByClass(x.getCode());
//                List<EnumResp> subList = EnumUtil.getEnumResp(list);
//                x.setSubList(subList);
//            });
//        }

        return ApiResult.ok(enumsResp);
    }

}
