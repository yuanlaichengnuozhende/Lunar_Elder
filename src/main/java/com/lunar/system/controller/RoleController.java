package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.mybatis.model.Order;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Role;
import com.lunar.system.entity.UserRole;
import com.lunar.system.enums.RoleType;
import com.lunar.system.query.RoleQuery;
import com.lunar.system.query.UserRoleQuery;
import com.lunar.system.request.RoleReq;
import com.lunar.system.service.RoleService;
import com.lunar.system.service.UserRoleService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "角色管理")
@RequestMapping("/role")
public class RoleController {

    private final RedissonClient redissonClient;
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    @ApiOperation("角色列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeRoleName", value = "角色名称", required = false, dataType = "string", paramType = "query")
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
    @RequiresPermissions({"/orgs/roles", "/sys/user/configRole"})
    @GetMapping("/page")
    public ApiResult<IPage<Role>> page(@Validated PageParam pageParam,
                                       String likeRoleName) {
        IPage<Role> page = roleService.findPage(
            RoleQuery.builder()
                .companyId(SecurityUtils.getCompanyId())
                .likeRoleName(likeRoleName)
                .deleted(false)
                .build(),
            pageParam, Order.createTimeAsc());
        if (CollectionUtils.isEmpty(page.getList())) {
            return ApiResult.ok(page);
        }

        List<Long> idList = page.getList().stream().map(Role::getId).collect(Collectors.toList());
        List<UserRole> relList = userRoleService.findList(UserRoleQuery.builder().roleIdList(idList).build());
        Map<Long, Long> relMap = relList.stream()
            .collect(Collectors.groupingBy(UserRole::getRoleId, Collectors.counting()));
        page.getList().forEach(e -> {
            Long count = relMap.get(e.getId());
            e.setLinkUser(count != null && count > 0L);
        });

        return ApiResult.ok(page);
    }

    @ApiOperation("角色详情")
    @RequiresPermissions("/sys/role/detail")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "int", paramType = "query")
    @GetMapping("/{id}")
    public ApiResult<Role> detail(@PathVariable Long id) {
        Role role = roleService.selectByPrimaryKey(id);
        if (role == null) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (SecurityUtils.missCompany(role.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }

        return ApiResult.ok(role);
    }

    @ApiOperation("新增角色")
    @Log(moduleType = ModuleType.ROLE, operType = OperType.OTHER)
    @RequiresPermissions("/sys/role/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid RoleReq req) {
        String label = "sys:role:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Role role = roleService.add(req);

            String log = String.format("新增角色，角色名称：“%s”", role.getRoleName());
            return ApiResult.log(role.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("编辑角色")
    @Log(moduleType = ModuleType.ROLE, operType = OperType.OTHER)
    @RequiresPermissions("/sys/role/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid RoleReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:role:edit:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            Role role = roleService.edit(req);

            String log = String.format("编辑角色，角色名称：“%s”", role.getRoleName());
            return ApiResult.log(role.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("删除角色")
    @RequiresPermissions("/sys/role/del")
    @Log(moduleType = ModuleType.ROLE, operType = OperType.OTHER)
    @PostMapping(value = "/delete")
    public ApiResult delete(@RequestBody @Valid IdReq req) {
        Role role = roleService.selectByPrimaryKey(req.getId());
        if (Objects.isNull(role)) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (SecurityUtils.missCompany(role.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }
        if (role.getDeleted()) {
            return ApiResult.ok();
        }

        // 预置角色不允许删除
        if (role.getRoleType() == RoleType.PRESET) {
            return ApiResult.error(SystemCode.PRESET_ROLE_CANNOT_DELETE);
        }

        roleService.delete(role);

        String log = String.format("删除角色，角色名称：“%s”", role.getRoleName());
        return ApiResult.log(log);
    }

}
