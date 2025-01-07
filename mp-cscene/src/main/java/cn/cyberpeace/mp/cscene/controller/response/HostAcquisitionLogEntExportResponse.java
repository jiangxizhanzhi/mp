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
public class HostAcquisitionLogEntExportResponse {

    @ExcelProperty("设备名称")
    @ApiModelProperty("设备名称")
    private String name;

    @ExcelProperty("文件名称")
    @ApiModelProperty("文件名称")
    private String path;

    @ExcelProperty("文件大小")
    @ApiModelProperty("文件大小")
    private Long size;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime createTime;

}
