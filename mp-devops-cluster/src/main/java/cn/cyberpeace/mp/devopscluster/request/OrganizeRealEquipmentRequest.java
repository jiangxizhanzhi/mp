package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/27
 */
@Data
public class OrganizeRealEquipmentRequest {
    @ApiModelProperty(value = "要分配的实体设备的uuid")
    private String uuid;
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty(value = "进行的操作: 1 分配, 2 回收")
    private Integer operate;
}
