package io.gigasource.invoker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ReflectionCache {
    public HashMap<String, Class<?>> clss;
    public ArrayList<RichCtor> ctors;
    public HashMap<String, Field> fields;
    public HashMap<String, ArrayList<RichMethod>> methodMap;

    public ReflectionCache() {
        clss = new HashMap<>();
        ctors = new ArrayList<>();
        methodMap = new HashMap<>();
        fields = new HashMap<>();
    }
}
