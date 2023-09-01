package cn.cyberpeace.mp.scene.constant;

public abstract class SceneStage {

    private String stage;

    public SceneStage(String stage) {
        this.stage = stage;
    }

    public String getStage() {
        return stage;
    }

    public abstract String getStageName();
}