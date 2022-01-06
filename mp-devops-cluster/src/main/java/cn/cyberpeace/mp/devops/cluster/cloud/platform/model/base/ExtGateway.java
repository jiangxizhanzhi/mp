package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 扩展网关信息
 * @author wanggang
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("网关信息")
public class ExtGateway implements Serializable {

    private List<PortConfig> portConfigs;
}
