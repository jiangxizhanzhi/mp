package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostAcquisitionSyslogExportResponse {

    @ExcelProperty("节点名称")
    @ApiModelProperty("节点名称")
    private String nodeName;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ExcelProperty("日志facilities")
    @ApiModelProperty(value = "日志facilities")
    private String appName;

    @ExcelProperty("日志描述")
    @ApiModelProperty("日志描述")
    private String eventDesc;

    @ExcelProperty("日志级别")
    @ApiModelProperty("日志级别")
    private Integer levelName;

}
