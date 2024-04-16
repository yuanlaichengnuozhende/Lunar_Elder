package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.I18nType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.redis.utils.I18nUtil;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.I18n;
import com.lunar.system.entity.I18nLang;
import com.lunar.system.model.I18nLangModel;
import com.lunar.system.query.I18nLangQuery;
import com.lunar.system.query.I18nQuery;
import com.lunar.system.request.I18nLangReq;
import com.lunar.system.response.I18nResp;
import com.lunar.system.service.I18nLangService;
import com.lunar.system.service.I18nService;
import com.google.common.collect.Lists;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.I18nType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * i18n
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "多语言")
@RequestMapping("/i18n")
public class I18nController {

    private final I18nService i18nService;
    private final I18nLangService i18nLangService;

    @InnerAuth
    @ApiOperation("重新缓存")
    @GetMapping("/reCache")
    public ApiResult reCache() {
        i18nService.reCache();
        return ApiResult.ok();
    }

    @ApiOperation("切换语言")
    @ApiImplicitParam(name = "lang", value = "lang", required = true, dataType = "string", paramType = "query")
    @GetMapping("/switch/{lang}")
    public ApiResult<Map<String, String>> switchLang(@PathVariable String lang) {
        if (StringUtils.length(lang) > 100) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        I18nUtil.setUserLang(lang, SecurityUtils.getUserId());

        Map<String, String> map = i18nService.getCache(lang);
        return ApiResult.ok(map);
    }

    @ApiOperation("查询语言缓存")
    @ApiImplicitParam(name = "lang", value = "lang", required = true, dataType = "string", paramType = "query")
    @GetMapping("/cache/{lang}")
    public ApiResult<Map<String, String>> getLangCache(@PathVariable String lang) {
        Map<String, String> map = i18nService.getCache(lang);
        return ApiResult.ok(map);
    }

    @ApiOperation("字段列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "i18nType", value = "所属产品。1 通用 2 错误码 3 系统管理 4 碳因子库 5 组织碳核算 6 产品碳阻迹 7 供应链 8 碳账户 9 降碳课题", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "likeFieldKey", value = "字段标识", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "likeFieldName", value = "字段名称", required = false, dataType = "string", paramType = "query")
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
    @RequiresPermissions("/sys/language")
    @GetMapping("/page")
    public ApiResult<IPage<I18n>> page(@Validated PageParam pageReq,
                                       I18nType i18nType, String likeFieldKey, String likeFieldName) {
        I18nQuery query = I18nQuery.builder()
            .i18nType(i18nType)
            .likeFieldKey(likeFieldKey)
            .likeFieldName(likeFieldName)
            .deleted(false)
            .build();
        IPage<I18n> page = i18nService.findPage(query, pageReq);

        return ApiResult.ok(page);
    }

    @ApiOperation("字段明细")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "int", paramType = "query")
    @RequiresPermissions("/sys/language/detail")
    @GetMapping("/{id}")
    public ApiResult<I18nResp> detail(@PathVariable Long id) {
        I18n i18n = i18nService.selectByPrimaryKey(id);
        if (i18n == null || i18n.getDeleted()) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        I18nResp resp = CopyUtil.copyObject(i18n, I18nResp.class);

        // 英文
        I18nLangModel lang = I18nLangModel.builder()
            .fieldKey(i18n.getFieldKey())
            .langDict(Consts.EN_KEY)
            .langValue(i18n.getFieldNameEn())
            .build();

        List<I18nLang> otherList = i18nLangService.findList(
            I18nLangQuery.builder().fieldKey(i18n.getFieldKey()).deleted(false).build());

        List<I18nLangModel> langList = Lists.newArrayList(lang);
        if (CollectionUtils.isNotEmpty(otherList)) {
            langList.addAll(CopyUtil.copyList(otherList, I18nLangModel.class));
        }

        resp.setLangList(langList);

        return ApiResult.ok(resp);
    }

    @ApiOperation("编辑字段")
//    @Log(moduleType = ModuleType.LANG, operType = OperType.OTHER)
    @RequiresPermissions("/sys/language/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid I18nLangReq req) {
        I18n i18n = i18nService.selectFirst(
            I18nQuery.builder().fieldKey(req.getFieldKey()).deleted(false).build());
        if (i18n == null) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        i18nService.edit(i18n, req);

        String log = String.format("编辑多语言字段：“%s”，“%s”，“%s”", i18n.getFieldName(), i18n.getFieldKey(),
            i18n.getI18nType().getName());
        return ApiResult.log(log);
    }

}
