package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;


/**
 * @Description: 流量执行请求对象
 * @Autho qiaokang
 * @Date：2021/6/17 09:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowExecuteRequest extends FlowBaseModel {

    @ApiModelProperty("流量文件地址")
    @NotBlank(message = "流量文件地址不能为空")
    private String flowFileUrl;

    @ApiModelProperty("流量生成器地址")
    @NotBlank(message = "流量生成器地址不能为空")
    private String flowGeneratorUrl;

    @ApiModelProperty("源ip")
    private String originIp;

    @ApiModelProperty("源端口")
    private Integer originPort;

    @ApiModelProperty("源mac地址")
    private String originMac;

    @ApiModelProperty("目标ip")
    private String targetIp;

    @ApiModelProperty("目标端口")
    private Integer targetPort;

    @ApiModelProperty("目标mac地址")
    private String targetMac;

}
