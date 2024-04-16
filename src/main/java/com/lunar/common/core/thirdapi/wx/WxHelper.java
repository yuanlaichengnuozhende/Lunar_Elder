package com.lunar.common.core.thirdapi.wx;

import cn.hutool.core.lang.Assert;
import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.thirdapi.wx.input.WxPhonenumberInput;
import com.lunar.common.core.thirdapi.wx.output.WxAccessToken;
import com.lunar.common.core.thirdapi.wx.output.WxPhonenumber;
import com.lunar.common.core.thirdapi.wx.output.WxSession;
import com.lunar.common.core.utils.RestUtil;
import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.thirdapi.wx.input.WxPhonenumberInput;
import com.lunar.common.core.thirdapi.wx.output.WxPhonenumber;
import com.lunar.common.core.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author szx
 * @date 2022/06/20 11:33
 */
@Slf4j
public class WxHelper {

    /** 换取session_key GET */
    private static final String JSCODE_2_SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /** 获取access_token GET */
    private static final String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /** 获取手机号 POST */
    private static final String GET_USER_PHONENUMBER = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    /**
     * 微信登录凭证校验
     *
     * @param appid
     * @param secret
     * @param jsCode
     * @return
     */
    public static WxSession jscode2Session(String appid, String secret, String jsCode) {
        Assert.notBlank(appid, "appid cannot be null");
        Assert.notBlank(secret, "secret cannot be null");
        Assert.notBlank(jsCode, "jsCode cannot be null");

        try {
            String url = String.format(JSCODE_2_SESSION, appid, secret, jsCode);

            String result = RestUtil.doGet(url);
            // String result = "{\"session_key\":\"nPxy4iwJLpqwf8OpD\\/hUsg==\",\"openid\":\"o8kwA5AghOIDm28S9M6zfT68ZaTg\",\"unionid\":\"odQcRw5LRvk43QOg7FNMxzxUVrSo\"}";
            log.info("微信返回={}", result);

            WxSession wxSession = JsonHelper.fromJson(result, WxSession.class);

            if (wxSession == null || StringUtils.isBlank(wxSession.getSessionKey())) {
                throw new ServiceException(ThirdCode.WX_AUTH_FAILED);
            }

            return wxSession;
        } catch (Exception e) {
            log.error("微信登录凭证校验失败", e);
            throw e;
        }
    }

    /**
     * 获取微信accessToken
     *
     * @param appid
     * @param secret
     * @return
     */
    public static WxAccessToken getAccessToken(String appid, String secret) {
        Assert.notBlank(appid, "appid cannot be null");
        Assert.notBlank(secret, "secret cannot be null");

        try {
            String url = String.format(GET_ACCESS_TOKEN, appid, secret);

            String result = RestUtil.doGet(url);
            // {"access_token":"ACCESS_TOKEN","expires_in":7200}
            log.info("微信返回={}", result);

            WxAccessToken wxAccessToken = JsonHelper.fromJson(result, WxAccessToken.class);

            if (wxAccessToken == null || StringUtils.isBlank(wxAccessToken.getAccessToken())) {
                throw new ServiceException(ThirdCode.WX_ACCESS_TOKEN_FAILED);
            }

            return wxAccessToken;
        } catch (Exception e) {
            log.error("获取微信accessToken失败", e);
            throw e;
        }
    }

    /**
     * 获取用户手机号
     *
     * @param accessToken
     * @param code
     * @return
     */
    public static String getUserPhonenumber(String accessToken, String code) {
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(code, "code cannot be null");

        try {
            String url = String.format(GET_USER_PHONENUMBER, accessToken);

            WxPhonenumberInput input = WxPhonenumberInput.builder().code(code).build();
            String reqJson = JsonHelper.toJson(input);

            String result = RestUtil.doPostByJson(url, reqJson);
            //
            log.info("微信返回={}", result);

            WxPhonenumber wxPhonenumber = JsonHelper.fromJson(result, WxPhonenumber.class);

            if (wxPhonenumber == null || wxPhonenumber.getErrcode() != 0 || wxPhonenumber.getPhoneInfo() == null) {
                throw new ServiceException(ThirdCode.WX_USER_PHONENUMBER_FAILED);
            }

            return wxPhonenumber.getPhoneInfo().getPurePhoneNumber();
        } catch (Exception e) {
            log.error("获取微信用户手机号失败", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        String appid = "wxca7a979b4022e2eb";
        String secret = "1870cd08f665df98c60c9f34734f2a25";

//        WxSession wxSession = jscode2Session(appid, secret, "x");
//        System.out.println(wxSession);

//        WxAccessToken accessToken = getAccessToken(appid, secret);
//        System.out.println(accessToken);

        String token = "58_THP0Te7bz_S7I28M3I22mdZ6RAdzzcj-VDbflf_LdAk5XM6yQVUpT8RIaWearSJwN3DpBb6oIyAEO14VsHX5bPlTi4Lh5APncnm8XXlHWE7IWxSjUqTFV-nEmVSxV2wRfHo7NtGDm09YADmTRJNaABAGYC";
        String mobile = getUserPhonenumber(token, "y");
        System.out.println(mobile);
    }

}
