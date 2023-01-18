package cn.cyberpeace.mp.frontadmin.response.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/1/17 16:55
 */
@Data
public class ModuleInfoResponse {

    @ApiModelProperty(value = "模块名")
    private String moduleName;

    @ApiModelProperty(value = "是否激活")
    private boolean isActivated;

}
