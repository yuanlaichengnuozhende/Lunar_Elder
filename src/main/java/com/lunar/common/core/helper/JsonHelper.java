package com.lunar.common.core.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author szx
 * @date 2022/02/23 14:42
 */
@Slf4j
public class JsonHelper {

    //映射器
    public static final ObjectMapper MAPPER = new ObjectMapper();

    //配置序列化参数
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        // 修改时间格式
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // null值不参与序列化。不设置通用方式，而是在属性上标记@JsonInclude(Include.NON_NULL)
//        MAPPER.setSerializationInclusion(Include.NON_NULL);

        //空值处理为空串
//        MAPPER.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
//                throws IOException {
//                //会把数字、对象都处理成空串，可能会造成类型错误，但本项目已够用，可以通过判断class做进一步处理
//                jsonGenerator.writeString("");
//            }
//        });

        // 声明自定义模块,配置double类型序列化配置
        SimpleModule module = new SimpleModule("DoubleSerializer",
                                               new Version(1, 0, 0, "", null, null));
        // Double和double需要分配配置
        module.addSerializer(Double.class, new DoubleSerializer());
        module.addSerializer(double.class, new DoubleSerializer());
        MAPPER.registerModule(module);

        // 驼峰转下划线
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    /**
     * double禁止使用科学计数法
     */
    static class DoubleSerializer extends JsonSerializer<Double> {

        @Override
        public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
            BigDecimal d = new BigDecimal(value.toString());
            // toPlainString可以原样输出数字，toString不一定
            gen.writeNumber(d.stripTrailingZeros().toPlainString());
        }

        @Override
        public Class<Double> handledType() {
            return Double.class;
        }
    }

    /**
     * 序列化成Json
     *
     * @param object 对象
     * @return Json
     */
    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }

        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.warn("write to json string error:" + object, e);
            return "";
        }
    }

    /**
     * 序列化成Json
     *
     * @param object   对象
     * @param jsonView JsonView
     * @return Json
     */
    public static String toJson(Object object, Class<?> jsonView) {
        if (object == null) {
            return "";
        }

        try {
            return MAPPER.writerWithView(jsonView).writeValueAsString(object);
        } catch (Exception e) {
            log.warn("write to json string error:" + object, e);
            return "";
        }
    }

    /**
     * 反序列化Json
     *
     * @param json  Json
     * @param clazz 类
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将map对象转换成json
     *
     * @param map
     * @return
     */
    public static String map2Json(Map map) {
        if (MapUtils.isEmpty(map)) {
            return "";
        }

        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            log.error("map2Json error", e);
        }
        return "";
    }

    public static String list2Json(List list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }

        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            log.error("list2Json error", e);
        }
        return "";
    }

    public static <T> T json2Bean(String json, Class<T> beanClass) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return MAPPER.readValue(json, beanClass);
        } catch (Exception e) {
            log.error("json2Bean error", e);
        }
        return null;
    }

    public static <T> List<T> json2List(String json, Class<T> beanClass) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return (List<T>) MAPPER.readValue(json, getCollectionType(List.class, beanClass));
        } catch (Exception e) {
            log.error("json2List error", e);
        }
        return null;
    }

    public static Map json2Map(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            return (Map) MAPPER.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("json2Map error", e);
        }
        return null;
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

}
