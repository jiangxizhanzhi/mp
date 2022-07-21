package cn.cyberpeace.mp.devops.cluster.enums;

import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/20
 */
public enum EquipmentPositionStatusEnum {

    /**
     * 实装设备在位状态
     */
    OFFLINE(0, "offline"),
    ONLINE(1, "online")
    ;

    private int code;
    private String name;

    EquipmentPositionStatusEnum(int code, String name) {
        this.code = code;
        this.name = ControllerConstant.REAL_EQUIPMENT_POSITION_STATUS_PREFIX + "." +name;
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
