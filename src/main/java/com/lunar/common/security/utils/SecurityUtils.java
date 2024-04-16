package com.lunar.common.security.utils;

import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.consts.TokenConsts;
import com.lunar.common.core.context.SecurityContextHolder;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.core.utils.StringUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.consts.TokenConsts;
import com.lunar.common.core.context.SecurityContextHolder;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限获取工具类
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取登录用户信息
     */
    public static LoginUser getLoginUser() {
        return SecurityContextHolder.get(SecurityConsts.LOGIN_USER, LoginUser.class);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getUsername();
    }

    /**
     * 获取公司id
     */
    public static Long getCompanyId() {
        return SecurityContextHolder.getCompanyId();
    }

    /**
     * 获取公司名称
     */
    public static String getCompanyName() {
        LoginUser loginUser = getLoginUser();
        return Optional.ofNullable(loginUser).map(LoginUser::getCompanyName).orElse("");
    }

    /**
     * 获取登录代码
     */
    public static String getCompanyCode() {
        LoginUser loginUser = getLoginUser();
        return Optional.ofNullable(loginUser).map(LoginUser::getCompanyCode).orElse(Consts.DEFAULT_COMPANY_CODE);
    }

    /**
     * 缺失公司权限
     *
     * @param companyId
     * @return
     */
    public static boolean missCompany(Long companyId) {
        if (companyId == null) {
            return false;
        }

        return !companyId.equals(getCompanyId());
    }

//    /**
//     * 获取用户key
//     */
//    public static String getUserKey() {
//        return SecurityContextHolder.getUserKey();
//    }

    /**
     * 获取登录用户当前组织id
     */
    public static Long getOrgId() {
        return SecurityContextHolder.getOrgId();
    }

    /**
     * 获取登录用户当前组织名称
     */
    public static String getOrgName() {
        LoginUser loginUser = getLoginUser();
        return Optional.ofNullable(loginUser).map(LoginUser::getOrgName).orElse("");
    }

    /**
     * 获取用户根组织
     */
    public static OrgPojo getRootOrg() {
        LoginUser loginUser = getLoginUser();
        return Optional.ofNullable(loginUser).map(LoginUser::getRootOrg).orElse(null);
    }

    /**
     * 缺失orgId权限
     *
     * @param orgId
     * @return
     */
    public static boolean missOrg(Long orgId) {
        if (orgId == null) {
            return false;
        }
        List<Long> orgIdList = getOrgIdList();
        return !orgIdList.contains(orgId);
    }

    /**
     * 获取登录用户组织ids。,分割
     */
    public static String getOrgIds() {
        List<Long> orgIdList = getOrgIdList();
        return Joiner.on(",").join(orgIdList);
    }

    /**
     * 获取用户组织id列表
     */
    public static List<Long> getOrgIdList() {
        List<OrgPojo> orgList = getOrgList();
        return orgList.stream().map(OrgPojo::getId).collect(Collectors.toList());
    }

    /**
     * 获取用户组织列表
     */
    public static List<OrgPojo> getOrgList() {
        LoginUser loginUser = getLoginUser();
        return Optional.ofNullable(loginUser).map(LoginUser::getOrgList).orElse(Lists.newArrayList());
    }

    /**
     * 从orgList中过滤Org，可能返回null
     *
     * @param id
     * @return
     */
    public static OrgPojo getOrg(Long id) {
        return getOrgList().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * 从orgList中过滤orgName，不返回null
     *
     * @param id
     * @return
     */
    public static String getOrgName(Long id) {
        OrgPojo org = getOrg(id);
        return Optional.ofNullable(org).map(OrgPojo::getOrgName).orElse("");
    }

    /**
     * 获取orgMap（去重）
     */
    public static Map<Long, String> getOrgMap() {
        Map<Long, String> map = Maps.newHashMap();
        List<OrgPojo> orgList = getOrgList();
        if (CollectionUtils.isEmpty(orgList)) {
            return map;
        }

        try {
            Map<Long, OrgPojo> orgMap = ConvertUtil.list2Map(orgList, OrgPojo::getId);
            return orgMap.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getOrgName()));
        } catch (Exception e) {
            log.error("查询orgMap失败", e);
        }

        return map;
    }

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        // 从header获取token标识
        String token = request.getHeader(TokenConsts.AUTHENTICATION);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    private static String replaceTokenPrefix(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConsts.PREFIX)) {
            token = token.replaceFirst(TokenConsts.PREFIX, "");
        }
        return token;
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

}
