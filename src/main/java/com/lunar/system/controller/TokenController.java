package com.lunar.system.controller;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.enums.EnvType;
import com.lunar.common.core.enums.LangType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.AESUtil;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.redis.utils.I18nUtil;
import com.lunar.common.redis.utils.RedisUtil;
import com.lunar.common.security.annotation.AllowNoLogin;
import com.lunar.common.security.auth.AuthUtil;
import com.lunar.common.security.model.Token;
import com.lunar.common.security.service.TokenService;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.request.LoginReq;
import com.lunar.system.request.MobileReq;
import com.lunar.system.response.CaptchaResp;
import com.lunar.system.response.TokenResp;
import com.lunar.system.service.CaptchaService;
import com.lunar.system.service.LoginService;
import com.lunar.system.service.VerifyService;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.enums.EnvType;
import com.lunar.common.core.enums.LangType;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.AESUtil;
import com.lunar.common.core.utils.IpUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth/token")
public class TokenController {

    /** 5分钟内，同一个ip最多允许10次 */
    private static final int IP_MAX_MIN = 5;

    /** 5分钟内，同一个ip最多允许10次 */
    private static final int IP_MAX_TIMES = 10;

    @Value("${spring.profiles.active:prod}")
    private String env;

    @Value("${aes.key}")
    private String aesKey;

    @Value("${aes.iv}")
    private String iv;

    private final TokenService tokenService;
    private final LoginService loginService;
    private final VerifyService verifyService;
    private final CaptchaService captchaService;

    @AllowNoLogin
    @ApiOperation("test-login登录测试")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "companyCode", value = "登录代码", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "lang", value = "语言", required = false, dataType = "string", paramType = "query", defaultValue = "zh")
    })
    @GetMapping(value = "/logintest")
    public ApiResult<TokenResp> logintest(String companyCode, String username, String lang) {
        if (EnvType.PROD.getActive().equals(env)) {
            return ApiResult.ok();
        }

        LoginUser loginUser = loginService.login(companyCode, username);

        TokenResp resp = loginSuccess(loginUser, lang);
        return ApiResult.ok(resp);
    }

    /**
     * aes加密登录. eg: twdVTzLrTTbCQaMvkIkoIoUwG5pYOxlTuqsEPp3oD9Vgz9s1D1SVFEItC13RXyc6XghMqZ/P/C9+3cDuu3yQ8A==
     */
    @AllowNoLogin
    @ApiOperation("账号密码登录")
    @PostMapping(value = "/account/login")
    public ApiResult<TokenResp> accountLogin(@RequestBody LoginReq req) {
        log.info("账号密码登录. req={}", req);
        String decrypt;
        try {
            decrypt = AESUtil.decryptPKCS5Padding(aesKey, req.getRequestStr(), iv);
        } catch (Exception e) {
            throw new ServiceException(ApiCode.DECRYPT_ERROR);
        }

        LoginReq loginReq = JsonHelper.fromJson(decrypt, LoginReq.class);
        if (loginReq == null) {
            return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR);
        }
        if (StringUtils.isBlank(loginReq.getUsername())) {
            return ApiResult.error(SystemCode.MISSING_USERNAME);
        }
        if (StringUtils.isBlank(loginReq.getPassword())) {
            return ApiResult.error(SystemCode.MISSING_PASSWORD);
        }

        LoginUser loginUser = loginService.login(loginReq);

        TokenResp resp = loginSuccess(loginUser, loginReq.getLang());
        return ApiResult.ok(resp);
    }

    @AllowNoLogin
    @ApiOperation("生成图形验证码")
    @GetMapping("/captcha")
    public ApiResult<CaptchaResp> captcha(HttpServletRequest request) {
//        String ip = IpUtils.getIpAddr(request);
//        if (checkIp(ip)) {
//            return ApiResult.error(ApiCode.TOO_MANY_REQUESTS);
//        }

        CaptchaResp resp = captchaService.generate();
        return ApiResult.ok(resp);
    }

    @AllowNoLogin
    @ApiOperation("发送验证码")
    @ApiImplicitParam(name = "mobile", value = "mobile", required = true, dataType = "string", paramType = "query")
    @GetMapping("/mobile/vfcode")
    public ApiResult verifyCode(@NotBlank(message = "手机号不能为空") String mobile,
                                HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);

        if (checkIp(ip)) {
            return ApiResult.error(ApiCode.TOO_MANY_REQUESTS);
        }

        verifyService.generateAndSendVerifyCode(mobile.trim());
        return ApiResult.ok();
    }

    @AllowNoLogin
    @ApiOperation("验证码登录")
    @PostMapping(value = "/vfcode/login")
    public ApiResult<TokenResp> vfcodeLogin(@RequestBody @Valid MobileReq req) {
        // 校验验证码
        verifyService.checkVerifyCode(req.getMobile().trim(), req.getVfcode().trim());

        // 校验通过，免密登录
        LoginUser loginUser = loginService.login(req.getCompanyCode(), req.getMobile());

        TokenResp resp = loginSuccess(loginUser, req.getLang());
        return ApiResult.ok(resp);
    }

