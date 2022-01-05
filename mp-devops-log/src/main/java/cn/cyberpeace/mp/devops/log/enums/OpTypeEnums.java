package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTypeEnums {
    // 登入
    LOGIN("login"),
    // 登出
    LOGOUT("logout"),
    // 创建
    CREATE("create"),
    // 更新
    UPDATE("update"),
    // 删除
    DELETE("delete"),
    // 学习
    LEARN("learn"),
    // 练习
    EXERCISE("exercise"),
    // 考试
    EXAM("exam"),
    // 训练
    TRAIN("train"),
    // 测评
    EVAL("eval"),
    // 参赛
    CONTEST("contest"),
    // 停止
    STOP("stop"),
    // 暂停
    PAUSE("pause"),
    // 启动
    START("start"),
    // 恢复
    RECOVER("recover"),

    DEFAULT("");


    private String code;

    OpTypeEnums(String code) {
        this.code = LogConstant.LOG_OP_TYPE_PREFIX + "." + code;
    }

    public String getCode() {
        return code;
    }


    @JsonCreator
    public static OpTypeEnums fromValue(int value) {
        return values()[value];
    }

    @JsonValue
    public String toValue() {
        return this.getCode();
    }

    @Override
    public String toString() {
        return this.ordinal() + ":" + code;
    }
}
