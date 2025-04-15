package ru.bedward70.rest.util;

import java.util.concurrent.Callable;

public class RuntimeExceptionUtil {

    private RuntimeExceptionUtil() {
    }

    public static <T> T wrap(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
