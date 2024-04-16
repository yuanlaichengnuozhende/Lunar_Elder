package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.system.entity.DictData;
import com.lunar.system.query.DictDataQuery;
import com.lunar.system.request.DictDataReq;
import com.lunar.system.response.DictDataResp;
import com.lunar.system.service.DictDataService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags = "字典分类管理")
@RequestMapping("/dictdata")
public class DictDataController {

    @Autowired
    private DictDataService cDictDataService;

    /**
     * 字典分类值列表
     *
     * @param dictType
     * @return
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/list/{dictType}")
    public ApiResult<List<DictData>> list(@PathVariable("dictType") String dictType) {
        log.info("字典分类值列表. dictType={}", dictType);
        List<DictData> list = cDictDataService.findList(DictDataQuery.builder().dictType(dictType).status('1').build(),
            "dict_sort asc, id asc");
        return ApiResult.ok(list);
    }

    /**
     * 查询字典分类列表
     */
    @ApiOperation("字典分类列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dictType", value = "字典类型", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "dictLabel", value = "分类名称", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "dictValue", value = "分类标识", required = false, dataType = "string", paramType = "query"),
    })
    @RequiresPermissions("/sys/dictdata")
    @GetMapping("/page")
    public ApiResult<IPage<DictDataResp>> page(@Validated PageParam pageReq,
                                               @NotBlank(message = "字典类型不能为空") String dictType,
                                               String dictLabel, String dictValue) {

        if (StringUtils.isEmpty(dictType)) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        DictDataQuery query = DictDataQuery.builder()
            .dictType(dictType)
            .likeDictLabel(dictLabel)
            .dictValue(dictValue)
            .build();
        IPage<DictDataResp> page = cDictDataService.findDictDataPage(query, pageReq.getPageNum(), pageReq.getPageSize(),
            "dict_sort asc, id asc");

        return ApiResult.ok(page);
    }

    @ApiOperation("新增字典分类")
    @Log(moduleType = ModuleType.DICT, operType = OperType.OTHER)
//    @RequiresPermissions("/sys/dictdata/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid DictDataReq req) {

        DictData dictData = cDictDataService.add(req);

        String log = String.format("新增分类，分类名称：“%s”" + "，分类标识：“%s”", dictData.getDictLabel(),
            dictData.getDictValue());
        return ApiResult.log(dictData.getId(), log);
    }

    @ApiOperation("编辑字典分类")
    @Log(moduleType = ModuleType.DICT, operType = OperType.OTHER)
//    @RequiresPermissions("/sys/dictdata/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid DictDataReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        DictData dictData = cDictDataService.edit(req);

        String log = String.format("编辑分类，分类名称：“%s”" + "，分类标识：“%s”", dictData.getDictLabel(),
            dictData.getDictValue());
        return ApiResult.log(dictData.getId(), log);
    }
}
