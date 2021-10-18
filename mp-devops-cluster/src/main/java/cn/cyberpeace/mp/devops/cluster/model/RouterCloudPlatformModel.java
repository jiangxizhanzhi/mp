package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 路由模型，对应云平台接口返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "路由模型，对应云平台接口返回信息")
public class RouterCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 路由列表
     */
    private List<RouterModel> routers;


}
