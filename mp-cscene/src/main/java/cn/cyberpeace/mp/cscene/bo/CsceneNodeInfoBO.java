package cn.cyberpeace.mp.cscene.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    private String resourceName;
    private Integer referenceType;
    private Integer event;
    private Integer eventStatus;
    private String userId;
    private String processMessage;
    private Integer sceneStatus;
    private String sceneStatusDesc;
    private Boolean deleted;
}
