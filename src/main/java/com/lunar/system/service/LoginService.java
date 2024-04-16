package com.lunar.system.service;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.redis.utils.RedisUtil;
import com.lunar.common.security.auth.AuthUtil;
import com.lunar.system.entity.Company;
import com.lunar.system.entity.OperLog;
import com.lunar.system.entity.Org;
import com.lunar.system.enums.UserStatus;
import com.lunar.system.query.CompanyQuery;
import com.lunar.system.query.UserQuery;
import com.lunar.system.request.LoginReq;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.response.UserResp;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.EnableStatus;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.model.OrgPojo;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.response.UserResp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录校验方法
 */
@Slf4j
@AllArgsConstructor
@Component
public class LoginService {

    private final UserService userService;
    private final OperLogService operLogService;
    private final CompanyService companyService;
    private final OrgService orgService;
    private final PermissionService permissionService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final CaptchaService captchaService;

    /**
     * 登录
     *
     * @param req
     * @return
     */
    public LoginUser login(LoginReq req) {
        String companyCode = req.getCompanyCode();
        // 不传companyCode则使用默认值
        companyCode = StringUtils.isBlank(companyCode) ? Consts.DEFAULT_COMPANY_CODE : companyCode.trim();

        String username = req.getUsername().trim();
        String password = req.getPassword();

        // 登录错误锁定
        String loginErrorKey = String.format(CacheConsts.LOGIN_ERROR_KEY, companyCode, username);
        Integer loginCount = RedisUtil.get(loginErrorKey);
        // 60分钟内，超过5次失败，锁定账号
        if (Objects.nonNull(loginCount) && loginCount >= CacheConsts.LOGIN_ERROR_TIMES) {
            throw new ServiceException(SystemCode.LOGIN_ERROR_TOO_MANY);
        }
//        // 60分钟内，超过5次失败，需要校验图形验证码
//        if (Objects.nonNull(loginCount) && loginCount >= CacheConsts.LOGIN_ERROR_TIMES) {
//            captchaService.checkVerifyCode(req.getUuid(), req.getCode());
//        }

        LoginUser loginUser = null;
        try {
            loginUser = getLoginUser(companyCode, username);

            if (StringUtils.isEmpty(password)) {
                log.error("用户密码为空");
                throw new ServiceException(SystemCode.USER_NOT_FOUND);
            }
            // 校验密码
            if (!AuthUtil.matchesPassword(password, loginUser.getPassword())) {
                log.error("密码校验不通过");
                throw new ServiceException(SystemCode.USER_NOT_FOUND);
            }

            // 登录成功，删除错误次数缓存
            RedisUtil.del(loginErrorKey);

            log.info("用户登录成功");
            saveLog(loginUser);
        } catch (ServiceException e) {
            // 登录失败，缓存并增加登录次数
            if (Objects.isNull(loginCount)) {
                loginCount = 1;
                RedisUtil.set(loginErrorKey, 1, CacheConsts.LOGIN_ERROR_TIMEOUT, TimeUnit.MINUTES);
            } else {
                loginCount = (int) RedisUtil.incr(loginErrorKey);
            }

            throw new ServiceException(SystemCode.USER_NOT_FOUND.getCode(),
                                       SystemCode.USER_NOT_FOUND.getMessage(),
                                       String.valueOf(loginCount));
        }

        return loginUser;
    }

    /**
     * 登录
     *
     * @param companyCode
     * @param username
     * @return
     */
    public LoginUser login(String companyCode, String username) {
        // 不传companyCode则使用默认值
        companyCode = StringUtils.isBlank(companyCode) ? Consts.DEFAULT_COMPANY_CODE : companyCode.trim();

        LoginUser loginUser = getLoginUser(companyCode, username);

        log.info("用户登录成功");
        saveLog(loginUser);

        return loginUser;
    }

    /**
     * feign查询用户信息
     *
     * @param companyCode
     * @param username
     * @return
     */
    private LoginUser getLoginUser(String companyCode, String username) {
        log.info("用户登录. companyCode={}, username={}", companyCode, username);
//        ApiResult<LoginUser> apiResult = userFeign.login(companyCode, username, SecurityConsts.INNER);
        ApiResult<LoginUser> apiResult = loginByUsername(companyCode, username);
        if (apiResult == null) {
            throw new ServiceException(ApiCode.LOGIN_FAIL);
        }
        if (apiResult.isEmpty()) {
            throw new ServiceException(apiResult.getCode(), apiResult.getMsg());
        }
        return apiResult.getSuccessData();
    }

    /**
     * 登录
     */
    private ApiResult<LoginUser> loginByUsername(String companyCode, String username) {
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

    /**
     * 异步保存登录日志
     *
     * @param loginUser
     */
    private void saveLog(LoginUser loginUser) {
        String ipAddr = IpUtils.getRemoteIpAddr(ServletUtils.getRequest());

        threadPoolTaskExecutor.execute(() -> {
            OperLog operLog = OperLog.builder()
                .companyId(loginUser.getCompanyId())
                .operType(OperType.LOGIN)
                .moduleType(ModuleType.LOGIN)
                .username(loginUser.getUsername())
                .realName(loginUser.getRealName())
                .ipAddr(ipAddr)
                .createBy(loginUser.getUserId())
                .content("登录系统")
                .build();
            operLogService.insertSelective(operLog);
            log.info("登录日志记录完成. thread[{}]", Thread.currentThread().getName());
        });
    }

}