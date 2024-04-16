package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.FileLog;
import com.lunar.system.query.FileLogQuery;
import com.lunar.system.service.FileLogService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.BizModule;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 下载管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "下载管理")
@RequestMapping("/filelog")
public class FileLogController {

    private final RedissonClient redissonClient;
    private final FileLogService fileLogService;

    /**
     * 保存文件日志
     *
     * @param fileLog
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @PostMapping(value = "/inner/save")
    public ApiResult<FileLog> saveLog(@RequestBody @Valid FileLog fileLog) {
        log.info("保存文件日志. fileLog={}", fileLog);

        Long userId = SecurityUtils.getUserId();

        fileLog.setId(null);
        fileLog.setDeleted(false);
        fileLog.setCreateBy(userId);
        fileLog.setUpdateBy(userId);
        fileLogService.insertSelective(fileLog);
        log.info("保存文件日志完成");

        return ApiResult.ok(fileLog);
    }

    /**
     * 更新文件日志
     *
     * @param fileLog
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @PostMapping(value = "/inner/update")
    public ApiResult updateLog(@RequestBody @Valid FileLog fileLog) {
        log.info("更新文件日志. fileLog={}", fileLog);
        if (fileLog.getFileStatus() == null) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        fileLog.setCompanyId(null);
        fileLog.setBizModule(null);
        fileLog.setFileName(null);
        fileLog.setUpdateBy(SecurityUtils.getUserId());
        fileLogService.updateSelective(fileLog);
        log.info("更新文件日志完成");

        return ApiResult.ok();
    }

    @ApiOperation(value = "文件下载列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "orgId", value = "组织id", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "bizModule", value = "功能模块。1 系统管理；2 排放因子库；3 产品碳足迹；4 企业碳核算", required = false, dataType = "int", paramType = "query"),
    })
    @ControllerMeta({ControllerMetaType.CREATE_BY, ControllerMetaType.UPDATE_BY, ControllerMetaType.ORG_NAME})
    @RequiresPermissions("/sys/download")
    @GetMapping("/page")
    public ApiResult<IPage<FileLog>> page(@Validated PageParam pageReq,
                                          Long orgId, BizModule bizModule) {
        if (SecurityUtils.missOrg(orgId)) {
            return ApiResult.error(ApiCode.ORG_PERMS_ERROR);
        }

        FileLogQuery query = FileLogQuery.builder()
            .companyId(SecurityUtils.getCompanyId())
            .orgId(orgId)
            .bizModule(bizModule)
            .build();
        IPage<FileLog> page = fileLogService.findPage(query, pageReq, Order.createTimeDesc());

        return ApiResult.ok(page);
    }

    @ApiOperation("记录下载日志")
    @Log(moduleType = ModuleType.FILE_LOG, operType = OperType.DOWNLOAD)
    @PostMapping("/downloadLog/save")
    public ApiResult saveDownloadLog(@RequestBody @Valid IdReq req) {
        String label = "sys:filelog:download:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            FileLog entity = fileLogService.selectByPrimaryKey(req.getId());
            if (entity == null) {
                throw new ServiceException(ApiCode.REQUEST_PARAM_ERROR);
            }
            if (SecurityUtils.missCompany(entity.getCompanyId())) {
                throw new ServiceException(ApiCode.COMPANY_PERMS_ERROR);
            }
            if (SecurityUtils.missOrg(entity.getOrgId())) {
                throw new ServiceException(ApiCode.ORG_PERMS_ERROR);
            }

            String log = String.format("下载文件，文件名称：“%s”", entity.getFileName());
            return ApiResult.log(entity.getId(), log);
        });
        return apiResult;
    }

}
