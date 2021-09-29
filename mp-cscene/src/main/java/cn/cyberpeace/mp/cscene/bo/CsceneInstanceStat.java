package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel(description = "场景实例运维数据")
@Data
public class CsceneInstanceStat {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "引用类型")
    private Integer referenceType;
    @ApiModelProperty(value = "引用元数据")
    private String referenceMetaData;
    @ApiModelProperty(value = "虚拟机数量")
    private Integer vmCount;
    @ApiModelProperty(value = "容器数量")
    private Integer containerCount;
    @ApiModelProperty(value = "实装数量")
    private Integer entityCount;
    @ApiModelProperty(value = "cpu总计")
    private Integer cpu;
    @ApiModelProperty(value = "内存总计")
    private Integer ram;
    @ApiModelProperty(value = "硬盘总计")
    private Integer disk;
    @ApiModelProperty(value = "创建用户名")
    private String createUsername;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "场景实例存活时间")
    private Integer sceneTtl;
    @ApiModelProperty(value = "场景实例属性")
    private Integer sceneAttr;
    @ApiModelProperty(value = "场景事件")
    private Integer event;
    @ApiModelProperty(value = "场景事件状态")
    private Integer eventStatus;
    @ApiModelProperty(value = "场景事件信息")
    private String eventMessage;
    @ApiModelProperty(value = "启动时长(秒)")
    private Integer createConsumeSeconds;
}
