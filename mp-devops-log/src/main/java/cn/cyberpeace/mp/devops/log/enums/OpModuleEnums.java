package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpModuleEnums {
    // 0."系统"
    SYSTEM("system"),
    // 1."工具"
    tool("tool"),
    // 2."课程"
    COURSE("course"),
    // 3."资源"
    RESOURCE("resource"),
    // 4."账号"
    ACCOUNT("account"),
    // 5."场景"
    SCENES("scenes"),
    // 6."职业体系"
    CAREER_SYSTEM("career_system"),
    // 7."考试"
    TEST("test"),
    // 8."习题集管理"
    PROBLEM_SET("problem_set"),
    // 9."夺旗解题"
    CAPTURE_SOLVE_PROBLEM("capture_solve_problem"),
    // 10."理论基础"
    BASIS("basis"),
    // 11."试卷"
    TEST_PAPER("test_paper"),
    // 12."任务"
    TASK("task"),
    // 13."流量"
    FLOW("flow"),
    // 14."探针"
    PROBE("probe"),
    // 15."靶标"
    TARGET("target"),
    // 16."红蓝对抗"
    RED_VS_BLUE("red_vs_blue"),
    // 17."单兵训练"
    TRAINING_SINGLE("training_single"),
    // 18."团队训练"
    TRAINING_TEAM("training_team"),
    // 19:赛场
    ARENA("arena"),
    // 20:理论赛
    THEORY("theory"),
    // 21:运维赛,
    OPERATIONS("operations"),
    // 22:夺旗赛,
    FLAG_RACE("flag_race"),
    // 23:攻防赛,
    OFFENSIVE_VS_DEFENSIVE("offensive_vs_defensive"),
    // 24:渗透赛,
    PENETRATION("penetration"),
    // 25:战队"
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
