package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ImportType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.dto.ImportLogDto;
import com.lunar.system.entity.ImportLog;
import com.lunar.system.query.ImportLogQuery;
import com.lunar.system.service.ImportLogService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ImportType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "导入记录")
@RequestMapping("/importLog")
public class ImportLogController {

    private final RedissonClient redissonClient;
    private final ImportLogService importLogService;

    /**
     * 保存导入记录
     *
     * @param dto
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @PostMapping(value = "/inner/save")
    public ApiResult<ImportLog> saveLog(@RequestBody @Valid ImportLogDto dto) {
        log.info("保存导入记录. dto={}", dto);

        Long userId = SecurityUtils.getUserId();

        ImportLog importLog = CopyUtil.copyObject(dto, ImportLog.class);

        importLog.setId(null);
        importLog.setCreateBy(userId);
        importLog.setUpdateBy(userId);
        importLogService.insertSelective(importLog);
        log.info("保存导入记录完成");

        return ApiResult.ok(importLog);
    }

    /**
     * 更新导入记录
     *
     * @param dto
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @PostMapping(value = "/inner/update")
    public ApiResult updateLog(@RequestBody @Valid ImportLogDto dto) {
        log.info("更新导入记录. dto={}", dto);
        if (dto.getId() == null || dto.getImportStatus() == null) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        ImportLog importLog = importLogService.selectByPrimaryKey(dto.getId());
        BeanUtils.copyProperties(dto, importLog);
        importLog.setCompanyId(null);
        importLog.setFileName(null);
        importLog.setUpdateBy(SecurityUtils.getUserId());
        importLogService.updateSelective(importLog);
        log.info("更新导入记录完成");

        return ApiResult.ok();
    }

    @ApiOperation("导入记录列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "importType", value = "导入内容。1 供应商；2 采购产品", required = false, dataType = "int", paramType = "query"),
    })
    @ControllerMeta({ControllerMetaType.CREATE_BY})
//    @RequiresPermissions({"/perms"})
    @GetMapping("/page")
    public ApiResult<IPage<ImportLog>> page(@Validated PageParam pageParam,
                                            ImportType importType) {
        ImportLogQuery query = ImportLogQuery.builder()
            .companyId(SecurityUtils.getCompanyId())
            .orgIdList(SecurityUtils.getOrgIdList())
            .importType(importType)
            .build();

        IPage<ImportLog> page = importLogService.findPage(query, pageParam, Order.createTimeDesc());

        return ApiResult.ok(page);
    }

}