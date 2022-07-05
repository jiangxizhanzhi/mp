package cn.cyberpeace.mp.situationaware.model;

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
public class WazuhInternetSpeedModel {

    @JSONField(name = "DownLoadSpeed")
    @ApiModelProperty("下载速度")
    private String downloadspeed;

    @JSONField(name = "UploadSpeed")
    @ApiModelProperty("上传速度")
    private String uploadSpeed;

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;

}
