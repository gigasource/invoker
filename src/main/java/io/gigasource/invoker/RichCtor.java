package io.gigasource.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RichCtor {
    public Class<?>[] paramsType;
    public Constructor ctor;

    public RichCtor(Constructor ct) {
        ct.setAccessible(true);
        ctor = ct;
        paramsType = ct.getParameterTypes();
    }

    public boolean isParamsMatch(Class<?>[] passingParams) {
        return TypeUtils.isMatch(paramsType, passingParams);
    }

    public Object create(Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return ctor.newInstance(args);
    }
}
