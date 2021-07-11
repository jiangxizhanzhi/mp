package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class FlowTrendRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("分段数")
    private Integer segmentCount;

    
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

    @ApiModelProperty("聚合类型，0：统计时间窗口流量大小，1：统计时间窗口流量次数，默认：0")
    private Integer type = NumberUtils.INTEGER_ZERO;



}
