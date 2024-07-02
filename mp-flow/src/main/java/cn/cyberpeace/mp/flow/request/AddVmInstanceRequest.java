package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName AddVmInstanceRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel(value = "新增虚拟机请求")
public class AddVmInstanceRequest {

    /**
     * 流量网络信息
     */
    @ApiModelProperty("流量网络信息")
    @NotNull(message = "networkInfo list can not be empty")
    private List<FlowNetworkInfo> networkInfos;


}
