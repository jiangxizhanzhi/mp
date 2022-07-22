package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.devops.log.constant.LogConstant;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/20
 */
public enum OpModuleEnum {

    /**
     * 操作模块
     */
    SYSTEM(0, "system"),
    TOOL(1, "tool"),
    COURSE(2, "course"),
    RESOURCE(3, "resource"),
    ACCOUNT(4, "account"),
    SCENE(5, "scenes"),
    CAREER_SYSTEM(6, "career_system"),
    // TEST(7, "test"),
    PROBLEM_SET(8, "problem_set"),
    CAPTURE_SOLVE_PROBLEM(9, "capture_solve_problem"),
    BASIS(10, "basis"),
    TEST_PAPER(11, "test_paper"),
    TASK(12, "task"),
    FLOW(13, "flow"),
    PROBE(14, "probe"),
    TARGET(15, "target"),
    RED_VS_BLUE(16, "red_vs_blue"),
    TRAINING_SINGLE(17, "training_single"),
    TRAINING_TEAM(18, "training_team"),
    ARENA(19, "arena"),
    THEORY(20, "theory"),
    OPERATIONS(21, "operations"),
    FLAG_RACE(22, "flag_race"),
    OFFENSIVE_VS_DEFENSIVE(23, "offensive_vs_defensive"),
    PENETRATION(24, "penetration"),
    TEAM(25, "team"),
    ;

    private int code;
    private String name;

    OpModuleEnum(int code, String name) {
        this.code = code;
        this.name = LogConstant.LOG_OP_MODULE_PREFIX + "." + name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