//    /**
//     * 钉钉登录：获取用户token
//     * <p>
//     * 接口地址：注意/auth与钉钉登录与分享的回调域名地址一致
//     */
//    @ApiOperation("钉钉登录")
//    @GetMapping(value = "/ding/login")
//    public ApiResult<TokenResp> dingLogin(@NotBlank(message = "authCode不能为空") String authCode,
//                                          @NotBlank(message = "lang不能为空") String lang) {
//        DingAccessToken callbackToken =
//            DingHelper.authCodeCallback(apiProp.getDingAppid(), apiProp.getDingAppsecret(), authCode);
//
//        DingUserCallBack userCallBack = DingHelper.getUserinfoByToken(callbackToken.getAccessToken());
//        if (userCallBack == null) {
//            return ApiResult.error("钉钉登录失败");
//        }
//
//        // 登录的钉钉userid
//        String username = DingHelper.getByUnionid(getAccessToken(), userCallBack.getUnionId());
//
//        // 使用username登录
//        LoginUser loginUser = loginService.login(Consts.DEFAULT_COMPANY_CODE, username);
//
//        TokenResp resp = loginSuccess(loginUser, lang);
//        return ApiResult.ok(resp);
//    }

    @ApiOperation("登出")
    @GetMapping("/logout")
    public ApiResult<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isBlank(token)) {
            return ApiResult.ok();
        }

        // 删除用户缓存记录
        AuthUtil.logoutByToken(token);

        return ApiResult.ok();
    }

    @ApiOperation("刷新token")
    @GetMapping("/refresh")
    public ApiResult<TokenResp> refresh() {
        String username = SecurityUtils.getUsername();
        // 重新登录
        LoginUser loginUser = loginService.login(SecurityUtils.getCompanyCode(), username);
        TokenResp resp = createJwt(loginUser);

        String lang = I18nUtil.getUserLang(SecurityUtils.getUserId());
        resp.setLang(lang);
        return ApiResult.ok(resp);
    }

    /**
     * 登录成功
     *
     * @param loginUser
     * @param lang
     * @return
     */
    private TokenResp loginSuccess(LoginUser loginUser, String lang) {
        TokenResp resp = createJwt(loginUser);

        // 保存用户语言
        String langName = LangType.getByNameDefaultIfNull(lang).getName();
        I18nUtil.setUserLang(langName, loginUser.getUserId());
        resp.setLang(langName);

        return resp;
    }

    /**
     * 生成jwt
     *
     * @param loginUser
     * @return
     */
    private TokenResp createJwt(LoginUser loginUser) {
        Token token = tokenService.createToken(loginUser);

        // 接口返回信息
        return TokenResp.builder()
            .companyId(loginUser.getCompanyId())
            .companyName(loginUser.getCompanyName())
            .orgId(loginUser.getOrgId())
            .orgName(loginUser.getOrgName())
            .userId(loginUser.getUserId())
            .username(loginUser.getUsername())
            .realName(loginUser.getRealName())
            .roles(loginUser.getRoles())
            .permissions(loginUser.getPermissions())
            .accessToken(token.getAccessToken())
            .expiresIn(token.getExpiresIn())
            .defaultPassword(loginUser.getDefaultPassword())
            .build();
    }

    /**
     * 5分钟内，同一个ip最多允许10次
     *
     * @param ip
     * @return
     */
    private boolean checkIp(String ip) {
        String key = String.format(CacheConsts.REDIS_VFCODE_IP, ip);
        Object o = RedisUtil.get(key);
        if (Objects.nonNull(o)) {
            int num = Integer.parseInt(o.toString());
            if (num >= IP_MAX_TIMES) {
                return true;
            }
            RedisUtil.incr(key, 1);
        } else {
            RedisUtil.set(key, 1, IP_MAX_MIN, TimeUnit.MINUTES);
        }
        return false;
    }

}
