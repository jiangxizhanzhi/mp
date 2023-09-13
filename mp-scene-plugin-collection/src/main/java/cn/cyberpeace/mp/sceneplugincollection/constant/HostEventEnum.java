package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.situationaware.constant.SituationBase18nCode;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;

/**
 * 主机事件枚举
 * @author zhanzhi
 */
public enum HostEventEnum {

    ALL(0, SituationBase18nCode.EVENT_TYPE_ALL, null, null),
    PROCESS(1, SituationBase18nCode.EVENT_TYPE_PROCESS, ProbeConstant.ProbeId.PROCESS_INFO, FileEventType.PROCESS),
    PORT(2, SituationBase18nCode.EVENT_TYPE_PORT, ProbeConstant.ProbeId.PORT_INFO, FileEventType.PORT),
    INTERNET_CONNECT(3, SituationBase18nCode.EVENT_TYPE_INTERNET_CONNECT, ProbeConstant.ProbeId.INTERNET_CONNECTION, FileEventType.INTERNET_CONNECTION),
    FILE(4, SituationBase18nCode.EVENT_TYPE_FILE, ProbeConstant.ProbeId.FILE_MODIFY, FileEventType.FILE),
    USERS(5, SituationBase18nCode.EVENT_TYPE_USER, ProbeConstant.ProbeId.USERS_MODIFY, FileEventType.USERS),
    REGISTRY(6, SituationBase18nCode.EVENT_TYPE_REGISTRY, ProbeConstant.ProbeId.REGISTRY_MODIFY, FileEventType.REGISTRY),

    ;


    HostEventEnum(Integer id, String i18nCode, String probeId, FileEventType fileEventType) {
        this.id = id;
        this.i18nCode = i18nCode;
        this.probId = probeId;
        this.fileEventType = fileEventType;

    }

    private final Integer id;

    private final String i18nCode;

    private final String probId;

    private final FileEventType fileEventType;

    public Integer getId() {
        return id;
    }

    public String getI18nMessage() {
        return MessageCommon.get(this.getI18nCode());
    }

    public String getProbId() {
        return probId;
    }

    public FileEventType getFileEventType() {
        return fileEventType;
    }

    private String getI18nCode() {
        return i18nCode;
    }

}
