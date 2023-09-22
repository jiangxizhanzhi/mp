package cn.cyberpeace.mp.situationaware.constant;

/**
 * 告警匹配规则中的告警方向
 *
 * @ClassName WarningTargetEnum
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 14:36
 **/
public enum WarningTargetEnum implements BaseKeyAndName {

    FILE(1, "关键文件系统"),

    PROCESS(2, "系统进程"),

    CONFIG(3, "系统配置"),

    SYSTEM_LOG(4, "系统日志"),

    CORE_LOG(5, "关键日志")


    ;



    WarningTargetEnum(Integer key, String name) {
        this.key = key;
        this.name = name;
    }

    private final Integer key;

    private final String name;
    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }
}
