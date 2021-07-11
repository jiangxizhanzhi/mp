package cn.cyberpeace.mp.auth.constant;


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
