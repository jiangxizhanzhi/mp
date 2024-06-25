package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 镜像发生器操作请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowActuatorRequest implements Serializable {

    /**
     * 镜像发生器ID
     */
    @NotEmpty
    @ApiModelProperty(value = "镜像发生器ID列表")
    private List<String> ids;

}
