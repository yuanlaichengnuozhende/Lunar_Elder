package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.DictType;
import com.lunar.system.query.DictTypeQuery;
import com.lunar.system.request.DictTypeReq;
import com.lunar.system.response.DictTypeResp;
import com.lunar.system.service.DictTypeService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
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

import javax.validation.Valid;

/**
 * 数据字典 Controller
 *
 * @author liff
 * @date 2021-12-17
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "字典管理")
@RequestMapping("/dicttype")
public class DictTypeController {

    private final RedissonClient redissonClient;
    private final DictTypeService cDictTypeService;

    /**
     * 查询字典列表
     */
    @ApiOperation("字典列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dictName", value = "字典名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "dictType", value = "字典类型", required = false, dataType = "string", paramType = "query"),
    })
    @RequiresPermissions("/sys/dicttype")
    @GetMapping("/page")
    public ApiResult<IPage<DictTypeResp>> page(@Validated PageParam pageReq,
                                               String dictName,
                                               String dictType) {

        DictTypeQuery query = DictTypeQuery.builder()
            .likeDictName(dictName)
            .dictType(dictType)
            .build();
        IPage<DictTypeResp> page = cDictTypeService.findDictTypePage(query, pageReq.getPageNum(), pageReq.getPageSize(),
            "create_time desc,id desc");

        return ApiResult.ok(page);
    }

    @ApiOperation("新增字典")
    @Log(moduleType = ModuleType.DICT, operType = OperType.OTHER)
    @RequiresPermissions("/sys/dicttype/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid DictTypeReq req) {
        String label = "sys:dicttype:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            DictType dictType = cDictTypeService.add(req);

            String log = String.format("新增字典，字典名称：“%s”" + ",字典标识：“%s”", dictType.getDictName(),
                dictType.getDictType());
            return ApiResult.log(dictType.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("编辑字典")
    @Log(moduleType = ModuleType.DICT, operType = OperType.OTHER)
    @RequiresPermissions("/sys/dicttype/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid DictTypeReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:dicttype:edit:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            DictType dictType = cDictTypeService.edit(req);

            String log = String.format("编辑字典，字典名称：“%s”" + ",字典标识：“%s”", dictType.getDictName(),
                dictType.getDictType());
            return ApiResult.log(dictType.getId(), log);
        });
        return apiResult;
    }
}
