package cn.cyberpeace.mp.systemoperation.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @Description: 节点监控请求
 * @Autho qiaokang
 * @Date：2021/7/9 09:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("节点监控请求")
public class HostMonitorRequest extends SceneBaseRequest {

    @ApiModelProperty(value = "场景id", required = true)
    @NotEmpty(message = "场景id不能为空")
    private String sceneId;

}
