package cn.cyberpeace.mp.scene.constant;

/**
 * @ClassName SceneStage
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public abstract class SceneStage {

    private String stage;

    private String icon;

    public SceneStage(String stage, String icon) {
        this.stage = stage;
        this.icon = icon;
    }

    public String getStage() {
        return stage;
    }

    public String getIcon() {
        return icon;
    }

    public abstract String getStageName();
}