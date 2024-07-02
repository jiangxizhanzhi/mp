package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName CloudPlatformEnum
 * @Description 云平台枚举信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum CloudPlatformEnum{

    UP("up","正常"),

    DOWN("down","异常"),

    STOPPED("Stopped","已停止"),

    PAUSED("PAUSED","暂停"),

    UPPER_DOWN("DOWN","异常"),

    ACTIVE("ACTIVE","运行"),

    RUNNING("Running","运行中"),

    ERROR("Error","错误"),

    POWEROFF("PowerOff","已关机");

    private static final Map<String, CloudPlatformEnum> map = new HashMap<>();

    static {
        for (CloudPlatformEnum t : CloudPlatformEnum.values()) {
            map.put(t.getState(), t);
        }
    }

    private String state;

    private String desc;

    /**
     * CloudPlatformEnum
     *
     * @param state
     * @param desc
     */
    CloudPlatformEnum(String state, String desc) {
        this.state = state;
        this.desc = desc;

    }

    /**
     * getState
     *
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * getDesc
     *
     * @return
     */
    public String getDesc() {
        return desc;
    }


    /**
     * fromValue
     *
     * @param state
     * @return 云平台枚举信息
     */
    @JsonCreator
    public static CloudPlatformEnum fromValue(String state) {
        return map.get(state);
    }

    /**
     * toValue
     *
     * @return
     */
    @JsonValue
    public String toValue() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.equals(Locale.CHINA) ? this.getDesc() : this.getState();
    }
}
