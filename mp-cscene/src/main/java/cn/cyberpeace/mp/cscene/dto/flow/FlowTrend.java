package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class FlowTrend {

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("流量大小")
    private Long dataSize;
}
