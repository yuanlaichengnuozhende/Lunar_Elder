package com.lunar.system.controller;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.core.utils.tree.Tree;
import com.lunar.common.core.utils.tree.TreeSelect;
import com.lunar.common.core.utils.tree.TreeUtil;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Permission;
import com.lunar.system.entity.RolePermission;
import com.lunar.system.query.PermissionQuery;
import com.lunar.system.query.RolePermissionQuery;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.service.PermissionService;
import com.lunar.system.service.RolePermissionService;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.core.utils.tree.Tree;
import com.lunar.common.core.utils.tree.TreeSelect;
import com.lunar.common.core.utils.tree.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "权限管理")
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;
    private final RolePermissionService rolePermissionService;

    @InnerAuth
    @ApiOperation("重新缓存权限点名称")
    @GetMapping("/reCache")
    public ApiResult reCache() {
        permissionService.reCache();
        return ApiResult.ok();
    }

    @ApiOperation("获取左侧菜单权限列表")
    @GetMapping("/router")
    public ApiResult<List<PermissionResp>> getRouter() {
        //获取用户权限
        List<PermissionResp> list = permissionService.findUserPermList(SecurityUtils.getUserId());
        return ApiResult.ok(list);
    }

    @ApiOperation("权限树")
    @ApiImplicitParam(name = "roleId", value = "角色id", required = false, dataType = "int", paramType = "query")
//    @RequiresPermissions("sys:permission:tree")
    @GetMapping("/tree")
    public ApiResult<TreeSelect> tree(Long roleId) {
        List<Permission> list = permissionService.findList(
            PermissionQuery.builder().deleted(false).build(), "pid,order_num,id asc");

        List<Tree> treeList = list.stream().map(x ->
                Tree.builder()
                    .code(x.getId())
                    .name(x.getPermissionName())
                    .pcode(x.getPid())
                    .sortId(x.getOrderNum())
                    .build())
            .collect(Collectors.toList());

        List<Tree> tree = TreeUtil.toTree(treeList);

        TreeSelect treeSelect = TreeSelect.builder().tree(tree).build();

        // 查询角色所有权限id
        if (!NumUtil.isNullOrZero(roleId)) {
            List<RolePermission> relList = rolePermissionService.findList(
                RolePermissionQuery.builder().roleId(roleId).build(), "id asc");

            Map<OptionType, List<Long>> collect = relList.stream()
                .collect(Collectors.groupingBy(RolePermission::getOptionType,
                    Collectors.mapping(RolePermission::getPermissionId, Collectors.toList())));
            treeSelect.setAllCheckedList(collect.get(OptionType.ALL));
            treeSelect.setHalfCheckedList(collect.get(OptionType.HALF));
        }

        return ApiResult.ok(treeSelect);
    }

    @ApiOperation("权限列表-分页")
    @RequiresPermissions("/sys/permission")
    @GetMapping("/page")
    public ApiResult<IPage<Permission>> page(@Validated PageParam pageParam) {
        IPage<Permission> page = permissionService.findPage(
            PermissionQuery.builder().deleted(false).build(), pageParam, Order.idAsc());
        return ApiResult.ok(page);
    }

    @ApiOperation("权限详情")
    @RequiresPermissions("/sys/permission/detail")
    @GetMapping("/{id}")
    public ApiResult<Permission> detail(@PathVariable Long id) {
        Permission permission = permissionService.selectByPrimaryKey(id);
        return ApiResult.ok(permission);
    }

//    @ApiOperation("新增权限")
//    @RequiresPermissions("/sys/permission/add")
//    @PostMapping("/add")
//    public ApiResult add(@RequestBody @Valid Permission req) {
//        Permission permission = CopyUtil.copyObject(req, Permission.class);
//        permission.setId(null);
//        permission.setCreateBy(SecurityUtils.getUserId());
//        permission.setUpdateBy(SecurityUtils.getUserId());
//        permissionService.insertSelective(permission);
//        return ApiResult.ok();
//    }
//
//    @ApiOperation("编辑权限")
//    @RequiresPermissions("/sys/permission/edit")
//    @PostMapping("/edit")
//    public ApiResult edit(@RequestBody @Valid Permission req) {
//        if (NumberUtil.isNullOrZero(req.getId())) {
//            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
//        }
//
//        Permission permission = permissionService.selectByPrimaryKey(req.getId());
//        if (permission == null) {
//            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
//        }
//
//        BeanUtils.copyProperties(req, permission, "create_by", "create_time");
//        permission.setUpdateBy(SecurityUtils.getUserId());
//        permission.setUpdateTime(new Date());
//        permissionService.updateSelective(permission);
//        return ApiResult.ok();
//    }
//
//    @ApiOperation("删除权限")
//    @RequiresPermissions("/sys/permission/delete")
//    @Log(moduleType = ModuleType.PERMISSION, operType = OperType.OTHER)
//    @PostMapping(value = "/delete")
//    public ApiResult delete(@RequestBody @Valid IdReq req) {
//        Permission permission = permissionService.selectByPrimaryKey(req.getId());
//        if (permission == null) {
//            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
//        }
//
//        permissionService.delete(permission);
//
//        String log = String.format("删除权限：“%s”", permission.getPermissionName());
//        return ApiResult.ok(log);
//    }

}
