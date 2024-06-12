package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteFlowCollectBo {
    //删除optionKey
    private String sceneId;
    //删除arkime
    private String collectorInstanceId;
    //删除流量镜像
    private String flowMirrorId;
}
