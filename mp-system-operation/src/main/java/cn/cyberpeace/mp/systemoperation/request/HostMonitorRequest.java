package cn.cyberpeace.mp.systemoperation.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 节点监控请求
 * @Autho qiaokang
 * @Date：2021/7/9 09:23
 */
@Data
@ApiModel("节点监控请求")
public class HostMonitorRequest extends SceneBaseRequest {

    @ApiModelProperty("场景id")
    private String sceneId;


}
