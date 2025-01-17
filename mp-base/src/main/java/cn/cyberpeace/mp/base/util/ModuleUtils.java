 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ModuleUtils.java.
 // File size 1276.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ModuleUtils {

    public static final String GROUP_NAME = "cn.cyberpeace.mp";

    @SuppressWarnings("java:S2386")
    public static final String[] MODULE_NAMES = getModuleNames();

    private ModuleUtils() {}

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

    public static String[] getModuleNames() {
        Set<String> moduleNames = new LinkedHashSet<>();
        for (String packageName: findPackageNamesStartingWith(GROUP_NAME)) {
            var name = packageName.substring(GROUP_NAME.length());
            if (!StringUtils.isEmpty(name)) {
                moduleNames.add(name.split("\\.")[1]);
            }
        }
        return moduleNames.toArray(new String[0]);
    }

}
