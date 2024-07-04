package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 拓扑中获取载荷列表的请求参数格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "载荷列表的请求参数格式")
public class LoadRequestVO {

    @ApiModelProperty(value = "运行平台")
    private String runTable;

    @ApiModelProperty(value = "控制类型")
    private String controlType;

    @ApiModelProperty(value = "来源(1载荷平台 2本地)")
    private String source;

    @ApiModelProperty(value = "载荷名称")
    private String toolName;
}
