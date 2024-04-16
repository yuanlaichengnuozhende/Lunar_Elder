package com.lunar.common.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * @author szx
 * @date 2022/02/12 14:51
 */
public class DoubleRangeValidator implements ConstraintValidator<DoubleRange, Object> {

    private boolean minClose;
    private boolean maxClose;
    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void initialize(DoubleRange constraintAnnotation) {
        minClose = constraintAnnotation.minClose();
        maxClose = constraintAnnotation.maxClose();
        min = BigDecimal.valueOf(constraintAnnotation.min());
        max = BigDecimal.valueOf(constraintAnnotation.max());
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (null == o) {
            return true;
        }
        if (!(o instanceof Number)) {
            return false;
        }
        BigDecimal value = BigDecimal.valueOf(((Number) o).doubleValue());

        boolean greatThanMin = false;
        if (minClose) {
            greatThanMin = value.compareTo(min) >= 0;
        } else {
            greatThanMin = value.compareTo(min) > 0;
        }

        boolean lessThanMax = false;
        if (maxClose) {
            lessThanMax = value.compareTo(max) <= 0;
        } else {
            lessThanMax = value.compareTo(max) < 0;
        }

        return greatThanMin && lessThanMax;
    }
}
