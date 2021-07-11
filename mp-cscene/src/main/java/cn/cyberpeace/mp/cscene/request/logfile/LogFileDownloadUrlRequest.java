package cn.cyberpeace.mp.cscene.request.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class LogFileDownloadUrlRequest {

    @ApiModelProperty(value = "探针名称")
    private String agentName;

    @ApiModelProperty(value = "系统类型")
    private String osType;

    @ApiModelProperty(value = "路径")
    private List<String> paths;

}
