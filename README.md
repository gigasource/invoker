# invoker
Helper classes which allow execute Java reflection easier
### Introduction
Invoker make it easier to work with Reflection in Java.<br/>
It automatically set target ctor, methods modifier to public.<br/>
It also guessing the parameter types which has been pass into the method call so we don't need to get method, ctor signature.

###Example

```java
public class A {
    private A() {
    }
    
    private A(int x, A ref) {
    }

    private void Method1(int p1, double p2, String p3) { }
    public void Method1(int p1, int p2) {} 
}
```


Reflection
```java
public class App { 
    public static void main(String[] args) {
        // calling private ctor
        Constructor defaultCtor = A.class.getConstructor();
        defaultCtor.setAccessible(true);
        A a = (A) defaultCtor.newInstance();    
 
        // calling private ctor with parameters
        Constructor anotherCtor = A.class.getConstructor(int.class, A.class);
        anotherCtor.setAccessible(true);
        A a2 = (A) anotherCtor.newInstance(5, a);

        // calling private method
        Method m1 = A.class.getDeclaredMethod("Method1", int.class, double.class, String.class);
        m1.setAccessible(true);
        m1.invoke(a, 1, 1.5, "Hello");
 
        // calling overload method
        Method m2 = A.class.getDeclaredMethod("Method1", int.class, int.class);
        m2.invoke(a, 1, 1);
    }
}
```


With Invoker
```java
public class App { 
    public static void main(String[] args) {
        // calling private ctor
        A a = (A) Invoker.ctor(A.class);
        A a2 = (A) Invoker.ctor(A.class, 5, a);
       
        // calling private method
        Invoker.invoke(A.class, "Method1", a, 1, 1.5d, "Hello");
        // calling overload method
        Invoker.invoke(A.class, "Method1", a, 1, 1);
    }
}
```

### Roadmap
1. Improve method, ctor matching algorithm to allow passing parameters as normal use.
The following code won't work:
```
class X { 
  public static void A(int x) { }
  public static void B(double x) { }
}

Invoker.invoke(X.class, "A", null, 5); // work
Invoker.invoke(X.class, "B", null, 5); // won't work (type mismatch between int and double
```
