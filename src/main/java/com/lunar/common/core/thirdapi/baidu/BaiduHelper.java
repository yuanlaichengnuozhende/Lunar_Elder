package com.lunar.common.core.thirdapi.baidu;

import cn.hutool.core.lang.Assert;
import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.thirdapi.baidu.output.BaiduAccessToken;
import com.lunar.common.core.thirdapi.baidu.output.ImageClassify;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.core.utils.RestUtil;
import com.lunar.common.core.code.ThirdCode;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.utils.NumUtil;
import com.lunar.common.core.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author szx
 * @date 2022/06/20 11:33
 */
@Slf4j
public class BaiduHelper {

    /** 获取access_token GET */
    private static final String GET_ACCESS_TOKEN = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s";

    /** 图像识别 POST */
    private static final String POST_IMAGE_CLASSIFY = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token=%s";

    /**
     * 获取微信accessToken
     *
     * @param appid
     * @param secret
     * @return
     */
    public static BaiduAccessToken getAccessToken(String appid, String secret) {
        Assert.notBlank(appid, "appid cannot be null");
        Assert.notBlank(secret, "secret cannot be null");

        try {
            String url = String.format(GET_ACCESS_TOKEN, appid, secret);

            String result = RestUtil.doGet(url);
            // {"access_token":"ACCESS_TOKEN","expires_in":7200}
            log.info("百度返回={}", result);

            BaiduAccessToken accessToken = JsonHelper.fromJson(result, BaiduAccessToken.class);

            if (accessToken == null || StringUtils.isBlank(accessToken.getAccessToken())) {
                throw new ServiceException(ThirdCode.BAIDU_ACCESS_TOKEN_FAILED);
            }

            return accessToken;
        } catch (Exception e) {
            log.error("获取百度accessToken失败", e);
            throw e;
        }
    }

    /**
     * 图像识别
     *
     * @param accessToken
     * @param imgUrl
     * @return
     */
    public static ImageClassify postImageClassify(String accessToken, String imgUrl) {
        Assert.notBlank(accessToken, "accessToken cannot be null");
        Assert.notBlank(imgUrl, "imgUrl cannot be null");

        try {
            String url = String.format(POST_IMAGE_CLASSIFY, accessToken);

//            ImageClassifyInput input = ImageClassifyInput.builder().url(imgUrl).build();
//            String reqJson = JsonHelper.toJson(input);

            String reqJson = "url=" + imgUrl;

            String result = RestUtil.doPostByJson(url, reqJson);
            // {"result_num": 5,"result": [{"keyword": "车牌照","score": 0.936288,"root": "交通工具-汽车"},{"keyword": "车牌","score": 0.745724,"root": "交通工具-汽车"},{"keyword": "车后窗","score": 0.501488,"root": "交通工具-汽车"},{"keyword": "轿车","score": 0.289002,"root": "交通工具-汽车"},{"keyword": "汽车中网","score": 0.084144,"root": "交通工具-汽车"}],"log_id": 1544502277880663586}
            log.info("百度返回={}", result);

            ImageClassify output = JsonHelper.fromJson(result, ImageClassify.class);

            if (output == null) {
                throw new ServiceException(ThirdCode.BAIDU_IMAGE_CLASSIFY_FAILED);
            }

            if (!NumUtil.isNullOrZero(output.getErrcode())) {
                throw new ServiceException(output.getErrmsg());
            }

            if (CollectionUtils.isEmpty(output.getResult())) {
                throw new ServiceException(ThirdCode.BAIDU_IMAGE_CLASSIFY_EMPTY);
            }

            return output;
        } catch (Exception e) {
            log.error("百度图像识别失败", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        String appid = "ZTeg8YGjscDImGrtMI6xGE5W";
        String secret = "QHx9RZhjDbT6imdP4Y8h9cUZ6Iv8DMEf";

        BaiduAccessToken accessToken = getAccessToken(appid, secret);
        System.out.println(JsonHelper.toJson(accessToken));

        String token = accessToken.getAccessToken();
        ImageClassify output = postImageClassify(token,
            "https://oss.xxx.net/test/2022/06/16/vehicle_plate.jpeg");
        System.out.println(JsonHelper.toJson(output));
    }

}
