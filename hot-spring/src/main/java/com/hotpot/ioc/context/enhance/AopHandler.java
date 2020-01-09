package com.hotpot.ioc.context.enhance;

import com.hotpot.aop.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
public class AopHandler implements EnhanceHandler {
    @Override
    public int getPriority() {
        return -1;
    }

    public static void main(String[] args) {
        boolean matches = Pattern.matches("com\\.hotpot\\..+", "com.hotpot.test");
        System.out.println(matches);
    }
    @Override
    public void handle(Map<String, Object> beanMap) {
        List<Class> aspectClassList = new ArrayList<>();
        filterAspectClass(beanMap, aspectClassList);

        for (Class clazz : aspectClassList) {
            // 切入点的缓存，键值对形如("pointcut()", "com\.hotpot\.test\..")
            Map<String, String> pointcutMap = new HashMap<>(16);
            // 代理方法的缓存，键值对形如("Before", list<method>)，("Around", list<method>)
            Map<String, List<Method>> methodMap = new HashMap<>(16);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                Pointcut pointcut = method.getAnnotation(Pointcut.class);
                if (pointcut != null) {
                    pointcutMap.put(method.getName() + "()", pointcut.value());
                    continue;
                }

                Before before = method.getAnnotation(Before.class);
                Around around = method.getAnnotation(Around.class);
                After after = method.getAnnotation(After.class);
                if (before != null) {
                    cacheMethod(methodMap, method, "Before");
                }
                if (around != null) {
                    cacheMethod(methodMap, method, "Around");
                }
                if (after != null) {
                    cacheMethod(methodMap, method, "After");
                }
                // TODO
            }
        }
    }

    private void cacheMethod(Map<String, List<Method>> methodMap, Method method, String name) {
        List<Method> methods = methodMap.computeIfAbsent(name, k -> new ArrayList<>());
        methods.add(method);
    }

    /**
     * 从beanMap中获取能够匹配的bean的名称 TODO
     */
    private List<String> match(Map<String, Object> beanMap, String regex) {
        return null;
    }

    /**
     * 筛选出切面类
     */
    private void filterAspectClass(Map<String, Object> beanMap, List<Class> aspectClassList) {
        try {
            for (String className : beanMap.keySet()) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Aspect.class)) {
                    aspectClassList.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
