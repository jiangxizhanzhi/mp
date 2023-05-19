package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Description: 删除虚拟USB请求体
 * @Author: guquan
 * @Date：2023/4/17 17:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VirtualUsbDeleteRequest extends BaseRequest {

    @NotEmpty
    @ApiModelProperty(value = "uuid列表")
    private List<String> uuids;
}
