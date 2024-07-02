package cn.cyberpeace.mp.base.util.mptool;

import java.net.URL;

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
