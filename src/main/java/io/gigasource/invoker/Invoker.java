package io.gigasource.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Invoker {
    // Cause reflection is very slow so we need to cache it.
    private static HashMap<Class<?>, ReflectionCache> cache = new HashMap<>();

    /**
     * Create new instance of specified class
     * @param cls Class you want to create instance
     * @param args Args
     * @return Class instance or
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object ctor(Class<?> cls, Object... args) throws Exception {
        ReflectionCache c = get(cls);
        Class<?>[] types = TypeUtils.getTypes(args);

        // check if already have ctor in cache
        for(RichCtor rctor : c.ctors) {
            if (rctor.isParamsMatch(types))
                return rctor.create(args);
        }

        // if not then cache ctor for later use
        Constructor[] ctors = cls.getConstructors();
        for(Constructor ct: ctors) {
            RichCtor rct = new RichCtor(ct);
            if (rct.isParamsMatch(TypeUtils.getTypes(args))) {
                c.ctors.add(rct);
                return ct.newInstance(args);
            }
        }

        throw new NoSuchMethodException(cls.getCanonicalName() + " doens't have constructor with provided args.");
    }

    /**
     * Invoke method of specified class with params
     * @param cls
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    public static Object invoke(Class<?> cls, String method, Object... args) throws Exception {
        ReflectionCache c = get(cls);
        Class<?>[] types = TypeUtils.getTypes(args);

        // invoke from cache
        if (c.methodMap.containsKey(method)) {
            for(RichMethod rm : c.methodMap.get(method)) {
                if (rm.isParamsMatch(types)) {
                    return rm.invoke(args);
                }
            }
        }

        // find match method, cache, invoke result then return
        Method[] methods = cls.getDeclaredMethods();
        for(Method m : methods) {
            String name = m.getName();
            if (name.equals(method)) {
                RichMethod rm = new RichMethod(m);
                if (rm.isParamsMatch(types)) {
                    if (!c.methodMap.containsKey(name))
                        c.methodMap.put(name, new ArrayList<>());
                    c.methodMap.get(name).add(rm);
                    return rm.invoke(args);
                }
            }
        }

        throw new NoSuchMethodException(cls.getCanonicalName() + " don't have method " + method + " with provided arguments");
    }

    /**
     * Get field of current class
     * @param cls
     * @param fieldName
     * @return
     * @throws Exception
     */
    public static Field fieldInfo(Class<?> cls, String fieldName) throws Exception {
        ReflectionCache c = get(cls);
        if (c.fields.containsKey(fieldName)) {
            return c.fields.get(fieldName);
        }
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        c.fields.put(fieldName, field);
        return field;
    }

    /**
     * Get field value
     * @param cls
     * @param fieldName
     * @param target
     * @return
     * @throws Exception
     */
    public static Object field(Class<?> cls, String fieldName, Object target) throws Exception {
        return fieldInfo(cls, fieldName).get(target);
    }

    /**
     * Get nested class of current class
     * @param cls current class
     * @param className nested class name
     * @return Nested class or null
     */
    public static Object cls(Class<?> cls, String className) {
        ReflectionCache c = get(cls);
        if (c.clss.containsKey(className)) {
            return c.clss.get(className);
        }

        Class<?>[] nestClasses = cls.getDeclaredClasses();
        for(Class<?> nestCls : nestClasses) {
            if (nestCls.getName().equals(className)) {
                c.clss.put(className, nestCls);
                return nestCls;
            }
        }

        return null;
    }

    /**
     * Return cached reflection cache if exist, or create new, cache then return created instance
     * @param cls
     * @return
     */
    private static ReflectionCache get(Class<?> cls){
        if (!cache.containsKey(cls))
            cache.put(cls, new ReflectionCache());
        return cache.get(cls);
    }
}




