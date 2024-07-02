package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TaiShiTotalCount
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "场景态势流量和主机事件总数统计")
public class TaiShiTotalCount {
    @ApiModelProperty(value = "流量事件总数")
    private Long flow;

    @ApiModelProperty(value = "主机事件总数")
    private Long host;
}
