package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerPageRequest extends BasePageRequest {

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "分类")
    private String categories;

    
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

}
