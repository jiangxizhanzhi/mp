package cn.cyberpeace.mp.scene.model.base;

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
    @DefaultStr
    private String flavor;
    @DefaultStr
    private String initCommands;

    public static class Type {
        public static final Integer OTHER = 0;
        public static final Integer SWITCH = 1;
        public static final Integer ROUTER = 2;
        public static final Integer FIREWALL = 3;
        public static final Integer TERMINAL = 4;

        private Type() {}
    }

    public static class SystemType {
        public static final String OTHER = "other";

        private SystemType() {}
    }

    public static class SystemArchitecture {
        public static final String OTHER = "other";

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
