package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhProbeCommonModel {

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    @ApiModelProperty("macId")
    private List<String> macIds;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("输出数据")
    private String rawOutPut;

    @ApiModelProperty("探针id")
    private String probeId;
}
