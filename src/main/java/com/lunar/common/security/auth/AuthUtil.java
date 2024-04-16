package com.lunar.common.security.auth;

import com.lunar.common.core.context.ApplicationContextUtil;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.exception.auth.NotPermissionException;
import com.lunar.common.core.exception.auth.NotRoleException;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.security.annotation.Logical;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.annotation.RequiresRoles;
import com.lunar.common.security.service.TokenService;
import com.lunar.common.security.utils.SecurityUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lunar.common.core.context.ApplicationContextUtil;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.security.annotation.Logical;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.annotation.RequiresRoles;
import com.lunar.common.security.service.TokenService;
import com.lunar.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.Set;

/**
 * Token 权限验证工具类
 */
@Slf4j
public class AuthUtil {

    /**
     * 所有权限标识 严格equals
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 管理员角色权限标识 严格equals
     */
    private static final String SUPER_ADMIN = "admin";

    public static TokenService tokenService;

    static {
        try {
            tokenService = ApplicationContextUtil.getBean(TokenService.class);
        } catch (Exception e) {
            log.error("tokenService error", e);
        }
    }

    /**
     * 会话注销
     */
    public static void logout() {
        String token = SecurityUtils.getToken();
        if (token == null) {
            return;
        }
        logoutByToken(token);
    }

    /**
     * 会话注销，根据指定Token
     *
     * @param token
     */
    public static void logoutByToken(String token) {
        tokenService.delLoginUser(token);
    }

    /**
     * 会话注销，根据公司和用户id
     *
     * @param companyId
     * @param userId
     */
    public static void logoutByUserId(Long companyId, Long userId) {
        tokenService.delLoginUserById(companyId, userId);
    }

    /**
     * 检验当前会话是否已经登录，如未登录，则抛出异常
     */
    public static void checkLogin() {
        getLoginUser();
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     */
    public static LoginUser getLoginUser() {
        String token = SecurityUtils.getToken();
        if (token == null) {
            throw new ServiceException("未提供token");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException("无效的token");
        }
        return loginUser;
    }

    /**
     * 获取当前登录用户信息
     */
    public static LoginUser getLoginUser(String token) {
        return tokenService.getLoginUser(token);
    }

    /**
     * 刷新令牌有效期，相差不足24小时，自动刷新缓存
     */
    public static void refreshTokenIfExpireShort(LoginUser loginUser) {
        tokenService.refreshTokenIfExpireShort(loginUser);
    }

    /**
     * 根据注解(@RequiresPermissions)鉴权, 如果验证未通过，则抛出异常: NotPermissionException
     *
     * @param requiresPermissions 注解对象
     */
    public static void checkPerms(RequiresPermissions requiresPermissions) {
        if (requiresPermissions.logical() == Logical.AND) {
            checkPermsAnd(requiresPermissions.value());
        } else {
            checkPermsOr(requiresPermissions.value());
        }
    }

    /**
     * 验证用户是否含有指定权限，必须全部拥有
     *
     * @param permissions 权限列表
     */
    public static void checkPermsAnd(String... permissions) {
        Set<String> permSet = getPermSet();
        for (String permission : permissions) {
            if (!hasPermission(permSet, permission)) {
                throw new NotPermissionException(permissions[0]);
            }
        }
    }

    /**
     * 验证用户是否含有指定权限，只需包含其中一个
     *
     * @param permissions 权限码数组（可能是空串数组）
     */
    public static void checkPermsOr(String... permissions) {
        Set<String> permSet = getPermSet();
        for (String permission : permissions) {
            if (hasPermission(permSet, permission)) {
                return;
            }
        }
        if (permissions.length > 0) {
            throw new NotPermissionException(permissions[0]);
        }
    }

    /**
     * 根据注解(@RequiresRoles)鉴权
     *
     * @param requiresRoles 注解对象
     */
    public static void checkRoles(RequiresRoles requiresRoles) {
        if (requiresRoles.logical() == Logical.AND) {
            checkRoleAnd(requiresRoles.value());
        } else {
            checkRoleOr(requiresRoles.value());
        }
    }

    /**
     * 验证用户是否含有指定角色，必须全部拥有
     *
     * @param roles 角色标识数组
     */
    public static void checkRoleAnd(String... roles) {
        Set<String> roleSet = getRoleSet();
        for (String role : roles) {
            if (!hasRole(roleSet, role)) {
                throw new NotRoleException(roles[0]);
            }
        }
    }

    /**
     * 验证用户是否含有指定角色，只需包含其中一个
     *
     * @param roles 角色标识数组
     */
    public static void checkRoleOr(String... roles) {
        Set<String> roleSet = getRoleSet();
        for (String role : roles) {
            if (hasRole(roleSet, role)) {
                return;
            }
        }
        if (roles.length > 0) {
            throw new NotRoleException(roles[0]);
        }
    }

    /**
     * 获取当前账号的权限列表
     *
     * @return 权限列表
     */
    private static Set<String> getPermSet() {
        try {
            LoginUser loginUser = getLoginUser();
            return loginUser.getPermissions();
        } catch (Exception e) {
            return Sets.newHashSet();
        }
    }

    /**
     * 获取当前账号的角色列表
     *
     * @return 角色列表
     */
    private static Set<String> getRoleSet() {
        try {
            LoginUser loginUser = getLoginUser();
            return loginUser.getRoles();
        } catch (Exception e) {
            return Sets.newHashSet();
        }
    }

    /**
     * 判断是否包含权限
     *
     * @param authorities 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermission(Collection<String> authorities, String permission) {
//        return authorities.stream().filter(StringUtils::hasText)
//            .anyMatch(x -> ALL_PERMISSION.contains(x) || PatternMatchUtils.simpleMatch(x, permission));
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        }
        return authorities.stream().filter(StringUtils::hasText)
            .anyMatch(x -> x.equals(ALL_PERMISSION) || x.equals(permission));
    }

    /**
     * 判断是否包含角色
     *
     * @param roles 角色列表
     * @param role  角色
     * @return 用户是否具备某角色权限
     */
    public static boolean hasRole(Collection<String> roles, String role) {
//        return roles.stream().filter(StringUtils::hasText)
//            .anyMatch(x -> SUPER_ADMIN.contains(x) || PatternMatchUtils.simpleMatch(x, role));
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        return roles.stream().filter(StringUtils::hasText)
            .anyMatch(x -> x.equals(SUPER_ADMIN) || x.equals(role));
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        Collection<String> roles = Lists.newArrayList("admi", "bbb");
        String role = "*";
        System.out.println(hasRole(roles, role));

        String pwd = "abcd123456";
        String encrypted = encryptPassword(pwd);
        System.out.println(encrypted);

        boolean match = matchesPassword(pwd, encrypted);
        System.out.println(match);

//        Collection<String> permissions = Lists.newArrayList("*:*:*", "bbb");
//        String permission = "hhhh";
//        System.out.println(hasPermission(permissions, permission));

//        System.out.println(PatternMatchUtils.simpleMatch("*", permission));
    }

}
