package com.lunar.common.core.convert.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * 手机号脱敏
 */
@Slf4j
public class MobileMosaicJsonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isNotEmpty(value)) {
            try {
                value = value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            } catch (Exception e) {
                log.error("手机号脱敏失败", e);
            }
        }
        gen.writeRawValue("\"" + value + "\"");
    }

}
