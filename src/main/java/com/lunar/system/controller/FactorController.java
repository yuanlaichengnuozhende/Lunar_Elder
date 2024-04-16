package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.GasType;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.localcache.utils.LocalCacheUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.FileUploadUtil;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Factor;
import com.lunar.system.entity.FactorGas;
import com.lunar.system.query.FactorGasQuery;
import com.lunar.system.query.FactorQuery;
import com.lunar.system.request.EnableStatusReq;
import com.lunar.system.request.FactorReq;
import com.lunar.system.service.FactorGasService;
import com.lunar.system.service.FactorService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.GasType;
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
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 排放因子库
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "排放因子库")
@RequestMapping("/factor")
public class FactorController {

    private final RedissonClient redissonClient;
    private final FactorService factorService;
    private final FactorGasService factorGasService;

    @ApiOperation("因子列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeName", value = "名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "year", value = "发布年份", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "likeInstitution", value = "发布机构", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "firstClassify", value = "一级分类", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "secondClassify", value = "二级分类", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "likeDescription", value = "适用场景", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "status", value = "状态。0 启用 1 禁用", required = false, dataType = "int", paramType = "query")
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
    @RequiresPermissions({"/factor/list"})
    @GetMapping("/page")
    public ApiResult<IPage<Factor>> page(@Validated PageParam pageParam,
                                         String likeName, String year, String likeInstitution,
                                         String firstClassify, String secondClassify,
                                         String likeDescription, EnableStatus status) {
        FactorQuery query = FactorQuery.builder()
            .likeName(likeName)
            .year(year)
            .likeInstitution(likeInstitution)
            .firstClassify(firstClassify)
            .secondClassify(secondClassify)
            .likeDescription(likeDescription)
            .status(status)
            .deleted(false)
            .build();

        IPage<Factor> page = factorService.findPage(query, pageParam,
            "create_time desc, id desc");

        // 反显字典
        page.getList().forEach(x -> {
            x.setFirstClassify(LocalCacheUtil.getEnumLabelByDictValue(DictConsts.firstClassify, x.getFirstClassify()));

            x.setSecondClassify(LocalCacheUtil.getEnumLabelByDictValue(DictConsts.secondClassify, x.getSecondClassify()));
        });

        return ApiResult.ok(page);
    }

    @ApiOperation("详情")
    @RequiresPermissions("/factor/list/info")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "int", paramType = "query")
    @GetMapping("/{id}")
    public ApiResult<Factor> detail(@PathVariable Long id) {
        Factor factor = factorService.selectByPrimaryKey(id);
        if (factor == null) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (factor.getDeleted()) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        List<FactorGas> gasList = factorGasService.findList(
            FactorGasQuery.builder().factorId(factor.getId()).build(), "id asc");
        factor.setGasList(gasList);

        return ApiResult.ok(factor);
    }

    @ApiOperation("新增")
    @Log(moduleType = ModuleType.FACTOR, operType = OperType.OTHER)
    @RequiresPermissions("/factor/list/info/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid FactorReq req) {
        if (CollectionUtils.isEmpty(req.getGasList())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_MISSING);
        }
        // 二氧化碳当量校验
        boolean valid = req.getGasList()
            .stream()
            .filter(x -> GasType.getByName(x.getGasType()) == GasType.CO2E)
            .anyMatch(FactorGas::isValid);
        if (!valid) {
            return ApiResult.error(SystemCode.FACTOR_CO2E);
        }

        String label = "sys:factor:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Factor factor = factorService.add(req);
            String log = String.format("新增排放因子，排放因子名称：“%s”", factor.getName());
            return ApiResult.log(factor.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("编辑")
    @Log(moduleType = ModuleType.FACTOR, operType = OperType.OTHER)
    @RequiresPermissions("/factor/list/info/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid FactorReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }
        if (CollectionUtils.isEmpty(req.getGasList())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_MISSING);
        }
        // 二氧化碳当量校验
        boolean valid = req.getGasList()
            .stream()
            .filter(x -> GasType.CO2E.getName().equalsIgnoreCase(x.getGasType()))
            .anyMatch(FactorGas::isValid);
        if (!valid) {
            return ApiResult.error(SystemCode.FACTOR_CO2E);
        }

        String label = "sys:factor:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Factor factor = factorService.edit(req);
            String log = String.format("编辑排放因子，排放因子名称：“%s”", factor.getName());
            return ApiResult.log(factor.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("启用禁用")
    @RequiresPermissions("/factor/list/info/status")
    @Log(moduleType = ModuleType.FACTOR, operType = OperType.OTHER)
    @PostMapping(value = "/status")
    public ApiResult status(@RequestBody @Valid EnableStatusReq req) {
        Factor factor = factorService.selectByPrimaryKey(req.getId());
        if (factor == null) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (factor.getDeleted()) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        String label = "sys:factor:status:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            factor.setStatus(req.getStatus());
            factor.setUpdateBy(SecurityUtils.getUserId());
            factorService.updateSelective(factor);

            String content = String.format("%s排放因子，排放因子名称：“%s”", req.getStatus().getName(), factor.getName());

            return ApiResult.log(content);
        });
        return apiResult;
    }

}
