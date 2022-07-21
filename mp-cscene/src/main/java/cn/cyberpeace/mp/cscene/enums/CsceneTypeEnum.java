package cn.cyberpeace.mp.cscene.enums;

import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/21
 */
public enum CsceneTypeEnum {
    /**
     * 靶场监控的场景实例类型
     */
    EXERCISE(110, "exercise"),
    COURSE(120, "course"),
    CLASS_SCHEDULE(121, "class_schedule"),
    EXAM(130, "exam"),
    HOMEWORK(140, "homework"),
    INDIVIDUAL_TRAINING(211, "individual_training"),
    TEAM_TRAINING(213, "team_training"),
    CYBER_WAR_GAME(214, "cyber_war_game"),
    RANGE_TEST(236, "range_test"),
    AD_COMMON(300, "ad_common"),
    OPERATIONS(301, "operations"),
    PROBLEM_SOLVING_COMPETITION(302, "problem_solving_competition"),
    ATTACK_AND_DEFENSE(303, "attack_and_defense"),
    INFILTRATION_COMPETITION(304, "infiltration_competition")
    ;

    private int code;
    private String name;

    CsceneTypeEnum(int code, String name) {
        this.code = code;
        this.name = ControllerConstant.CSCENE_INSTANCE_TYPE_PREFIX + "." + name;
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
