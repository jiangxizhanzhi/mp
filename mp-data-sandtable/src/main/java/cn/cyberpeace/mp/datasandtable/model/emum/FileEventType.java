package cn.cyberpeace.mp.datasandtable.model.emum;

/**
 * 文件预警类型
 */
public enum FileEventType {
    FILE("FILE"), REGISTRY("REGISTRY");

    private final String type;

    FileEventType(String type) {
        this.type = type;
    }

    @Override public String toString() {
        return type;
    }
}
