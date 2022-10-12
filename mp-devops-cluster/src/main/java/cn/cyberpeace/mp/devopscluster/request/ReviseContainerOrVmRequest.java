package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviseContainerOrVmRequest {
    /**
     * 实例对应的云平台的id
     */
    @ApiModelProperty(value = "实例对应的云平台的id")
    private String id;
}
