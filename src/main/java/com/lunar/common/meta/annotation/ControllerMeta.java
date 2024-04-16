package com.lunar.common.meta.annotation;

import com.lunar.common.meta.enums.ControllerMetaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller元数据
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerMeta {

    /**
     * 需要填充的数据
     */
    ControllerMetaType[] value() default {};

}