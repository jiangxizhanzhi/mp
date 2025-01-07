package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ling_yu
 * @Date 2024/12/31
 */
@Data
public class HostBasicInfoExportResponse {

    @ExcelProperty("机器名称")
    @ApiModelProperty(value = "机器名称")
    private String name;

    @ExcelProperty("OS架构")
    @ApiModelProperty(value = "OS架构")
    private String architecture;

    @ExcelProperty("OS")
    @ApiModelProperty(value = "OS")
    private String systemTypeName;

    @ExcelProperty("系统版本")
    @ApiModelProperty(value = "系统版本")
    private String version;

    @ExcelProperty("IP地址")
    @ApiModelProperty(value = "IP地址")
    private String ip;

}
