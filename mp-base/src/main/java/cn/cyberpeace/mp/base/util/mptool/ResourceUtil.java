 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResourceUtil.java.
 // File size 1170.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.mptool;

import java.net.URL;

/**
 * @ClassName ResourceUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ResourceUtil {

    /**
     * getResource
     *
     * @param resource
     * @param baseClass
     * @return
     */
    public static URL getResource(String resource, Class<?> baseClass) {
        return (null != baseClass) ? baseClass.getResource(resource) : getClassLoader().getResource(resource);
    }

    /**
     * getClassLoader
     *
     * @return
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ResourceUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }

    /**
     * getContextClassLoader
     *
     * @return
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
