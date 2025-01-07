package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostProcessInfoExportResponse {

    @ExcelProperty("PID")
    @ApiModelProperty(value = "PID")
    private Integer pid;

    @ExcelProperty("父进程")
    @ApiModelProperty(value = "父进程")
    private Integer ppid;

    @ExcelProperty("进程用户")
    @ApiModelProperty(value = "进程用户")
    private String euser;

    @ExcelProperty("内存使用(MB)")
    @ApiModelProperty(value = "内存使用(MB)")
    private String memorySize;

    @ExcelProperty("命令行")
    @ApiModelProperty(value = "命令行")
    private String cmd;

    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    private String state;

    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @JSONField(name = "start_time")
    private String startTime;

}
