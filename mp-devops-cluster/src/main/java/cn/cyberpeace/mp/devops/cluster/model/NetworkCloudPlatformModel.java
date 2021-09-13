package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 网络模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "网络模型，对应云平台接口返回信息")
public class NetworkCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 网络列表
     */
    private List<NetworkModel> networks;


}
