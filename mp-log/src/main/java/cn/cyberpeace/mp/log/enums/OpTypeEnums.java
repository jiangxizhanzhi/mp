package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTypeEnums {
    
    LOGIN("login"),
    
    LOGOUT("logout"),
    
    CREATE("create"),
    
    UPDATE("update"),
    
    DELETE("delete"),
    
    LEARN("learn"),
    
    EXERCISE("exercise"),
    
    EXAM("exam"),
    
    TRAIN("train"),
    
    EVAL("eval"),
    
    CONTEST("contest"),
    
    STOP("stop"),
    
    PAUSE("pause"),
    
    START("start"),
    
    RECOVER("recover"),
    
    EXPORT("export"),

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
