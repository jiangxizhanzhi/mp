package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName CommonTargetNameEnum
 * @Description 常用靶标名称枚举信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum CommonTargetNameEnum {

    SWITCH_SIMPLE("Switch Simple","交换机"),

    ROUTER("Router","路由器"),

    FIREWALL("Firewall","防火墙"),

    INTERNET("Internet","外部网络"),

    NPC("NPC","仿真节点"),

    CORE_ROUTER("Core Router","核心路由器");

    public static final Map<String, CommonTargetNameEnum> map = new HashMap<>();

    static {
        for (CommonTargetNameEnum t : CommonTargetNameEnum.values()) {
            map.put(t.getState(), t);
        }
    }

    private String state;

    private String desc;

    /**
     * CommonTargetNameEnum
     *
     * @param state
     * @param desc
     */
    CommonTargetNameEnum(String state, String desc) {
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
     * @return 常用靶标名称枚举信息
     */
    @JsonCreator
    public static CommonTargetNameEnum fromValue(String state) {
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
