package cn.cyberpeace.mp.collection.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhProbeCommon {

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("输出数据")
    private String rawOutPut;

    @ApiModelProperty("探针id")
    private String probeId;
}
