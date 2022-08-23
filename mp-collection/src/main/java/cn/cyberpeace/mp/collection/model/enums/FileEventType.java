package cn.cyberpeace.mp.collection.model.enums;

/**
 * 文件预警类型
 */
public enum FileEventType {
    FILE("FILE"), REGISTRY("REGISTRY"), PROCESS("PROCESS"), PORT("PORT"), INTERNET_CONNECTION("INTERNET_CONNECTION"),
    USERS("USERS");

    private final String type;

    FileEventType(String type) {
        this.type = type;
    }

    @Override public String toString() {
        return type;
    }
}
