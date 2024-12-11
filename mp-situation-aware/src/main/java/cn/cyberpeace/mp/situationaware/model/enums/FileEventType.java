package cn.cyberpeace.mp.situationaware.model.enums;

/**
 * 文件预警类型
 */
public enum FileEventType {
    FILE("FILE"), REGISTRY("REGISTRY"), PROCESS("PROCESS"), PORT("PORT"), INTERNET_CONNECTION("INTERNET_CONNECTION"),
    USERS("USERS"),LOG("LOG"),OS_LOG("OS_LOG"),APPLICATION_LOG("APPLICATION_LOG"),SECURITY_LOG("SECURITY_LOG");

    private final String type;

    FileEventType(String type) {
        this.type = type;
    }

    @Override public String toString() {
        return type;
    }
}
