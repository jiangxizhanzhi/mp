package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class LogFileDeleteRequest  {

    @ApiModelProperty(value = "日志类型")
    private LogReportTypeEnum log_report_type_enum;

    @ApiModelProperty(value = "日志文件名称")
    private String log_name;

    @ApiModelProperty(value = "节点名称")
    private String hostName;

    @ApiModelProperty(value = "日志路径")
    private String path;
}