package cn.cyberpeace.mp.base.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ModuleUtils {
    private ModuleUtils() {}

    public static final String GROUP_NAME = "cn.cyberpeace.mp";

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
