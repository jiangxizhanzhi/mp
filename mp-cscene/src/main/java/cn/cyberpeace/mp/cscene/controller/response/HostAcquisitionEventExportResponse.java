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
public class HostAcquisitionEventExportResponse {

    @ExcelProperty("主机名称")
    @ApiModelProperty("主机名称")
    private String nodeName;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ExcelProperty("应用名")
    @ApiModelProperty(value = "应用名")
    private String appName;

    @ExcelProperty("事件名称")
    @ApiModelProperty("事件名称")
    private String eventDesc;

//    @ExcelProperty("级别")
    @ApiModelProperty("级别")
    private Integer level;

    @ExcelProperty("级别")
    @ApiModelProperty("级别")
    private String levelName;

    @ExcelProperty("规则描述")
    @ApiModelProperty("规则描述")
    private String description;

}
