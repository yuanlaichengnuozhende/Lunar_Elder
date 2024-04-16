package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.core.utils.tree.Tree;
import com.lunar.common.core.utils.tree.TreeUtil;
import com.lunar.common.localcache.utils.LocalCacheUtil;
import com.lunar.common.localcache.utils.UnitUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.dto.UnitConvertPairDto;
import com.lunar.system.entity.LibAddress;
import com.lunar.system.entity.LibUnitConversion;
import com.lunar.system.query.LibAddressQuery;
import com.lunar.system.query.LibUnitConversionQuery;
import com.lunar.system.request.LibUnitConversionReq;
import com.lunar.system.service.LibAddressService;
import com.lunar.system.service.LibUnitConversionService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.DictConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.core.utils.tree.Tree;
import com.lunar.common.core.utils.tree.TreeUtil;
import com.lunar.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 库
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "库（地址库&单位库）")
@RequestMapping("/lib")
public class LibController {

    private final LibAddressService libAddressService;
    private final LibUnitConversionService libUnitConversionService;

    /**
     * 查询地址库
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/address/addressByCodes")
    public ApiResult<List<LibAddress>> addressByCodes(@RequestParam("codes") List<Integer> codes) {
        List<LibAddress> list = libAddressService.findList(LibAddressQuery.builder().codeList(codes).build());
        return ApiResult.ok(list);
    }

    /**
     * 单位换算
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/unit/unitConvert")
    public ApiResult<String> innerUnitConvert(@RequestParam("unitFrom") String unitFrom,
                                              @RequestParam("unitTo") String unitTo) {
        String value = libUnitConversionService.convert(unitFrom, unitTo);
        return ApiResult.ok(value);
    }

    /**
     * 单位批量换算
     * <p>
     * key from-to value 换算比例值
     */
    @InnerAuth
    @ApiIgnore
    @PostMapping("/inner/unit/batchUnitConvert")
    public ApiResult<Map<String, String>> batchUnitConvert(@RequestBody List<UnitConvertPairDto> pairs) {
        Map<String, String> map = libUnitConversionService.batchConvert(pairs);
        return ApiResult.ok(map);
    }

    /**
     * 所有单位换算
     * <p>
     * key from-to value 换算比例值
     */
    @InnerAuth
    @ApiIgnore
    @PostMapping("/inner/unit/allUnitConvert")
    public ApiResult<Map<String, String>> allUnitConvert() {
        Map<String, String> map = libUnitConversionService.allConvert();
        return ApiResult.ok(map);
    }

    @ApiOperation(value = "地址库分级查询")
    @ApiImplicitParam(name = "pCode", value = "父级地址code，省级为0", required = false, dataType = "int", paramType = "query")
    @GetMapping("/address/list")
    public ApiResult<List<Tree>> addressList(@NotNull(message = "pCode不能为空") Integer pCode) {
        List<LibAddress> list = libAddressService.findList(LibAddressQuery.builder().pCode(pCode).build());
        List<Tree> treeList = list.stream().map(this::libAddress2Tree).collect(Collectors.toList());
        return ApiResult.ok(treeList);
    }

    @ApiOperation(value = "地址库查询")
    @ApiImplicitParam(name = "addressCode", value = "地址code", required = true, dataType = "int", paramType = "query")
    @GetMapping("/address/code")
    public ApiResult<Tree> addressCode(@NotNull(message = "addressCode不能为空") Integer addressCode) {
        LibAddress dto = libAddressService.selectOne(LibAddressQuery.builder().addressCode(addressCode).build());
        Tree tree = libAddress2Tree(dto);
        return ApiResult.ok(tree);
    }

    @ApiOperation(value = "地址树")
    @ApiImplicitParam(name = "level", value = "地址级别。省 1 市 2 区 3", required = false, dataType = "int", paramType = "query")
    @GetMapping("/address/tree")
    public ApiResult<List<Tree>> addressTree(@RequestParam(value = "level", defaultValue = "3") Integer level) {
        List<LibAddress> list = libAddressService.findList(LibAddressQuery.builder().ltLevel(level - 1).build());
        List<Tree> treeList = list.stream().map(this::libAddress2Tree).collect(Collectors.toList());

        List<Tree> tree = TreeUtil.toTree(treeList);

        return ApiResult.ok(tree);
    }

