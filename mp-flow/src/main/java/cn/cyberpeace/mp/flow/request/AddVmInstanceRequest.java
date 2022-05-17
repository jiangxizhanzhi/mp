package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowNetworkInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 新增虚拟机请求
 * @Author qiaokang
 * @Date：2021/8/23 10:36
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
