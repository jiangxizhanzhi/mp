package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName DeleteFlowCollectBo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
