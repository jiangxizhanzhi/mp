package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 新增虚拟机请求
 * @Autho qiaokang
 * @Date：2021/8/23 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增虚拟机请求")
public class AddVmInstanceRequest {

    /**
     * 网络id
     */
    @ApiModelProperty(value = "网络id")
    @NotBlank(message = "networkId can not be empty")
    private String networkId;

}
