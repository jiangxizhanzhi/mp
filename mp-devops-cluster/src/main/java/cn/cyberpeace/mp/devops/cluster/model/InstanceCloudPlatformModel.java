package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 虚拟机模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "虚拟机模型，对应云平台接口返回信息")
public class InstanceCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 实例列表
     */
    private List<InstanceModel> instances;


}
