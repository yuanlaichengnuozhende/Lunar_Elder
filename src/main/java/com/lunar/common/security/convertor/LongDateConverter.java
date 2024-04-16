package com.lunar.common.security.convertor;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @author szx
 */
@Slf4j
public class LongDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        try {
            return new Date(Long.parseLong(s));
        } catch (Exception e) {
            log.info("整形(时间戳)日期转换错误,改用字符串转换. {}", s);
            try {
                return DateUtil.parse(s).toJdkDate();
            } catch (Exception ex) {
                log.error("日期转换错误. {}", s, ex);
                return null;
            }
        }
    }
}