package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTargetEnums {

    // "用户"
    USER("user"),
    // "角色"
    ROLE("role"),
    // "组织"
    ORG("org"),
    // "资源"
    TARGET("target"),
    //镜像
    IMAGE("image"),
    //快照
    SNAPSHOT("snapshot"),
    //实装
    REALEQUIPMENT("real_equipment"),
    //服务
    SERVICE("service"),
    // 探针
    PROBE("probe"),
    // 采集模板
    COLLECTION_TEMPLATE("collection_template"),
    // 场景模板
    SCENE_CONFIG("scene_config"),
    // 场景
    SCENE_INSTANCE("scene_instance"),
    // 节点
    SCENE_ELEMENT("scene_element"),
    DEFAULT("");


    private String code;

    OpTargetEnums(String code) {
        this.code = LogConstant.LOG_OP_TARGET_PREFIX + "." + code;
    }

    public String getCode() {
        return code;
    }


    @JsonCreator
    public static OpTargetEnums fromValue(int value) {
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
