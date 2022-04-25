package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 流表模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "流表模型，对应云平台接口返回信息")
public class OpenFlowCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 流表列表
     */
    private List<OpenFlow> openFlows;


}
