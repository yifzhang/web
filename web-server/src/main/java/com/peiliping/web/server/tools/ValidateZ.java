package com.peiliping.web.server.tools;

import org.apache.commons.lang3.Validate;

public class ValidateZ extends Validate {

    public static void checkEachNotNull(Object... objects) {
        Validate.notEmpty(objects);
        for (Object o : objects) {
            Validate.notNull(o);
        }
    }

    public static void checkEachStringNotBlank(String... strs) {
        Validate.notEmpty(strs);
        for (String o : strs) {
            Validate.notBlank(o);
        }
    }

    public static void checkOneIsNotNull(Object... objects) {
        Validate.notEmpty(objects);
        for (Object o : objects) {
            if (o != null) {
                return;
            }
        }
        Validate.isTrue(false);
    }

    public static void checkEachExceedZero(Number... nums) {
        Validate.notEmpty(nums);
        for (Number n : nums) {
            Validate.notNull(n);
            Validate.isTrue(n.doubleValue() > 0);
        }
    }
}
