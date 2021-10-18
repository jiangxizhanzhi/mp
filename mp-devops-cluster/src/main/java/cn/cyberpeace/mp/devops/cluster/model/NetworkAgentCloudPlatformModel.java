package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.NetworkAgentModel;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 网络代理模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "网络代理模型，对应云平台接口返回信息")
public class NetworkAgentCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 网络代理
     */
    private List<NetworkAgentModel> agents;


}
