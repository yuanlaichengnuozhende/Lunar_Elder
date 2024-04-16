package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CheckUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.meta.annotation.ControllerMeta;
import com.lunar.common.meta.enums.ControllerMetaType;
import com.lunar.common.redis.lock.LockUtil;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.auth.AuthUtil;
import com.lunar.common.security.service.TokenService;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Company;
import com.lunar.system.entity.Org;
import com.lunar.system.entity.Role;
import com.lunar.system.entity.User;
import com.lunar.system.enums.UserStatus;
import com.lunar.system.query.CompanyQuery;
import com.lunar.system.query.UserQuery;
import com.lunar.system.request.DefaultPasswordReq;
import com.lunar.system.request.PasswordReq;
import com.lunar.system.request.UserReq;
import com.lunar.system.request.UserStatusReq;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.response.UserDetailResp;
import com.lunar.system.response.UserResp;
import com.lunar.system.service.CompanyService;
import com.lunar.system.service.OrgService;
import com.lunar.system.service.PermissionService;
import com.lunar.system.service.UserService;
import com.google.common.base.Joiner;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.request.IdReq;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.core.utils.CheckUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@Api(tags = "用户管理")
@RequestMapping("/user")
public class UserController {

    @Value("${reset.password}")
    private String resetPassword;

    private final RedissonClient redissonClient;
    private final CompanyService companyService;
    private final UserService userService;
    private final OrgService orgService;
    private final PermissionService permissionService;
    private final TokenService tokenService;

    /**
     * 登录
     */
    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/login/{companyCode}/{username}")
    public ApiResult<LoginUser> login(@PathVariable("companyCode") String companyCode,
                                      @PathVariable("username") String username) {
        log.info("用户登录. companyCode={}, username={}", companyCode, username);

        Company company = companyService.selectOne(
            CompanyQuery.builder().companyCode(companyCode).companyStatus(EnableStatus.ON).build());
        if (company == null) {
            return ApiResult.error(SystemCode.COMPANY_CODE_NOT_FOUND);
        }

        IPage<UserResp> page = userService.findUserPage(
            UserQuery.builder().companyId(company.getId()).username(username).build(), 1, 1, null);
        if (CollectionUtils.isEmpty(page.getList())) {
            return ApiResult.error(SystemCode.USER_NOT_FOUND);
        }

        UserResp user = page.getList().get(0);

        if (user.getUserStatus() != UserStatus.ON) {
            return ApiResult.error(SystemCode.USER_STATUS_ERROR);
        }

        if (user.getDeleted()) {
            return ApiResult.error(SystemCode.USER_STATUS_ERROR);
        }

        // 用户角色id列表
        Set<String> roles = user.getRoleList()
            .stream()
            .map(x -> String.valueOf(x.getId()))
            .collect(Collectors.toSet());
        // 用户权限perms列表
        List<PermissionResp> permList = permissionService.findUserPermList(user.getId());
        Set<String> perms = permList.stream().map(PermissionResp::getPerms).collect(Collectors.toSet());

        LoginUser loginUser = LoginUser.builder()
            .companyId(company.getId())
            .companyCode(company.getCompanyCode())
            .companyName(company.getCompanyName())
            .userId(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .mobile(user.getMobile())
            .roles(roles)
            .permissions(perms)
            .password(user.getPassword())
            .defaultPassword(user.getDefaultPassword())
            .build();

        if (CollectionUtils.isNotEmpty(user.getOrgList())) {
            // 用户所属组织（单个）
            Org org = user.getOrgList().get(0);
            loginUser.setOrgId(org.getId());
            loginUser.setOrgName(org.getOrgName());

            // 查询所有下级组织
            List<Org> allOrg = orgService.findAllChildren(company.getId(), org.getId());
            List<OrgPojo> orgList = CopyUtil.copyList(allOrg, OrgPojo.class);
            loginUser.setOrgList(orgList);

            // 根组织id
            List<Org> orgHierarchy = orgService.findOrgHierarchy(company.getId(), org.getId());
            loginUser.setRootOrg(CopyUtil.copyObject(orgHierarchy.get(0), OrgPojo.class));
        }

        // 更新用户登录时间
        userService.updateLastLoginTime(user.getId(), new Date());

        return ApiResult.ok(loginUser);
    }

    @InnerAuth
    @ApiIgnore
    @GetMapping("/inner/getByIds")
    public ApiResult<List<User>> innerIetByIds(@RequestParam List<Long> ids) {
        log.info("根据用户ids查询用户. ids={}", ids);
        if (CollectionUtils.isEmpty(ids)) {
            return ApiResult.ok();
        }

        List<User> list = userService.findList(UserQuery.builder().idList(ids).build());
        return ApiResult.ok(list);
    }

    @ApiOperation("查询多个用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "ids", value = "ids", required = false, dataType = "ids", paramType = "query"),
    })
    @GetMapping("/getByIds")
    public ApiResult<List<User>> getByIds(@RequestParam List<Long> ids) {
        log.info("根据用户ids查询用户. ids={}", ids);
        if (CollectionUtils.isEmpty(ids)) {
            return ApiResult.ok();
        }

        List<User> list = userService.findList(UserQuery.builder().idList(ids).build());
        return ApiResult.ok(list);
    }

