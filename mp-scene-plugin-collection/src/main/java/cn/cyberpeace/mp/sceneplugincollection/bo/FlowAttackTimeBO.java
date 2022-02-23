package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAttackTimeBO {
    @ApiModelProperty("设备名称")
    private String nodeName;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("次数")
    private Integer times;
}