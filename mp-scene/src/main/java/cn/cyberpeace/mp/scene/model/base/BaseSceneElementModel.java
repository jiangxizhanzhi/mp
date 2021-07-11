package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneElementModel extends BaseSceneNodeModel {
    @DefaultInt
    private Integer type;
    @DefaultStr
    private String file;
    @DefaultStr(SystemType.OTHER)
    private String systemType;
    @DefaultStr(SystemArchitecture.OTHER)
    private String systemArchitecture;
    @DefaultStr(SystemLanguage.OTHER)
    private String systemLanguage;
    @DefaultInt
    private Integer cpu;
    @DefaultInt
    private Integer ram;
    @DefaultInt
    private Integer disk;
    @DefaultStr
    private String initCommands;
    @DefaultInt
    private Integer maxPortNumber;
    @DefaultBool
    private Boolean isAuxiliary;

    public static class Type {
        public static final int OTHER = 0;
        public static final int SWITCH = 1;
        public static final int ROUTER = 2;
        public static final int FIREWALL = 3;
        public static final int TERMINAL = 4;
        public static final int NPC = 7;

        private Type() {}
    }

    public static class SystemType {
        public static final String OTHER = "other";
        public static final String WINDOWS_11 = "windows11";
        public static final String WINDOWS_10 = "windows10";
        public static final String WINDOWS_8 = "windows8";
        public static final String WINDOWS_7 = "windows7";
        public static final String WINDOWS_XP = "windowsxp";
        public static final String WINDOWS_VISTA = "windows_vista";
        public static final String WINDOWS_SERVER_2018 = "windows-server2018";
        public static final String WINDOWS_SERVER_2016 = "windows-server2016";
        public static final String WINDOWS_SERVER_2012 = "windows-server2012";
        public static final String WINDOWS_SERVER_2010 = "windows-server2010";
        public static final String WINDOWS_SERVER_2008 = "windows-server2008";
        public static final String WINDOWS_SERVER_2003 = "windows-server2003";
        public static final String WINDOWS_SERVER_2000 = "windows-server2000";
        public static final String CENTOS = "centos";
        public static final String UBUNTU = "ubuntu";
        public static final String KALI = "kali";

        public static final String WINDOWS = "windows";
        public static final String LINUX = "linux";

        private SystemType() {}
    }

    public static class SystemArchitecture {
        public static final String OTHER = "other";
        public static final String BIT_32 = "32";
        public static final String BIT_64 = "64";

        private SystemArchitecture() {}
    }

    public static class SystemLanguage {
        public static final String OTHER = "other";
        public static final String CN = "cn";
        public static final String TC = "tc";
        public static final String EN = "en";
        public static final String FR = "fr";
        public static final String RU = "ru";
        public static final String JA = "ja";
        public static final String KO = "ko";
        public static final String VI = "vi";
        public static final String AR = "ar";

        private SystemLanguage() {}
    }
}
