package cn.cyberpeace.mp.devopscluster.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @ClassName HypervisorWeightAndMaxInstanceDTO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorWeightAndMaxInstanceDTO extends BaseDTO {

    /**
     * 主机名/节点名称
     */
    @ApiModelProperty(value = "主机名/节点名称")
    private String hostname;
}
