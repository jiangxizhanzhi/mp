package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseSceneBO<M extends BaseSceneModel, T extends BaseSceneBO<M, T>> extends EidBO<M, T> {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "场景文件")
    private String file;
    @ApiModelProperty(value = "显示上下文")
    private String displayContext;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "是否是分靶场部分拓扑")
    private boolean partScene = false;
    @ApiModelProperty(value = "合肥电院定制字段，vpn客户端需要暴露的局域网段，默认是192.168.1.0/24")
    private String clientLan;
    @ApiModelProperty(value = "第三方靶场启动的vpnServer端地址")
    private String serverVpnIp;
}
