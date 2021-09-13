package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * 容器模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "容器模型，对应云平台接口返回信息")
public class ContainerCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 容器列表
     */
    private List<ContainerModel> containers;


}
