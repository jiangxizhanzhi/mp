package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpModuleEnums {
    // "系统"
    SYSTEM("system"),
    // "工具"
    tool("tool"),
    // "课程"
    COURSE("course"),
    // "资源"
    RESOURCE("resource"),
    // "账号"
    ACCOUNT("account"),
    // "场景"
    SCENES("scenes"),
    // "职业体系"
    CAREER_SYSTEM("career_system"),
    // "考试"
    TEST("test"),
    // "习题集管理"
    PROBLEM_SET("problem_set"),
    // "夺旗解题"
    CAPTURE_SOLVE_PROBLEM("capture_solve_problem"),
    // "理论基础"
    BASIS("basis"),
    // "试卷"
    TEST_PAPER("test_paper"),
    // "任务"
    TASK("task"),
    // "流量"
    FLOW("flow"),
    // "探针"
    PROBE("probe"),
    // "靶标"
    TARGET("target"),
    // "红蓝对抗"
    RED_VS_BLUE("red_vs_blue"),
    // "单兵训练"
    TRAINING_SINGLE("training_single"),
    // "团队训练"
    TRAINING_TEAM("training_team"),

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
