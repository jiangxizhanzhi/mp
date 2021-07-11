package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpModuleEnums {
    
    SYSTEM("system"),
    
    tool("tool"),
    
    COURSE("course"),
    
    RESOURCE("resource"),
    
    ACCOUNT("account"),
    
    SCENES("scenes"),
    
    CAREER_SYSTEM("career_system"),
    
    TEST("test"),
    
    PROBLEM_SET("problem_set"),
    
    CAPTURE_SOLVE_PROBLEM("capture_solve_problem"),
    
    BASIS("basis"),
    
    TEST_PAPER("test_paper"),
    
    TASK("task"),
    
    FLOW("flow"),
    
    PROBE("probe"),
    
    TARGET("target"),
    
    RED_VS_BLUE("red_vs_blue"),
    
    TRAINING_SINGLE("training_single"),
    
    TRAINING_TEAM("training_team"),
    
    ARENA("arena"),
    
    THEORY("theory"),
    
    OPERATIONS("operations"),
    
    FLAG_RACE("flag_race"),
    
    OFFENSIVE_VS_DEFENSIVE("offensive_vs_defensive"),
    
    PENETRATION("penetration"),
    
    TEAM("team"),

    DEFAULT("");

    private String code;

    OpModuleEnums(String code) {
        this.code = LogConstant.LOG_OP_MODULE_PREFIX + "." + code;
    }

    public String getCode() {
        return code;
    }


    @JsonCreator
    public static OpModuleEnums fromValue(int value) {
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
