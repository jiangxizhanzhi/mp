package cn.cyberpeace.mp.devops.cluster.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RealEquipmentScenesPageRequest extends BasePageRequest{

    /**
     * UUID
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;
}
