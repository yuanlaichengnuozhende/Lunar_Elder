package com.lunar.common.log.annotation;

import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;
import com.lunar.common.core.enums.ModuleType;
import com.lunar.common.core.enums.OperType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志记录注解
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 模块类型
     */
    ModuleType moduleType();

//    /**
//     * 模块标题（二级模块）
//     */
//    String title() default "";

    /**
     * 操作类型
     */
    OperType operType() default OperType.OTHER;

    /**
     * 只在有内容时才保存日志
     */
    boolean needContent() default true;

//    /**
//     * 是否保存请求的参数
//     */
//    boolean isSaveRequestData() default true;
//
//    /**
//     * 是否保存响应的参数
//     */
//    boolean isSaveResponseData() default true;

}
