package com.lunar.common.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * double参数校验
 *
 * @author szx
 * @date 2022/02/12 14:40
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DoubleRangeValidator.class)
@Documented
public @interface DoubleRange {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** 最小值开闭。false开，true闭 */
    boolean minClose() default false;

    /** 最大值开闭。false开，true闭 */
    boolean maxClose() default false;

    double min() default 0d;

    double max() default Double.MAX_VALUE;

}
