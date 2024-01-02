package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("批量删除运行日志请求")
@Data
public class LogFilesDeleteRequest {
    @ApiModelProperty(value = "运行日志列表")
    private LogFileDeleteRequest[] logFiles;
}
