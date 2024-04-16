package com.lunar.common.core.thirdapi.wx;

import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.thirdapi.wx.output.WxStep;
import com.lunar.common.core.thirdapi.wx.output.WxUserInfo;
import com.google.common.collect.Maps;
import com.lunar.common.core.helper.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author szx
 * @date 2019/07/19 18:54
 */
@Slf4j
public class WxUtil {

    /**
     * 解密微信用户数据
     *
     * @param encryptedData
     * @param iv
     * @param sessionKey
     * @return
     */
    public static WxUserInfo decodeWxUserData(String encryptedData, String iv, String sessionKey) throws Exception {
        log.info("解密微信用户数据");
        String data = decodeWxData(encryptedData, iv, sessionKey);
        log.info("解密数据={}", data);

        return JsonHelper.fromJson(data, WxUserInfo.class);
    }

    /**
     * 解密微信用户步数
     *
     * @param encryptedData
     * @param iv
     * @param sessionKey
     * @return
     */
    public static WxStep decodeWxStep(String encryptedData, String iv, String sessionKey) throws Exception {
        log.info("解密微信用户步数");
        String data = decodeWxData(encryptedData, iv, sessionKey);
        log.info("解密数据={}", data);

        return JsonHelper.fromJson(data, WxStep.class);
    }

    /**
     * 解密参数
     *
     * @param encryptedData 加密数据
     * @param iv            偏移量
     * @param sessionKey    session_key
     * @return
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidParameterSpecException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decodeWxData(String encryptedData, String iv, String sessionKey)
        throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
        NoSuchProviderException, NoSuchPaddingException, InvalidParameterSpecException, IllegalBlockSizeException
        , BadPaddingException {
        log.info("解密微信数据");
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
        // 如果密钥不足16位，那么就补足
        int base = 16;
        if (keyByte.length % base != 0) {
            int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
            keyByte = temp;
        } // 初始化
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        //Cipher cipher = Cipher.getInstance("AES/CBC/NOPadding");
        SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
        parameters.init(new IvParameterSpec(ivByte));
        cipher.init(Cipher.DECRYPT_MODE, spec, parameters); // 初始化
        byte[] resultByte = cipher.doFinal(dataByte);
        String result = "";
        if (null != resultByte && resultByte.length > 0) {
            result = new String(resultByte, StandardCharsets.UTF_8);
        }
        return result;
    }

    /**
     * 拼接微信模板消息
     *
     * @param openId
     * @param templateId
     * @param url
     * @param first
     * @param keyword1
     * @param keyword2
     * @param keyword3
     * @param remark
     * @return
     */
    public static String getTemplate(String openId,
                                     String templateId,
                                     String url,
                                     String first,
                                     String keyword1,
                                     String keyword2,
                                     String keyword3,
                                     String remark) {
        log.info(
            "拼接模板消息. openId={}, templateId={}, url={}, first={}, keyword1={}, keyword2={}, keyword3={}, remark={}",
            openId, templateId, url, first, keyword1, keyword2, keyword3, remark);
        Map<String, Object> templateNews = Maps.newHashMap();
        templateNews.put("touser", openId);
        templateNews.put("template_id", templateId);
        templateNews.put("url", url);
        Map<String, Object> data = Maps.newHashMap();
        Map<String, String> firstMap = Maps.newHashMap();
        firstMap.put("value", first);
        firstMap.put("color", "#173177");
        data.put("first", firstMap);

        Map<String, String> keyword1Map = Maps.newHashMap();
        keyword1Map.put("value", keyword1);
        keyword1Map.put("color", "#173177");
        data.put("keyword1", keyword1Map);

        Map<String, String> keyword2Map = Maps.newHashMap();
        keyword2Map.put("value", keyword2);
        keyword2Map.put("color", "#173177");
        data.put("keyword2", keyword2Map);

        Map<String, String> keyword3Map = Maps.newHashMap();
        keyword3Map.put("value", keyword3);
        keyword3Map.put("color", "#173177");
        data.put("keyword3", keyword3Map);

        Map<String, String> remarkMap = Maps.newHashMap();
        remarkMap.put("value", remark);
        remarkMap.put("color", "#173177");
        data.put("remark", remarkMap);
        templateNews.put("data", data);

//        String message = JSONObject.toJSONString(templateNews);
        String message = JsonHelper.toJson(templateNews);
        return message;
    }

    public static void main(String[] args) {
        try {
            String encryptedData = "sDUm+J6+doKQSXVfe3PcNhtFlXxhFFJuBReI9gTBakI58L6Vm9exa2/De9T5jgqNxs767tKek" +
                "+B8sYPjjHkE6i1ZstohJXQ5a6qxcnEZ5vzi2U6FNTSvuoVS2zhzXpifVVTr58vag897sA" +
                "/izCyNhTuTcJPdfRE3KjThbSoZI8O2ZjO/V+Yk8ZtBuKtLHpeXtnw7HPjcT9eT8HlaKSpKpw==";
            String iv = "JkubfYjVifYoJt0buzPhbA==";
            String sessionKey = "+yq4+8KxZPFllwp2XK0yMg==";
            String str = decodeWxData(encryptedData, iv, sessionKey);
            System.out.println(str);
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException", e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error("InvalidAlgorithmParameterException", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
        } catch (NoSuchProviderException e) {
            log.error("NoSuchProviderException", e);
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException", e);
        } catch (InvalidParameterSpecException e) {
            log.error("InvalidParameterSpecException", e);
        } catch (IllegalBlockSizeException e) {
            log.error("IllegalBlockSizeException", e);
        } catch (BadPaddingException e) {
            log.error("BadPaddingException", e);
        }
    }
}
