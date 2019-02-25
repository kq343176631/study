package com.style.common.validator;

import com.style.common.exception.ValidateException;
import com.style.utils.core.SpringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 数据校验工具
 */
public class ValidatorUtils {

    private static Validator validator = (Validator) SpringUtils.getBean("validator");

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     */
    public static void validateEntity(Object object, Class<?>... groups) throws ValidateException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            throw new ValidateException(constraintViolations.iterator().next().getMessage());
        }
    }
}
