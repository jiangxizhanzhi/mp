package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 云平台枚举信息
 */
public enum VmStatusEnum {

    ACTIVE("ACTIVE","运行"),

    BUILD("BUILD","构建中"),

    DELETED("DELETED","已删除"),

    ERROR("ERROR","错误"),

    HARD_REBOOT("HARD_REBOOT","暴力重启"),

    MIGRATING("MIGRATING","迁移中"),

    PASSWORD("PASSWORD","重置密码"),

    PAUSED("PAUSED","暂停"),

    REBOOT("REBOOT","重启"),

    REBUILD("REBUILD","重建中"),

    RESCUE("RESCUE","重启"),

    RESIZE("RESIZE","重启"),

    REVERT_RESIZE("REVERT_RESIZE","重启"),

    SHELVED("SHELVED","重启"),

    SHELVED_OFFLOADED("SHELVED_OFFLOADED","重启"),

    SHUTOFF("SHUTOFF","重启"),

    SOFT_DELETED("SOFT_DELETED","已关机"),

    SUSPENDED("SUSPENDED","已关机"),

    UNKNOWN("UNKNOWN","已关机"),

    VERIFY_RESIZE("VERIFY_RESIZE","已关机");

    private static final Map<String, VmStatusEnum> map = new HashMap<>();

    static {
        Arrays.stream(VmStatusEnum.values()).forEach(
                t -> map.put(t.getState(), t)
        );
    }

    private String state;

    private String desc;

    VmStatusEnum(String state, String desc) {
        this.state = state;
        this.desc = desc;

    }

    public String getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }


    @JsonCreator
    public static VmStatusEnum fromValue(String state) {
        return map.get(state);
    }

    @JsonValue
    public String toValue() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.equals(Locale.CHINA) ? this.getDesc() : this.getState();
    }
}
