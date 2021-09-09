package cn.cyberpeace.mp.devops.cluster.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "实装-接口资源分页请求信息")
public class RealEquipmentNetworkCardPageRequest extends BasePageRequest{

    /**
     * UUID
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;
}
