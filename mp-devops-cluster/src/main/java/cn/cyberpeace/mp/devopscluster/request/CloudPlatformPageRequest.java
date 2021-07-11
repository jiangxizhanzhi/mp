package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CloudPlatformPageRequest extends BasePageRequest implements Serializable {

    
    @ApiModelProperty(value = "节点名称")
    private String hostName;

    
    @ApiModelProperty(value = "排序方式")
    private String sort;

}
