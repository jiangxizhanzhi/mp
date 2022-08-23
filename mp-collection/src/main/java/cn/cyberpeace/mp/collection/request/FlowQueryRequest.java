package cn.cyberpeace.mp.collection.request;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class FlowQueryRequest {
    @ApiModelProperty(value = "一条流量记录的id")
    private String dataId;

    @ApiModelProperty(value = "源ip")
    private String sourceIp;

    @ApiModelProperty(value = "目标ip")
    private String targetIp;

    @ApiModelProperty(value = "流量发生的时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
}
