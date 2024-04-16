package com.lunar.common.security.resolver;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.ContentType;
import com.lunar.common.core.helper.JsonHelper;
import com.lunar.common.core.helper.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class JsonWriter {

    public static void write(HttpServletResponse response, Object content) {
        response.setContentType(ContentType.JSON + ";charset=" + CharsetUtil.UTF_8);
        if (content != null) {
            doWrite(response, JsonHelper.toJson(content));
        }
    }

    public void write(HttpServletResponse response, Object content, String contentType) {
        if (content == null) {
            return;
        }

        if (contentType == null) {
            response.setContentType(ContentType.JSON + ";charset=" + CharsetUtil.UTF_8);
        } else {
            response.setContentType(contentType);
        }
        doWrite(response, content);
    }

    private static void doWrite(HttpServletResponse response, Object content) {
        // [ON-DEMAND] 数据脱敏
//        String jsonStr = JSON.toJSONString(content, new ValueDesensitizeFilter(),
//            SerializerFeature.DisableCircularReferenceDetect,
//            SerializerFeature.QuoteFieldNames, SerializerFeature.WriteMapNullValue,
//            SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero,
//            SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteDateUseDateFormat);
//        doWrite(response, jsonStr);
        doWrite(response, (String) content);
    }

    private static void doWrite(HttpServletResponse response, String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }

        try {
            PrintWriter writer = response.getWriter();
            writer.print(content);
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
