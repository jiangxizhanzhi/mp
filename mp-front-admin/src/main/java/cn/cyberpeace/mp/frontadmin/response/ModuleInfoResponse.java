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

    @ApiModelProperty(value = "模块是否激活 1:是 0:否")
    private Integer isActived;

}
