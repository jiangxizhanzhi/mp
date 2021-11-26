package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public abstract class BaseSceneElementPortConnectionBO<M extends BaseSceneElementPortConnectionModel,
        T extends BaseSceneElementPortConnectionBO<M, T>> extends EidBO<M, T> {
    @ApiModelProperty(value = "连接id")
    private String id;
    @ApiModelProperty(value = "源端口id")
    private String source;
    @ApiModelProperty(value = "目标端口id")
    private String target;
    @ApiModelProperty(value = "连接名称")
    private String name;
    @ApiModelProperty(value = "是否单向连接")
    private Boolean unidirectional;
    @ApiModelProperty(value = "显示参数")
    private String displayContext;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        setId(obj.getConnectionId());
        setSource(obj.getSourcePortId());
        setTarget(obj.getTargetPortId());
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        obj.setConnectionId(id);
        obj.setSourcePortId(source);
        obj.setTargetPortId(target);
        return obj;
    }
}
