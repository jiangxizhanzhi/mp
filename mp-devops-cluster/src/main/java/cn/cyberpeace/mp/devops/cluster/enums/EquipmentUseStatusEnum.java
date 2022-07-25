package cn.cyberpeace.mp.devops.cluster.enums;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/20
 */
public enum EquipmentUseStatusEnum {


    /**
     * 实装设备使用状态
     */
    FREE(0, "free"),
    USED(1, "used")
    ;

    private int code;
    private String name;

    EquipmentUseStatusEnum(int code, String name) {
        this.code = code;
        this.name = ControllerConstant.REAL_EQUIPMENT_USE_STATUS_PREFIX + "." + name;
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
