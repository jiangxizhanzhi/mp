package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 日志导出请求
 * @Author jpj
 * @Date：2023/12/08
 */
@Data
@ApiModel("导出日志请求")
public class LogExportRequest {

    @ApiModelProperty(value = "日志id列表")
    private String[] ids;

    @ApiModelProperty(value = "模块")
    private Integer module;

    @ApiModelProperty(value = "类型")
    private Integer type;

}
