package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionHostEventBO {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty(value = "节点类型  1: 交换机, 2: 路由器, 3: 防火墙, 4: 虚拟机, 5: 容器, 6: 实体设备")
    // Ref: BaseCloudPluginSceneElementModel.VirtualType 中的枚举
    private Integer virtualType;

    @ApiModelProperty("探针id")
    private String probeId;

    @ApiModelProperty("探针名称")
    private String probeName;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("采集数据")
    private Map<String, String> probeData;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("事件描述")
    private String eventDesc;

    @ApiModelProperty("rule等级")
    private Integer level;

    @ApiModelProperty("分类")
    private String catagory;

    @ApiModelProperty("rule描述信息")
    private String description;

    @ApiModelProperty("底层信息")
    private Map<String, Object> content;



}
