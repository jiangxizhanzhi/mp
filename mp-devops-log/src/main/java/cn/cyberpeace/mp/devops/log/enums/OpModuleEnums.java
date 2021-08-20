package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpModuleEnums {

    SYSTEM("系统"),

    tool("工具"),

    COURSE("课程"),

    RESOURCE("资源"),

    ACCOUNT("账号"),

    SCENES("场景"),

    CAREER_SYSTEM("职业体系"),

    TEST("考试"),

    PROBLEM_SET("习题集管理"),

    CAPTURE_SOLVE_PROBLEM("夺旗解题"),

    BASIS("理论基础"),

    TEST_PAPER("试卷"),

    TASK("任务"),

    FLOW("流量"),

    PROBE("探针"),

    TARGET("靶标"),

    RED_VS_BLUE("红蓝对抗"),

    TRAINING_SINGLE("单兵训练"),

    TRAINING_TEAM("团队训练"),

    DEFAULT("");

    private String desc;

    OpModuleEnums(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }


    @JsonCreator
    public static OpModuleEnums fromValue(int value) {
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
