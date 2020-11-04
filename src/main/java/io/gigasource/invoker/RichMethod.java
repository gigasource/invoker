package io.gigasource.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class RichMethod {
    public Class<?>[] _paramTypes;
    public boolean _isStatic;
    public Method _m;


    public RichMethod(Method m) {
        m.setAccessible(true);
        _paramTypes = m.getParameterTypes();
        _isStatic = Modifier.isStatic(m.getModifiers());
        _m = m;
    }

    public boolean isParamsMatch(Class<?>[] passingParams) {
        // methodParams = [method's arguments...]
        // passingParams:
        //   = [object itself, method's arguments...] for non-static method
        //   = [null, method's args...] for static method
        if (passingParams.length != _paramTypes.length + 1)
            return false;
        Class<?>[] passingParamsWoInvokerTarget = new Class<?>[passingParams.length-1];
        System.arraycopy(passingParams, 1, passingParamsWoInvokerTarget, 0, passingParamsWoInvokerTarget.length);
        return TypeUtils.isMatch(_paramTypes, passingParamsWoInvokerTarget);
    }

    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        Object[] params = new Object[args.length - 1];
        System.arraycopy(args, 1, params, 0, params.length);
        return _m.invoke(args[0], params);
    }
}
