package cn.cyberpeace.mp.auth.constant;

/**
 * 默认图片文件夹
 * @author wanggang
 */
public enum DefaultImgDirEnum  {

    AVATAR("/avatar", "default_img_avatar"),

    SYS_LOGO("/sys-logo", "default_img_sys_logo"),

    APPLICATION_SYSTEM("/application-system", "default_img_application_system"),

    NETWORK_EQUIPMENT("/network-equipment", "default_img_network_equipment"),

    OPERATION_SYSTEM("/operation-system", "default_img_operation_system"),

    SAFETY_EQUIPMENT("/safety-equipment", "default_img_safety_equipment"),

    STATUS("/status", "default_img_status"),

    SCENES_EDITOR("/scenes-editor", "default_img_scenes_editor"),

    COMMON_TARGET("/common-target", "default_img_common_target"),

    SAFETY("/0_safety", "default_img_safety"),
    SERVER("/1_server", "default_img_server"),
    INDUSTRIAL_CONTROL("/2_Industrial_control", "default_img_Industrial_control"),
    EXCHANGE_1("/3_exchange_1", "default_img_exchange_1"),
    EXCHANGE_2("/4_exchange_2", "default_img_exchange_2"),
    ROUTER_1("/5_router_1", "default_img_router_1"),
    ROUTER_2("/6_router_2", "default_img_router_2"),
    GATEWAY("/7_gateway", "default_img_gateway"),
    WIRELESS("/8_wireless", "default_img_wireless"),
    BIZ("/9_biz", "default_img_biz"),

    TARGET("", "default_img_target");




    private final String endPathName;

    private final String group;

    DefaultImgDirEnum(String endPathName, String group) {
        this.endPathName = endPathName;
        this.group = group;
    }

    public String getEndPathName() {
        return endPathName;
    }

    public String getGroup() {
        return group;
    }

    public String getCurrentPath() {
        return AuthConstant.DEFAULT_IMAGE_PREFIX.concat(this.endPathName);
    }

}
