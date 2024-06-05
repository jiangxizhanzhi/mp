package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.base.annotation.ParamName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchSdnSwitchRequest {
    @ApiModelProperty("页码")
    @ParamName("offset")
    private Integer page = 1;
    @ApiModelProperty("页码大小")
    @ParamName("limit")
    private Integer perPage = 10;

    @ApiModelProperty("交换机标识")
    private String name;
    @ApiModelProperty("物理交换机的实际标识")
    private String physicalSwitchName;
    @ApiModelProperty("物理交换机的管理ip")
    private String managerIp;
}
