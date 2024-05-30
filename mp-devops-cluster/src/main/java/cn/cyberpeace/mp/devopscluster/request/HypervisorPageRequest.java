package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HypervisorPageRequest extends BasePageRequest {
    @ApiModelProperty(value = "只查询主机名称")
    private Boolean onlyHostname;
}