    @ApiOperation("用户列表-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "likeUserInfo", value = "用户信息", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orgId", value = "组织id", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "roleId", value = "角色id", required = false, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "userStatus", value = "用户状态。0 启用 1 禁用", required = false, dataType = "int", paramType = "query")
    })
    @ControllerMeta({ControllerMetaType.UPDATE_BY})
    @RequiresPermissions("/orgs/user")
    @GetMapping("/page")
    public ApiResult<IPage<UserResp>> page(@Validated PageParam pageReq,
                                           String likeUserInfo,
                                           UserStatus userStatus,
                                           Long orgId,
                                           Long roleId) {
        UserQuery query = UserQuery.builder()
            .companyId(SecurityUtils.getCompanyId())
            //先不做数据权限
//            .orgIds(SecurityUtils.getOrgIdList())
            .likeUserInfo(likeUserInfo)
            .userStatus(userStatus)
            .orgId(orgId)
            .roleId(roleId)
            .deleted(false)
            .build();
        IPage<UserResp> page = userService.findUserPage(query, pageReq.getPageNum(), pageReq.getPageSize(),
                                                        "create_time desc, id desc");

//        Map<Long, String> orgMap = orgService.allHierarchyTree("/");

        page.getList().forEach(x -> {
            x.setRoleNames(getRoleNames(x.getRoleList()));
//            x.setOrgNames(getOrgNames(x.getOrgList(), orgMap));
            x.setOrgNames(getOrgNames(x.getOrgList()));
        });

        return ApiResult.ok(page);
    }

    @ApiOperation("用户详情")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "int", paramType = "query")
    @RequiresPermissions("/sys/user/detail")
    @GetMapping("/{id}")
    public ApiResult<UserDetailResp> detail(@PathVariable Long id) {
//        PageResult<UserResp> page = userService.findUserPage(
//            UserQuery.builder().id(id).orgIds(SecurityUtils.getOrgIdList()).build(),
//            1, 1, null);
        //不做数据权限
        IPage<UserResp> page = userService.findUserPage(
            UserQuery.builder().id(id).companyId(SecurityUtils.getCompanyId()).build(),
            1, 1, null);
        if (CollectionUtils.isEmpty(page.getList())) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        UserResp user = page.getList().get(0);

        UserDetailResp resp = CopyUtil.copyObject(user, UserDetailResp.class);
        resp.setRoleNames(getRoleNames(user.getRoleList()));
        resp.setOrgNames(getOrgNames(user.getOrgList()));

        return ApiResult.ok(resp);
    }

    @ApiOperation("新增用户")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @RequiresPermissions("/sys/user/add")
    @PostMapping("/add")
    public ApiResult add(@RequestBody @Valid UserReq req) {
        String label = "sys:user:add:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            User user = userService.add(req);

            String log = String.format("新增用户，用户名：“%s”" + "，姓名：“%s”", user.getUsername(), user.getRealName());
            return ApiResult.log(user.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("编辑用户")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @RequiresPermissions("/sys/user/edit")
    @PostMapping("/edit")
    public ApiResult edit(@RequestBody @Valid UserReq req) {
        if (NumUtil.isNullOrZero(req.getId())) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }

        String label = "sys:user:edit:" + SecurityUtils.getUserId();
        ApiResult apiResult = LockUtil.lock(redissonClient, label, () -> {
            User user = userService.edit(req);

            String log = String.format("编辑用户，用户名：“%s”" + "，姓名：“%s”", user.getUsername(), user.getRealName());
            return ApiResult.log(user.getId(), log);
        });
        return apiResult;
    }

    @ApiOperation("删除用户")
    @RequiresPermissions("/sys/user/del")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @PostMapping(value = "/delete")
    public ApiResult delete(@RequestBody @Valid IdReq req) {
        User user = userService.selectByPrimaryKey(req.getId());
        if (ObjectUtils.isEmpty(user)) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (SecurityUtils.missCompany(user.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }
        if (user.getDeleted()) {
            return ApiResult.ok();
        }

        userService.delete(user);

        String log = String.format("删除用户，用户名：“%s”" + "，姓名：“%s”", user.getUsername(), user.getRealName());
        return ApiResult.log(log);
    }

    @ApiOperation("启用禁用用户")
    @RequiresPermissions("/sys/user/status")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @PostMapping(value = "/status")
    public ApiResult status(@RequestBody @Valid UserStatusReq req) {
        User user = userService.selectByPrimaryKey(req.getId());
        if (user == null) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }
        if (SecurityUtils.missCompany(user.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }
        if (user.getUserStatus() == req.getUserStatus()) {
            return ApiResult.ok();
        }
        if (user.getAdminFlag()) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }

        user.setUserStatus(req.getUserStatus());
        // 禁用
        if (req.getUserStatus() == UserStatus.OFF) {
            // 踢出用户
            AuthUtil.logoutByUserId(user.getCompanyId(), user.getId());
            log.info("删除用户token成功");
        }
        user.setUpdateBy(SecurityUtils.getUserId());
        userService.updateSelective(user);

        String content = String.format("%s用户，用户名：“%s”" + "，姓名：“%s”",
                                       req.getUserStatus().getName(), user.getUsername(), user.getRealName());

        return ApiResult.log(content);
    }

//    @ApiOperation("用户角色配置")
//    @RequiresPermissions("/sys/user/configRole")
//    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
//    @PostMapping(value = "/edit/role")
//    public ApiResult editRole(@RequestBody @Valid UserRoleReq req) {
//        User user = userService.selectOne(
//            UserQuery.builder()
//                .id(req.getId())
//                .companyId(SecurityUtils.getCompanyId())
//                .orgIds(SecurityUtils.getOrgIdList())
//                .userStatus(UserStatus.ON)
//                .build());
//        if (user == null) {
//            return ApiResult.error(ApiCode.DATA_ERROR);
//        }
//
//        userService.editRole(user.getId(), req.getIdList());
//
//        String log = String.format("对用户：“%s”，“%s”进行角色权限配置", user.getRealName(), user.getUsername());
//        return ApiResult.log(log);
//    }
//
//    @ApiOperation("用户组织配置")
//    @RequiresPermissions("/sys/user/configOrg")
//    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
//    @PostMapping(value = "/edit/org")
//    public ApiResult editOrg(@RequestBody @Valid UserOrgReq req) {
//        User user = userService.selectOne(
//            UserQuery.builder()
//                .id(req.getId())
//                .companyId(SecurityUtils.getCompanyId())
//                .orgIds(SecurityUtils.getOrgIdList())
//                .userStatus(UserStatus.ON)
//                .build());
//        if (user == null) {
//            return ApiResult.error(ApiCode.DATA_ERROR);
//        }
//
//        userService.editOrg(user.getId(), req);
//
//        String log = String.format("对用户：“%s”，“%s”进行组织权限配置", user.getRealName(), user.getUsername());
//        return ApiResult.log(log);
//    }

    @ApiOperation("用户修改密码")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @PostMapping(value = "/password/modify")
    public ApiResult modifyPassword(@RequestBody @Valid PasswordReq req) {
        Long userId = SecurityUtils.getUserId();
        if (NumUtil.isNullOrZero(userId)) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        // 新旧密码不能相同
        if (StringUtils.equals(req.getOldPassword(), req.getNewPassword())) {
            return ApiResult.error(SystemCode.SAME_PASSWORD);
        }

        // 用户名密码不能相同
        if (StringUtils.equals(req.getNewPassword(), SecurityUtils.getUsername())) {
            return ApiResult.error(SystemCode.SAME_PASSWORD_USERNAME);
        }

        // 校验旧密码
        User user = userService.selectByPrimaryKey(userId);
        if (SecurityUtils.missCompany(user.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }

        // 校验旧密码
        if (!AuthUtil.matchesPassword(req.getOldPassword(), user.getPassword())) {
            return ApiResult.error(SystemCode.OLD_PASSWORD_ERROR);
        }

        // 校验新密码格式
        if (!CheckUtil.checkPassword(req.getNewPassword())) {
            return ApiResult.error(SystemCode.NEW_PASSWORD_ERROR);
        }

        user.setPassword(AuthUtil.encryptPassword(req.getNewPassword()));
        user.setDefaultPassword(false);
        user.setUpdateBy(userId);
        userService.updateSelective(user);
        log.info("用户修改密码完成");

        // 刷新token
        LoginUser loginUser = SecurityUtils.getLoginUser();
        loginUser.setDefaultPassword(false);
        tokenService.refreshToken(SecurityUtils.getLoginUser());

        return ApiResult.log("用户修改密码");
    }

    @ApiOperation("管理员重置密码")
    @RequiresPermissions("/sys/user/resetPassword")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @PostMapping(value = "/password/reset")
    public ApiResult resetPassword(@RequestBody @Valid IdReq req) {
        User user = userService.selectByPrimaryKey(req.getId());
        if (SecurityUtils.missCompany(user.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }

        user.setPassword(AuthUtil.encryptPassword(resetPassword));
        user.setUpdateBy(SecurityUtils.getUserId());
        user.setDefaultPassword(true);
        userService.updateSelective(user);
        log.info("管理员重置密码完成");

        // 踢出用户
        AuthUtil.logoutByUserId(user.getCompanyId(), user.getId());
        log.info("删除用户token成功");

        String log = String.format("重置密码，用户名：“%s”" + "，姓名：“%s”", user.getUsername(), user.getRealName());
        return ApiResult.log(log);
    }

    @ApiOperation("修改默认密码")
    @Log(moduleType = ModuleType.USER, operType = OperType.OTHER)
    @PostMapping(value = "/password/modifyDefault")
    public ApiResult modifyDefaultPassword(@RequestBody @Valid DefaultPasswordReq req) {
        Long userId = SecurityUtils.getUserId();
        if (NumUtil.isNullOrZero(userId)) {
            return ApiResult.error(ApiCode.DATA_ERROR);
        }

        User user = userService.selectByPrimaryKey(userId);
        if (SecurityUtils.missCompany(user.getCompanyId())) {
            return ApiResult.error(ApiCode.DATA_PERMISSION_ERROR);
        }

        // 新旧密码不能相同
        if (AuthUtil.matchesPassword(req.getNewPassword(), user.getPassword())) {
            return ApiResult.error(SystemCode.SAME_PASSWORD);
        }

        // 校验新密码格式
        if (!CheckUtil.checkPassword(req.getNewPassword())) {
            return ApiResult.error(SystemCode.NEW_PASSWORD_ERROR);
        }

        user.setPassword(AuthUtil.encryptPassword(req.getNewPassword()));
        user.setDefaultPassword(false);
        user.setUpdateBy(userId);
        userService.updateSelective(user);
        log.info("用户修改默认密码完成");

        // 刷新token
        LoginUser loginUser = SecurityUtils.getLoginUser();
        loginUser.setDefaultPassword(false);
        tokenService.refreshToken(SecurityUtils.getLoginUser());

        return ApiResult.log("用户修改默认密码");
    }

    /**
     * 角色
     *
     * @param list
     * @return
     */
    private String getRoleNames(List<Role> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        List<String> roleNames = list.stream().map(Role::getRoleName).collect(Collectors.toList());
        return Joiner.on("；").join(roleNames);
    }

    /**
     * 组织
     *
     * @param list
     * @return
     */
    private String getOrgNames(List<Org> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        List<String> orgNameList = list.stream().map(Org::getOrgName).collect(Collectors.toList());
        return Joiner.on("；").join(orgNameList);
    }

}
