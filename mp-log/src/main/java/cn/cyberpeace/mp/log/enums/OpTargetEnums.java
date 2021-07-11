package cn.cyberpeace.mp.log.enums;

import cn.cyberpeace.mp.base.annotation.SwaggerDisplayEnum;
import cn.cyberpeace.mp.log.constant.LogConstant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@SwaggerDisplayEnum
public enum OpTargetEnums {

    
    USER("user"),
    
    ROLE("role"),
    
    ORG("org"),
    
    TARGET("target"),
    
    MENU("menu"),
    
    IMAGE("image"),
    
    SNAPSHOT("snapshot"),
    
    REALEQUIPMENT("real_equipment"),
    
    SERVICE("service"),
    
    PROBE("probe"),
    
    COLLECTION_TEMPLATE("collection_template"),
    
    FLOW_INFO("flow_info"),
    
    FLOW_INFO_CATEGORY("flow_info_category"),
    
    FLOW_COLLECTION("flow_collection"),
    
    NOTICE("notice"),
    
    SYS_CONFIG("sys_config"),
    
    SCENE_CONFIG("scene_config"),
    
    SCENE_INSTANCE("scene_instance"),
    
    SCENE_ELEMENT("scene_element"),
    
    SCENE_ELEMENT_FLOW("scene_element_flow"),
    
    VIRTUAL_USB("virtual_usb");
    


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