    @ApiOperation("单位换算比例")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "unitFrom", value = "unitFrom", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "unitTo", value = "unitTo", required = false, dataType = "int", paramType = "query")
    })
    @GetMapping("/unit/unitConvert")
    public ApiResult<String> unitConvert(@RequestParam("unitFrom") String unitFrom,
                                         @RequestParam("unitTo") String unitTo) {
        String value = libUnitConversionService.convert(unitFrom, unitTo);
        return ApiResult.ok(value);
    }

    @ApiOperation("单位换算列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "unit", value = "单位", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "unitClass", value = "单位类别", required = false, dataType = "string", paramType = "query")
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
    @RequiresPermissions({"/sys/units"})
    @GetMapping("/unit/page")
    public ApiResult<IPage<LibUnitConversion>> unitPage(@Validated PageParam pageParam,
                                                        String unit, String unitClass) {
        IPage<LibUnitConversion> page = libUnitConversionService.findPage(
            LibUnitConversionQuery.builder()
                .unitClass(unitClass)
                .unit(unit)
                .deleted(false)
                .build(),
            pageParam, Order.createTimeDesc());

        // 反显字典
        page.getList().forEach(x -> {
            x.setUnitClassName(LocalCacheUtil.getDataLabelByDictValue(DictConsts.FACTOR_UNIT_M, x.getUnitClass()));

            x.setUnitFromName(LocalCacheUtil.getEnumLabelByDictValue(DictConsts.FACTOR_UNIT_M, x.getUnitFrom()));

            x.setUnitToName(LocalCacheUtil.getEnumLabelByDictValue(DictConsts.FACTOR_UNIT_M, x.getUnitTo()));
        });

        return ApiResult.ok(page);
    }

    @ApiOperation("新增单位换算")
    @Log(moduleType = ModuleType.UNIT_CONVERSION, operType = OperType.OTHER)
    @RequiresPermissions("/sys/units/add")
    @PostMapping("/unit/add")
    public ApiResult unitAdd(@RequestBody @Valid LibUnitConversionReq req) {
        Long userId = SecurityUtils.getUserId();

        // 校验入参
        LibUnitConversion exist = libUnitConversionService.selectConvert(req.getUnitFrom(), req.getUnitTo());
        if (exist != null) {
            return ApiResult.error(SystemCode.UNIT_CONVERSION_EXIST);
        }

        LibUnitConversion libUnitConversion = CopyUtil.copyObject(req, LibUnitConversion.class);
        libUnitConversion.setId(null);
        libUnitConversion.setCreateBy(userId);
        libUnitConversion.setUpdateBy(userId);
        libUnitConversionService.insertSelective(libUnitConversion);

        String from = UnitUtil.formatFactUnitM(libUnitConversion.getUnitFrom());
        String to = UnitUtil.formatFactUnitM(libUnitConversion.getUnitTo());

        String log = String.format("新增单位换算，单位：“%s和%s”", from, to);
        return ApiResult.log(libUnitConversion.getId(), log);
    }

    @ApiOperation("编辑单位换算")
    @Log(moduleType = ModuleType.UNIT_CONVERSION, operType = OperType.OTHER)
    @RequiresPermissions("/sys/units/edit")
    @PostMapping("/unit/edit")
    public ApiResult unitEdit(@RequestBody @Valid LibUnitConversionReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        LibUnitConversion libUnitConversion = libUnitConversionService.selectByPrimaryKey(req.getId());
        if (libUnitConversion == null || libUnitConversion.getDeleted()) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        // 校验入参
        LibUnitConversion exist = libUnitConversionService.selectConvert(req.getUnitFrom(), req.getUnitTo());
        if (exist != null && !exist.getId().equals(libUnitConversion.getId())) {
            return ApiResult.error(SystemCode.UNIT_CONVERSION_EXIST);
        }

        BeanUtils.copyProperties(req, libUnitConversion);
        libUnitConversion.setUpdateBy(SecurityUtils.getUserId());
        libUnitConversion.setUpdateTime(new Date());
        libUnitConversionService.updateSelective(libUnitConversion);

        String from = UnitUtil.formatFactUnitM(libUnitConversion.getUnitFrom());
        String to = UnitUtil.formatFactUnitM(libUnitConversion.getUnitTo());

        String log = String.format("编辑单位换算，单位：“%s和%s”", from, to);
        return ApiResult.log(libUnitConversion.getId(), log);
    }

    @ApiOperation("删除单位换算")
    @Log(moduleType = ModuleType.UNIT_CONVERSION, operType = OperType.OTHER)
    @RequiresPermissions("/sys/units/del")
    @PostMapping(value = "/unit/delete")
    public ApiResult unitDelete(@RequestBody @Valid IdReq req) {
        LibUnitConversion libUnitConversion = libUnitConversionService.selectByPrimaryKey(req.getId());
        if (Objects.isNull(libUnitConversion)) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (libUnitConversion.getDeleted()) {
            return ApiResult.ok();
        }

        libUnitConversion.setDeleted(true);
        libUnitConversion.setUpdateBy(SecurityUtils.getUserId());
        libUnitConversionService.updateSelective(libUnitConversion);

        String log = String.format("删除单位换算，单位：“%s和%s”", libUnitConversion.getUnitFrom(),
            libUnitConversion.getUnitTo());
        return ApiResult.log(libUnitConversion.getId(), log);
    }

    private Tree libAddress2Tree(LibAddress dto) {
        if (dto == null) {
            return null;
        }

        return Tree.builder()
            .code(dto.getAddressCode())
            .name(dto.getAddressName())
            .pcode(dto.getPCode())
            .build();
    }

}
