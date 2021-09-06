package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseSceneBO<M extends BaseSceneModel, T extends BaseSceneBO<M, T>> extends EidBO<M, T> {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "场景文件")
    private String file;
}
