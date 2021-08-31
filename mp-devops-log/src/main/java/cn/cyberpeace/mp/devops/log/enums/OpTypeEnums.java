package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTypeEnums {

    LOGIN("登入"),

    LOGOUT("登出"),

    CREATE("创建"),

    UPDATE("更新"),

    DELETE("删除"),

    LEARN("学习"),

    EXERCISE("练习"),

    EXAM("考试"),

    TRAIN("训练"),

    EVAL("测评"),

    CONTEST("参赛"),

    STOP("停止"),

    PAUSE("暂停"),

    START("启动"),

    RECOVER("恢复"),

    DEFAULT("");


    private String desc;

    OpTypeEnums(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    @JsonCreator
    public static OpTypeEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public String toValue() {
        return this.getDesc();
    }

    @Override
    public String toString() {
        return this.ordinal() + ":" + desc;
    }
}
