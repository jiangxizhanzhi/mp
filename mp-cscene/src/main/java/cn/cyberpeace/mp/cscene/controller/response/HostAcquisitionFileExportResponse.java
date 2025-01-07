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
public class HostAcquisitionFileExportResponse {

    @ExcelProperty("主机名称")
    @ApiModelProperty("主机名称")
    private String nodeName;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ExcelProperty("文件名称")
    @ApiModelProperty("文件名称")
    private String path;

    @ExcelProperty("动作")
    @ApiModelProperty("动作")
    private String eventDesc;

    @ExcelProperty("级别")
    @ApiModelProperty("级别")
    private Integer level;

    @ExcelProperty("文件")
    @ApiModelProperty("文件")
    private String fileSize;

}
