package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 授权资源项
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationResourceItem {

    @ApiModelProperty(value = "资源项ID")
    private String id;

    @ApiModelProperty(value = "唯一编号")
    private String identifier;

    @ApiModelProperty(value = "产品/模块")
    private String module;

    @ApiModelProperty(value = "控制项名称")
    private String restrictName;

    @ApiModelProperty(value = "最大值")
    private String maxValue;

}
