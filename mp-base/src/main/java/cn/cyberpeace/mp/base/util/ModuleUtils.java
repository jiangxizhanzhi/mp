package cn.cyberpeace.mp.base.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ModuleUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ModuleUtils {

    public static final String GROUP_NAME = "cn.cyberpeace.mp";

    @SuppressWarnings("java:S2386")
    public static final String[] MODULE_NAMES = getModuleNames();

    /**
     * ModuleUtils
     */
    private ModuleUtils() {}

    /**
     * findPackageNamesStartingWith
     *
     * @param prefix
     * @return
     */
    public static List<String> findPackageNamesStartingWith(String prefix) {
        List<String> packageNames = new ArrayList<>();
        for (Package pkg: Package.getPackages()) {
            String packageName = pkg.getName();
            if (packageName.startsWith(prefix)) {
                packageNames.add(packageName);
            }
        }
        return packageNames;
    }

    /**
     * getModuleNames
     *
     * @return
     */
    public static String[] getModuleNames() {
        Set<String> moduleNames = new LinkedHashSet<>();
        for (String packageName: findPackageNamesStartingWith(GROUP_NAME)) {
            String name = packageName.substring(GROUP_NAME.length());
            if (!StringUtils.isEmpty(name)) {
                moduleNames.add(name.split("\\.")[1]);
            }
        }
        return moduleNames.toArray(new String[0]);
    }

}
