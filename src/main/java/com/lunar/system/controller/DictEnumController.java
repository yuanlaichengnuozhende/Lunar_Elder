package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.DictData;
import com.lunar.system.entity.DictEnum;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.query.DictEnumQuery;
import com.lunar.system.request.DictEnumReq;
import com.lunar.system.response.DictEnumResp;
import com.lunar.system.service.DictDataService;
import com.lunar.system.service.DictEnumService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@Api(tags = "字典枚举值管理")
@RequestMapping("/dictenum")
public class DictEnumController {

    private final RedissonClient redissonClient;
    private final DictEnumService dictEnumService;
    private final DictDataService cDictDataService;

    /**
     * 字典枚举值列表
     *
     * @param dictType
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/list/{dictType}")
    public ApiResult<List<DictEnum>> list(@PathVariable("dictType") String dictType) {
        log.info("字典枚举值列表. dictType={}", dictType);
        List<DictEnum> list = dictEnumService.listByDictType(dictType);
        return ApiResult.ok(list);
    }

    /**
     * 根据dictLabel查询
     *
     * @param dictType
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/getByDictLabel/{dictType}/{dictLabel}")
    public ApiResult<DictEnum> getByDictLabel(@PathVariable("dictType") String dictType,
                                              @PathVariable("dictLabel") String dictLabel) {
        log.info("根据dictLabel查询. dictType={}", dictType);
        DictEnum dictEnum = dictEnumService.getByDictLabel(dictType, dictLabel);
        return ApiResult.ok(dictEnum);
    }

    /**
     * 根据dictValue查询
     *
     * @param dictType
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/getByDictValue/{dictType}/{dictValue}")
    public ApiResult<DictEnum> getByDictValue(@PathVariable("dictType") String dictType,
                                              @PathVariable("dictValue") String dictValue) {
        log.info("根据dictValue查询. dictValue={}", dictValue);
        DictEnum dictEnum = dictEnumService.getByDictValue(dictType, dictValue);
        return ApiResult.ok(dictEnum);
    }

    /**
     * 查询字典枚举值列表
     */
    @ApiOperation("字典枚举值列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dictType", value = "字典类型", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "dictLabel", value = "枚举值名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "dictValue", value = "枚举值标识", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "sourceType", value = "所属分类", required = false, dataType = "string", paramType = "query"),
    })
    @RequiresPermissions("/sys/dictenum")
    @GetMapping("/page")
    public ApiResult<IPage<DictEnumResp>> page(@Validated PageParam pageReq,
                                               @NotBlank(message = "字典类型不能为空") String dictType,
                                               String dictLabel, String dictValue, String sourceType) {

        if (StringUtils.isEmpty(dictType)) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        DictEnumQuery query = DictEnumQuery.builder()
            .dictType(dictType)
            .likeDictLabel(dictLabel)
            .dictValue(dictValue)
            .sourceType(sourceType)
            .build();
        IPage<DictEnumResp> page = dictEnumService.findDictEnumPage(query, pageReq.getPageNum(), pageReq.getPageSize(),
            "dict_sort asc, id asc");

        List<String> dictValues = page.getList().stream().map(DictEnumResp::getSourceType).collect(Collectors.toList());
        List<DictData> dictList = cDictDataService.findList(
            DictDataQuery.builder().dictType(dictType).dictValues(dictValues).build());
        Map<String, DictData> dataMap = ConvertUtil.list2Map(dictList, DictData::getDictValue);

        page.getList().forEach(x -> {
            if (dataMap.size() > 0) {
                DictData dictData = dataMap.getOrDefault(x.getSourceType(), new DictData());
                x.setSourceTypeName(dictData.getDictLabel());
            }
        });

        return ApiResult.ok(page);
    }

    @ApiOperation("根据字典标识查询字典枚举值-批量查询")
    @GetMapping("/listAllByDictTypeBatch")
    public ApiResult listAllByDictTypeBatch(@NotBlank(message = "字典类型不能为空") String dictTypes) {
        Map<String, Object> map = dictEnumService.listBatchByDictType(dictTypes);
        return ApiResult.ok(map);
    }

    @ApiOperation("新增字典枚举值")
    @Log(moduleType = ModuleType.DICT, operType = OperType.OTHER)
//    @RequiresPermissions("/sys/dictenum/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid DictEnumReq req) {
        String label = "sys:dictenum:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            DictEnum dictEnum = dictEnumService.add(req);

            String log = String.format("新增枚举值，枚举值名称：“%s”" + "，枚举值标识：“%s”", dictEnum.getDictLabel(),
                dictEnum.getDictValue());
            return ApiResult.log(dictEnum.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("编辑字典枚举值")
    @Log(moduleType = ModuleType.DICT, operType = OperType.OTHER)
//    @RequiresPermissions("/sys/dictenum/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid DictEnumReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:dictenum:edit:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            DictEnum dictEnum = dictEnumService.edit(req);

            String log = String.format("编辑枚举值，枚举值名称：“%s”" + "，枚举值标识：“%s”", dictEnum.getDictLabel(),
                dictEnum.getDictValue());
            return ApiResult.log(dictEnum.getId(), log);
        });
        return apiResult;
    }

}
