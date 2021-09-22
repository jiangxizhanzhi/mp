package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("网关信息")
public class ExtGateway {

    private List<PortConfig> portConfigs;
}
