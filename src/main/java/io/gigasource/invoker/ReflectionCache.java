package io.gigasource.invoker;

import java.util.ArrayList;
import java.util.HashMap;

public class ReflectionCache {
    public ArrayList<RichCtor> ctors;
    public HashMap<String, ArrayList<RichMethod>> methodMap;

    public ReflectionCache() {
        ctors = new ArrayList<>();
        methodMap = new HashMap<>();
    }
}
