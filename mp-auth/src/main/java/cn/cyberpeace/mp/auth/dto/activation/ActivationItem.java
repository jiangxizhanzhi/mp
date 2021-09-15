package cn.cyberpeace.mp.auth.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 授权项
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationItem {

    @ApiModelProperty(value = "授权项key")
    private String key;

    @ApiModelProperty(value = "授权项名称")
    private String name;

    @ApiModelProperty(value = "授权项值")
    private String value;

    @ApiModelProperty(value = "1:授权功能  2:授权资源")
    private Integer type;

}
