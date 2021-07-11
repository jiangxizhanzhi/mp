package cn.cyberpeace.mp.log.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
@Data
public class EnvConfig {

    private static String addtionLocation = "";

    private static String mpEnvPath;

    private static String applicationExt;

    private static String applicationProp;

    private static String profileActive;

    private EnvConfig() {
    }

    public static String getAddtionLocation() {
        return addtionLocation;
    }

    public static void setAddtionLocation(String addtionLocation) {
        EnvConfig.addtionLocation = addtionLocation;
    }

    public static String getMpEnvPath() {
        return mpEnvPath;
    }

    public static void setMpEnvPath(String mpEnvPath) {
        EnvConfig.mpEnvPath = mpEnvPath;
    }

    public static String getApplicationExt() {
        return applicationExt;
    }

    public static void setApplicationExt(String applicationExt) {
        EnvConfig.applicationExt = applicationExt;
    }

    public static String getApplicationProp() {
        return applicationProp;
    }

    public static void setApplicationProp(String applicationProp) {
        EnvConfig.applicationProp = applicationProp;
    }

    public static String getProfileActive() {
        return profileActive;
    }

    public static void setProfileActive(String profileActive) {
        EnvConfig.profileActive = profileActive;
    }

    public static void splitForExtAndProp() {
        if (StringUtils.isNotEmpty(addtionLocation)) {
            String[] files = addtionLocation.split("\\,");
            for (String file : files) {
                if (StringUtils.isNotEmpty(file) && file.endsWith(".properties")) {
                    applicationProp = file;
                    break;
                }
            }
            for (String file : files) {
                if (StringUtils.isNotEmpty(file) && (file.endsWith(".yml") || file.endsWith(".yaml"))) {
                    applicationExt = file;
                    break;
                }
            }

        }
        log.info("EnvConfig: {}", toStr());
    }

    public static String toStr() {
        final StringBuilder sb = new StringBuilder("EnvConfig{");
        sb.append("profileActive=").append(profileActive);
        sb.append("mpEnvPath=").append(mpEnvPath);
        sb.append("addtionLocation=").append(addtionLocation);
        sb.append("applicationProp=").append(applicationProp);
        sb.append("applicationExt=").append(applicationExt);
        sb.append('}');
        return sb.toString();
    }
}
