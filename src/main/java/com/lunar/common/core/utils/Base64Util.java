package com.lunar.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * base64工具
 *
 * @author szx
 * @date 2022/01/18 10:48
 */
@Slf4j
public class Base64Util {

    public static String fileToBase64(String filePath) {
        try (
            InputStream in = new FileInputStream(filePath);
        ) {
            // 读取文件字节数组
            byte[] data = new byte[in.available()];
            in.read(data);

            //对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            //返回Base64编码过的字节数组字符串
            return encoder.encode(data);
        } catch (Exception e) {
            log.error("文件转base64失败", e);
        }

        return "";
    }

    /**
     * 判断图片base64字符串的文件格式
     *
     * @param dataPrefix
     * @return
     */
    public static String getImageBase64Ext(String dataPrefix) {
        String ext = "";
        if ("data:image/jpeg;".equalsIgnoreCase(dataPrefix)) {
            //data:image/jpeg;base64,base64编码的jpeg图片数据
            ext = "jpg";
        } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrefix)) {
            //data:image/x-icon;base64,base64编码的icon图片数据
            ext = "ico";
        } else if ("data:image/gif;".equalsIgnoreCase(dataPrefix)) {
            //data:image/gif;base64,base64编码的gif图片数据
            ext = "gif";
        } else if ("data:image/png;".equalsIgnoreCase(dataPrefix)) {
            //data:image/png;base64,base64编码的png图片数据
            ext = "png";
        }

        return ext;
    }

}
