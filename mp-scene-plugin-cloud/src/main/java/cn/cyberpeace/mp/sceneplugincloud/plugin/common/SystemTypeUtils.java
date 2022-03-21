package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemTypeUtils {

    public static final SystemTypeItem DEFAULT_SYSTEM_TYPE = new SystemTypeItem("LINUX", "LINUX", BaseSceneElementModel.SystemType.LINUX);

    public static final Map<String, SystemTypeItem> KEY_SYSTEM_TYPE_MAP = genKeySystemTypeMap(Arrays.asList(
            new SystemTypeItem("WINDOWS", "WINDOWS", BaseSceneElementModel.SystemType.WINDOWS),
            new SystemTypeItem("LINUX", "LINUX", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("UNIX", "UNIX", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("BSD", "BSD", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("OTHER", "OTHER", BaseSceneElementModel.SystemType.OTHER),

            new SystemTypeItem("1", "AIX", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("2", "HP-UX", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("3", "Solaris", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("4", "IRIX", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("5", "A/UX", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("6", "Xenix", BaseSceneElementModel.SystemType.LINUX),

            new SystemTypeItem("97", "WindowsXP", BaseSceneElementModel.SystemType.WINDOWS_XP),
            new SystemTypeItem("98", "Windows-Server2016", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2016),
            new SystemTypeItem("99", "Windows8", BaseSceneElementModel.SystemType.WINDOWS_8),
            new SystemTypeItem("100", "Windows-Server2008", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2008),
            new SystemTypeItem("101", "Windows-Server2003", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2003),
            new SystemTypeItem("102", "Windows Visita", BaseSceneElementModel.SystemType.WINDOWS_VISTA), // 兼容错误
            new SystemTypeItem("102", "Windows Vista", BaseSceneElementModel.SystemType.WINDOWS_VISTA),
            new SystemTypeItem("103", "Windows-Server2019", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2019),
            new SystemTypeItem("104", "Windows-Server2012", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2012),
            new SystemTypeItem("105", "Windows-Server2010", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2010),
            new SystemTypeItem("106", "Windows10", BaseSceneElementModel.SystemType.WINDOWS_10),
            new SystemTypeItem("107", "Windows7", BaseSceneElementModel.SystemType.WINDOWS_7),
            new SystemTypeItem("108", "Windows-Server2000", BaseSceneElementModel.SystemType.WINDOWS_SERVER_2000),

            new SystemTypeItem("201", "FreeBSD", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("202", "OpenBSD", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("203", "NetBSD", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("204", "DragonFly BSD", BaseSceneElementModel.SystemType.LINUX),

            new SystemTypeItem("301", "Gentoo", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("302", "Mint", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("303", "Arch", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("304", "Ubuntu", BaseSceneElementModel.SystemType.UBUNTU),
            new SystemTypeItem("305", "Deepin", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("306", "CentOS", BaseSceneElementModel.SystemType.CENTOS),
            new SystemTypeItem("307", "Debian", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("308", "Rocky", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("309", "Kali", BaseSceneElementModel.SystemType.KALI),
            new SystemTypeItem("310", "Fedora", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("311", "Android", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("312", "Redhat", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("313", "SUSE", BaseSceneElementModel.SystemType.LINUX),
            new SystemTypeItem("314", "Other", BaseSceneElementModel.SystemType.OTHER),

            new SystemTypeItem("401", "IOS", BaseSceneElementModel.SystemType.LINUX)
    ));

    private static Map<String, SystemTypeItem> genKeySystemTypeMap(List<SystemTypeItem> systemTypeItems) {
        Map<String, SystemTypeItem> keySystemTypeMap = new HashMap<>();
        systemTypeItems.forEach(systemTypeItem -> {
            keySystemTypeMap.put(systemTypeItem.getId().toLowerCase(), systemTypeItem);
            keySystemTypeMap.put(systemTypeItem.getName().toLowerCase(), systemTypeItem);
            keySystemTypeMap.put(systemTypeItem.getStandardName().toLowerCase(), systemTypeItem);
        });
        return keySystemTypeMap;
    }

    private SystemTypeUtils() {}

    public static SystemTypeItem getSystemTypeItem(String systemType) {
        if (StringUtils.isEmpty(systemType)) {
            return DEFAULT_SYSTEM_TYPE;
        }
        return KEY_SYSTEM_TYPE_MAP.getOrDefault(systemType.toLowerCase(), DEFAULT_SYSTEM_TYPE);
    }

    public static String getStandardSystemType(String systemType) {
        return getSystemTypeItem(systemType).getStandardName();
    }

    @Data
    @AllArgsConstructor
    public static class SystemTypeItem {
        private String id;
        private String name;
        private String standardName;
    }

}
