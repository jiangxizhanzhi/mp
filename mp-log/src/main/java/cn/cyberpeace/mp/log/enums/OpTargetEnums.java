 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpTargetEnums.java.
 // File size 1847.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.log.constant.LogConstant;
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
    // 菜单
    MENU("menu"),
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
    // 流量文件
    FLOW_INFO("flow_info"),
    // 流量文件分类
    FLOW_INFO_CATEGORY("flow_info_category"),
    // 流量集合
    FLOW_COLLECTION("flow_collection"),
    // 公告
    NOTICE("notice"),
    // 配置
    SYS_CONFIG("sys_config"),
    // 场景模板
    SCENE_CONFIG("scene_config"),
    // 场景
    SCENE_INSTANCE("scene_instance"),
    // 节点
    SCENE_ELEMENT("scene_element"),
    // 节点流量
    SCENE_ELEMENT_FLOW("scene_element_flow"),
    //虚拟usb
    VIRTUAL_USB("virtual_usb");
    //DEFAULT("");


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
