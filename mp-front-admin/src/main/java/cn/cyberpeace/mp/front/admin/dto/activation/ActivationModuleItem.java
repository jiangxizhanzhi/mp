package cn.cyberpeace.mp.front.admin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 授权功能项
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationModuleItem {

    @ApiModelProperty(value = "应用模块ID")
    private String id;

    @ApiModelProperty(value = "唯一编号")
    private String identifier;

    @ApiModelProperty(value = "应用模块名称")
    private String name;

}
