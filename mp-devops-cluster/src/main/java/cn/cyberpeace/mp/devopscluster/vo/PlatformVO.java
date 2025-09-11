package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocol;
import cn.cyberpeace.mp.devopscluster.model.PlatformModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformVO extends PlatformModel {

    @ApiModelProperty(value = "接入信息")
    private List<AccessProtocol> accessProtocolList;

}
