package com.lunar.system.controller;

import cn.hutool.core.convert.Convert;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Org;
import com.lunar.system.entity.UserOrg;
import com.lunar.system.model.OrgTree;
import com.lunar.system.model.OrgTreeSelect;
import com.lunar.system.model.OrgTreeUtil;
import com.lunar.system.query.OrgQuery;
import com.lunar.system.query.UserOrgQuery;
import com.lunar.system.request.OrgReq;
import com.lunar.system.service.OrgService;
import com.lunar.system.service.UserOrgService;
import com.lunar.system.service.UserService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组织管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "组织管理")
@RequestMapping("/org")
public class OrgController {

    private final RedissonClient redissonClient;
    private final OrgService orgService;
    private final UserOrgService userOrgService;
    private final UserService userService;

    /**
     * 公司全量组织列表
     */
    @ApiIgnore
    @InnerAuth
    @GetMapping("/inner/orgList/{companyId}")
    public ApiResult<List<Org>> innerOrgList(@PathVariable("companyId") Long companyId) {
        List<Org> list = orgService.findList(OrgQuery.builder().companyId(companyId).deleted(false).build(),
            "pid asc, id asc");
        return ApiResult.ok(list);
    }

    /**
     * 全量组织列表
     */
    @ApiIgnore
    @InnerAuth
    @GetMapping("/inner/allOrgList")
    public ApiResult<List<Org>> innerAllOrgList() {
        List<Org> list = orgService.findList(OrgQuery.builder().deleted(false).build(),
            "company_id asc, pid asc, id asc");
        return ApiResult.ok(list);
    }

    @ApiOperation("用户所属组织下拉框")
    @ApiImplicitParam(name = "likeOrgName", value = "组织名称", required = false, dataType = "string", paramType = "query")
    @GetMapping("/user/list")
    public ApiResult<List<OrgPojo>> userList(String likeOrgName) {
        List<OrgPojo> orgList = SecurityUtils.getOrgList();
        String orgName = Convert.toStr(likeOrgName, "");

        List<OrgPojo> respList = orgList.stream()
            .filter(x -> x.getOrgName().contains(orgName))
            .collect(Collectors.toList());
        return ApiResult.ok(respList);
    }

    @ApiOperation("组织树")
    @ApiImplicitParam(name = "userId", value = "用户id", required = false, dataType = "int", paramType = "query")
    @RequiresPermissions({"/orgs/manage", "/sys/user/configOrg"})
    @GetMapping("/tree")
    public ApiResult<OrgTreeSelect> tree(Long userId) {
        List<Org> list = orgService.findList(
            OrgQuery.builder().companyId(SecurityUtils.getCompanyId()).deleted(false).build(), "id asc");

        Map<Long, String> updateByMap = userService.getUpdateByMap(list);

        List<OrgTree> treeList = list.stream().map(x -> {
            String updateByName = updateByMap.getOrDefault(x.getUpdateBy(), "");
            int level = StringUtils.countMatches(x.getOrgPath(), "/");
            return OrgTree.builder()
                .code(x.getId())
                .name(x.getOrgName())
                .pcode(x.getPid())
                .sortId(x.getId())
                .orgType(x.getOrgType())
                .updateBy(x.getUpdateBy())
                .updateTime(x.getUpdateTime())
                .abbr(x.getOrgAbbr())
                .orgCode(x.getOrgCode())
                .updateByName(updateByName)
                .level(level)
                .build();
        }).collect(Collectors.toList());

        List<OrgTree> tree = OrgTreeUtil.toTree(treeList);

        OrgTreeSelect treeSelect = OrgTreeSelect.builder().tree(tree).build();

        // 查询用户所有组织id
        if (!NumUtil.isNullOrZero(userId)) {
            List<UserOrg> relList = userOrgService.findList(
                UserOrgQuery.builder().userId(userId).build(), Order.idAsc());

            Map<OptionType, List<Long>> collect = relList.stream()
                .collect(Collectors.groupingBy(UserOrg::getOptionType,
                    Collectors.mapping(UserOrg::getOrgId, Collectors.toList())));
            treeSelect.setAllCheckedList(collect.get(OptionType.ALL));
            treeSelect.setHalfCheckedList(collect.get(OptionType.HALF));
        }

        return ApiResult.ok(treeSelect);
    }

    @ApiOperation("组织列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeOrgName", value = "组织名称", required = false, dataType = "string", paramType = "query")
    })
    @RequiresPermissions({"/orgs/manage", "/sys/user/configOrg"})
    @GetMapping("/page")
    public ApiResult<IPage<Org>> page(@Validated PageParam pageParam,
                                      String likeOrgName) {
        IPage<Org> page = orgService.findPage(
            OrgQuery.builder()
                .companyId(SecurityUtils.getCompanyId())
                .likeOrgName(likeOrgName)
                .deleted(false)
                .build(),
            pageParam, Order.createTimeAsc());
        return ApiResult.ok(page);
    }

    @ApiOperation("组织详情")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "int", paramType = "query")
    @RequiresPermissions("/sys/org/detail")
    @GetMapping("/{id}")
    public ApiResult<Org> detail(@PathVariable Long id) {
        Org org = orgService.selectByPrimaryKey(id);
        if (SecurityUtils.missCompany(org.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }

        return ApiResult.ok(org);
    }

    @ApiOperation("新增组织")
    @Log(moduleType = ModuleType.COMPANY, operType = OperType.OTHER)
    @RequiresPermissions("/sys/org/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid OrgReq req) {
        // 不允许新增根组织
        if (req.getPid() == 0 || req.getOrgType() == OrgType.GROUP) {
            return ApiResult.error(SystemCode.ORG_ROOT_CANNOT_ADD);
        }
//        // 单体组织不能新增子组织
//        if (req.getOrgType() == OrgType.SINGLE) {
//            return ApiResult.error(SystemCode.ORG_SINGLE_CANNOT_ADD);
//        }

        String label = "sys:org:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Org org = orgService.add(req);

            String log = String.format("新增子组织，组织名称：“%s”" + "，组织编号：“%s”", org.getOrgName(),
                org.getOrgCode());
            return ApiResult.log(org.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("编辑组织")
    @Log(moduleType = ModuleType.COMPANY, operType = OperType.OTHER)
    @RequiresPermissions("/sys/org/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid OrgReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:org:edit:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Org org = orgService.edit(req);

            String log = String.format("编辑组织，组织名称：“%s”" + "，组织编号：“%s”", org.getOrgName(), org.getOrgCode());
            return ApiResult.log(org.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("删除组织")
    @RequiresPermissions("/sys/org/del")
    @Log(moduleType = ModuleType.COMPANY, operType = OperType.OTHER)
    @PostMapping(value = "/delete")
    public ApiResult delete(@RequestBody @Valid IdReq req) {
        Org org = orgService.selectByPrimaryKey(req.getId());
        if (ObjectUtils.isEmpty(org)) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (SecurityUtils.missCompany(org.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }
        if (org.getDeleted()) {
            return ApiResult.ok();
        }

        // 不允许删除根组织
        if (org.getPid() == 0 || org.getOrgType() == OrgType.GROUP || org.getOrgType() == OrgType.SINGLE) {
            return ApiResult.error(SystemCode.ORG_ROOT_CANNOT_ADD);
        }

        orgService.delete(org);

        String log = String.format("删除组织，组织名称：“%s”" + "，组织编号：“%s”", org.getOrgName(), org.getOrgCode());
        return ApiResult.log(log);
    }

}
