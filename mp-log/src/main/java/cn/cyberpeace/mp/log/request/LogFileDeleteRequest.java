package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
@ApiModel("运行日志删除请求参数")
@Data
@NoArgsConstructor
public class LogFileDeleteRequest  {

    @NotEmpty
    @ApiModelProperty(value = "日志类型", required = true)
    private LogReportTypeEnum log_report_type_enum;

    @NotEmpty
    @ApiModelProperty(value = "日志文件名称", required = true)
    private String log_name;

    @ApiModelProperty(value = "节点名称(云平台日志)")
    private String hostName;

    @ApiModelProperty(value = "日志路径(云平台日志)")
    private String path;
}