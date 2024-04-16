package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Info;
import com.lunar.system.entity.InfoContent;
import com.lunar.system.enums.InfoStatus;
import com.lunar.system.query.InfoContentQuery;
import com.lunar.system.request.InfoReq;
import com.lunar.system.request.InfoStatusReq;
import com.lunar.system.service.InfoContentService;
import com.lunar.system.service.InfoService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "资讯")
@RequestMapping("/info")
public class InfoController {

    private final RedissonClient redissonClient;
    private final InfoService infoService;
    private final InfoContentService infoContentService;

    @ApiOperation("用户端资讯列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeTitle", value = "标题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "infoType", value = "资讯类型", required = false, dataType = "string", paramType = "query"),
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
//    @RequiresPermissions({"/perms"})
    @GetMapping("/user/page")
    public ApiResult<IPage<Info>> userPage(@Validated PageParam pageParam,
                                           String likeTitle, String infoType) {
        IPage<Info> page =
            infoService.page(pageParam, likeTitle, infoType, InfoStatus.PUBLISHED, "publish_time desc");
        return ApiResult.ok(page);
    }

    @ApiOperation("资讯列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeTitle", value = "标题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "infoType", value = "资讯类型", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "infoStatus", value = "资讯状态。0 待发布；1 已发布；2 已下架", required = false, dataType = "int", paramType = "query"),
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
//    @RequiresPermissions({"/perms"})
    @GetMapping("/page")
    public ApiResult<IPage<Info>> page(@Validated PageParam pageParam,
                                       String likeTitle, String infoType, InfoStatus infoStatus) {
        IPage<Info> page = infoService.page(pageParam, likeTitle, infoType, infoStatus, Order.createTimeDesc());
        return ApiResult.ok(page);
    }

    @ApiOperation("资讯详情")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "int", paramType = "query")
//    @RequiresPermissions({"/perms"})
    @GetMapping("/{id}")
    public ApiResult<Info> detail(@PathVariable Long id) {
        Info entity = infoService.selectByPrimaryKey(id);
        InfoContent infoContent = infoContentService.selectOne(
            InfoContentQuery.builder().infoId(entity.getId()).build());
        entity.setAttachment(infoContent.getAttachment());
        entity.setContent(infoContent.getContent());

        return ApiResult.ok(entity);
    }

    @ApiOperation("新增资讯")
//    @Log(moduleType = ModuleType.INFO, operType = OperType.OTHER)
//    @RequiresPermissions({"/perms"})
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid InfoReq req) {
        String label = "sys:info:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Info entity = infoService.add(req);

            String logContent = String.format("新增资讯，资讯标题：“%s”", entity.getTitle());
            return ApiResult.log(entity.getId(), logContent);
        });
        return apiResult;
    }

    @ApiOperation("编辑资讯")
//    @Log(moduleType = ModuleType.INFO, operType = OperType.OTHER)
//    @RequiresPermissions({"/perms"})
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid InfoReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:info:edit:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Info entity = infoService.edit(req);

            String logContent = String.format("编辑资讯，资讯标题：“%s”", entity.getTitle());
            return ApiResult.log(entity.getId(), logContent);
        });
        return apiResult;
    }

    @ApiOperation("删除资讯")
//    @Log(moduleType = ModuleType.INFO, operType = OperType.OTHER)
//    @RequiresPermissions({"/perms"})
    @PostMapping("/delete")
    public ApiResult delete(@RequestBody @Valid IdReq req) {
        Info entity = getAndCheck(req.getId());
        if (entity.getDeleted()) {
            return ApiResult.ok();
        }

        entity.setDeleted(true);
        infoService.updateSelective(entity);

        String logContent = String.format("删除资讯，资讯标题：“%s”", entity.getTitle());
        return ApiResult.log(entity.getId(), logContent);
    }

    @ApiOperation("上下架")
//    @Log(moduleType = ModuleType.INFO, operType = OperType.OTHER)
//    @RequiresPermissions("/factor/list/info/status")
    @PostMapping(value = "/status")
    public ApiResult status(@RequestBody @Valid InfoStatusReq req) {
        if (req.getInfoStatus() == InfoStatus.WAIT_PUBLISH) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:info:status:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Info entity = getAndCheck(req.getId());

            entity.setInfoStatus(req.getInfoStatus());

            String oper = null;
            if (req.getInfoStatus() == InfoStatus.PUBLISHED) {
                // 发布
                entity.setPublishBy(SecurityUtils.getUserId());
                entity.setPublishTime(new Date());
                oper = "上架";
            } else {
                // 下架
                entity.setPublishBy(null);
                entity.setPublishTime(null);
                oper = "下架";
            }

            infoService.update(entity);

            String logContent = String.format("%s资讯，资讯标题：“%s”", oper, entity.getTitle());
            return ApiResult.log(logContent);
        });
        return apiResult;
    }

    /**
     * 查询并校验
     *
     * @param id
     * @return
     */
    private Info getAndCheck(Long id) {
        if (NumUtil.isNullOrZero(id)) {
            throw new ServiceException(ApiCode.REQUEST_PARAM_ERROR);
        }

        Info entity = infoService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new ServiceException(ApiCode.REQUEST_PARAM_ERROR);
        }
        if (SecurityUtils.missCompany(entity.getCompanyId())) {
            throw new ServiceException(ApiCode.COMPANY_PERMS_ERROR);
        }
        return entity;
    }

}