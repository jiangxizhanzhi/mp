package cn.cyberpeace.mp.front.admin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 激活响应
 * @Author zhouxinghuai
 * @Date：2021/09/07 11:13
 */
@Data
public class ActivationResponse extends ActivationRequest {

    @ApiModelProperty(value = "过期日期")
    private String expiredDate;

    @ApiModelProperty(value = "是否是激活状态 1:是 0:否")
    private Integer isActived;

}
