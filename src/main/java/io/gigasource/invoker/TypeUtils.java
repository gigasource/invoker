package io.gigasource.invoker;

import java.util.HashMap;

public class TypeUtils {
    public static HashMap<Class<?>, Class<?>> _primitiveMap;

    /**
     * Initialize primitiveType map if it's not initialized
     */
    private static void ensurePrimitiveMapInitialized() {
        // just simple type matching
        // for example, pass integer to method which accept double is not work.
        if (_primitiveMap == null) {
            _primitiveMap = new HashMap<>();
            _primitiveMap.put(Byte.class, byte.class);
            _primitiveMap.put(Short.class, short.class);
            _primitiveMap.put(Integer.class, int.class);
            _primitiveMap.put(Long.class, long.class);
            _primitiveMap.put(Double.class, double.class);
            _primitiveMap.put(Float.class, float.class);
            _primitiveMap.put(Boolean.class, boolean.class);
            _primitiveMap.put(Character.class, char.class);
        }
    }

    /**
     * The purpose of this method is to convert passing params type to primitive if possible
     * Because when we call in reflection, all params will be box in Object type
     * If we pass an 'int' value, this class will be box then become 'java.lang.Integer' type
     * Then we need to un-box it before matching
     * Non-primitive won't be affacted by this method
     *
     * @param params passing parameters types
     * @return converted types
     */
    public static Class<?>[] convertToPrimitive(Class<?>[] params) {
        ensurePrimitiveMapInitialized();
        Class<?>[] convertedCls = new Class<?>[params.length];
        for (int i=0; i<params.length; ++i) {
            if (params[i] == null) {
                convertedCls[i] = null;
            } else if (_primitiveMap.containsKey(params[i])) {
                convertedCls[i] = _primitiveMap.get(params[i]);
            } else {
                convertedCls[i] = params[i];
            }
        }
        return convertedCls;
    }

    /**
     * Return type from object data
     * @param args
     * @return
     * @throws Exception
     */
    public static Class<?>[] getTypes(Object... args) {
        if (args.length == 0)
            return new Class<?>[0];
        Class<?>[] type = new Class<?>[args.length];
        for(int i=0; i<args.length; ++i) {
            type[i] = args[i] == null ? null : args[i].getClass();
        }
        return type;
    }

    public static boolean isMatch(Class<?>[] declared, Class<?>[] passing) {
        passing = TypeUtils.convertToPrimitive(passing);

        if (declared.length != passing.length)
            return false;
        for (int i=0; i<passing.length; ++i) {
            if (!TypeUtils._isSubClassOrEqual(passing[i], declared[i]))
                return false;
        }
        return true;
    }

    private static boolean _isSubClassOrEqual(Class<?> target, Class<?> ancestor) {
        // in case the user passing null to params list, we cannot guess the type of params
        // so we expect it's true
        if (target == null || ancestor == Object.class || ancestor == target)
            return true;
        do {
            target = target.getSuperclass();
            if (target == ancestor)
                return true;
        } while(target != null);

        return false;
    }
}
