package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
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
    @ApiModelProperty(value = "场景模板-多靶场相关类型")
    private Integer multiSceneType;
    @ApiModelProperty(value = "联管中心中该拓扑的对应拓扑id")
    private String manageCenterSceneId;
}
