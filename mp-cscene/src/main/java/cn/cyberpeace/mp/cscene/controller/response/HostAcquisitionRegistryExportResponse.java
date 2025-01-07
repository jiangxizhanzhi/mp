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
public class HostAcquisitionRegistryExportResponse {

    @ExcelProperty("主机名称")
    @ApiModelProperty("主机名称")
    private String nodeName;

    @ExcelProperty("注册表名称")
    @ApiModelProperty("注册表名称")
    private String path;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ExcelProperty("动作")
    @ApiModelProperty("动作")
    private String eventDesc;

    @ExcelProperty("修改前")
    @ApiModelProperty("修改前")
    private String md5Before;

    @ExcelProperty("修改后")
    @ApiModelProperty("修改后")
    private String md5After;

    @ExcelProperty("级别")
    @ApiModelProperty("级别")
    private Integer level;

}
