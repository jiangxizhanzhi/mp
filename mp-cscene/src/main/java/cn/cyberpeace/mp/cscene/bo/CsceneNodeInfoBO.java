package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneNodeInfoBO {
    private String csceneId;
    private String csceneName;
    private String InternalSceneEid;
    private String InternalNodeEid;
    private String nodeId;
    private String nodeName;
    private Integer ram;
    private Integer nodeEvent;
    private Integer nodeEventStatus;
    private LocalDateTime nodeUpdateTime;
    private String resourceName;
    private Integer referenceType;
    private Integer event;
    private Integer eventStatus;
    private String userId;
    private String processMessage;
    private Integer sceneStatus;
    private String sceneStatusDesc;
    private Boolean deleted;
    private Double cpuPercent;

    public boolean isPause() {
        if (nodeEvent == null || nodeEventStatus == null) {
            return false;
        }
        return nodeEvent == SceneElementModel.Event.PAUSE && nodeEventStatus == EventStatus.OVER;
    }

    public boolean isSuspend(){
        if (nodeEvent == null || nodeEventStatus == null) {
            return false;
        }
        return nodeEvent == SceneElementModel.Event.SNAPSHOT && nodeEventStatus == EventStatus.OVER;
    }
}
