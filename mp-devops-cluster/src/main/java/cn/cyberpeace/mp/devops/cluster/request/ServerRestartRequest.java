package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 服务重启请求
 * @Author qiaokang
 * @Date：2021/9/14 10:50
 */
@Data
@ApiModel("服务重启请求")
public class ServerRestartRequest {

    @ApiModelProperty("节点名称")
    @NotNull(message = "hostname can not be empty")
    private String hostname;

    @ApiModelProperty("服务名称集合")
    @NotNull(message = "service name can not be empty")
    private List<String> serviceList;

}
