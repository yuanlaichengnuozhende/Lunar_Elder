package com.lunar.system.controller;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.DateUtils;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.OperLog;
import com.lunar.system.query.OperLogQuery;
import com.lunar.system.request.DownloadLogReq;
import com.lunar.system.service.OperLogService;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.DateUtils;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 操作日志
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "操作日志")
@RequestMapping("/operlog")
public class OperLogController {

    private final OperLogService operLogService;

    @InnerAuth
    @ApiIgnore
    @PostMapping(value = "/inner/save")
    public ApiResult saveLog(@RequestBody @Valid OperLog operLog) {
        operLogService.insertSelective(operLog);
        log.info("保存系统日志完成");
        return ApiResult.ok();
    }

    @ApiOperation(value = "操作日志列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeUsername", value = "操作人姓名或账号", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "moduleType", value = "模块类型", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "startDate", value = "开始时间", required = false, dataType = "data", paramType = "query"),
        @ApiImplicitParam(name = "endDate", value = "结束时间", required = false, dataType = "data", paramType = "query")
    })
    @RequiresPermissions("/sys/operlog")
    @GetMapping("/page")
    public ApiResult<IPage<OperLog>> page(@Validated PageParam pageReq,
                                          String likeUsername, ModuleType moduleType,
                                          String startDate, String endDate) {
        OperLogQuery query = OperLogQuery.builder()
            .companyId(SecurityUtils.getCompanyId())
            .likeUsername(likeUsername)
            .moduleType(moduleType)
//            .startDate(DateUtils.getStartDate(startDate))
//            .endDate(DateUtils.getEndDate(endDate))
            .startDate(DateUtils.parseStringDate(startDate))
            .endDate(DateUtils.parseStringDate(endDate))
            .build();
        IPage<OperLog> page = operLogService.findPage(query, pageReq, Order.createTimeDesc());

        return ApiResult.ok(page);
    }

    @ApiOperation("记录下载日志")
    @PostMapping("/save/downloadLog")
    public ApiResult downloadLog(@RequestBody @Valid DownloadLogReq req) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        String ipAddr = IpUtils.getRemoteIpAddr(ServletUtils.getRequest());

        OperLog operLog = OperLog.builder()
            .companyId(SecurityUtils.getCompanyId())
            .operType(OperType.DOWNLOAD)
            .moduleType(req.getModuleType())
            .content(req.getContent() + "[" + req.getUrl() + "]")
            .username(loginUser.getUsername())
            .realName(loginUser.getRealName())
            .ipAddr(ipAddr)
            .createBy(SecurityUtils.getUserId())
            .build();
        operLogService.insertSelective(operLog);
        log.info("保存下载日志完成");
        return ApiResult.ok();
    }

}
