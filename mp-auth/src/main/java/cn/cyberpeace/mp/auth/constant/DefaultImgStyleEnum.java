package cn.cyberpeace.mp.auth.constant;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/9/1 17:37
 */
public enum DefaultImgStyleEnum {

    DEFAULT("default", "默认", DefaultImgDirEnum.TARGET.getGroup()),

    FLAT("flat", "扁平", DefaultImgDirEnum.DEFAULT_IMG_TARGET_FLAT.getGroup()),

    LINE("line", "线性", DefaultImgDirEnum.DEFAULT_IMG_TARGET_LINE.getGroup()),

    REALISTIC("realistic", "写实", DefaultImgDirEnum.DEFAULT_IMG_TARGET_REALISTIC.getGroup());

    private final String style;

    private final String name;

    private final String group;

    DefaultImgStyleEnum(String style, String name, String group) {
        this.style = style;
        this.name = name;
        this.group = group;
    }

    public String getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }
}
