package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.model.base.BaseSceneNodeModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseSceneNodeBO<M extends BaseSceneNodeModel, T extends BaseSceneNodeBO<M, T>>
        extends EidBO<M, T> {
    @ApiModelProperty(value = "节点id")
    private String id;
    @ApiModelProperty(value = "所属区域id")
    private String area;
    @ApiModelProperty(value = "2d图标地址")
    private String icon2;
    @ApiModelProperty(value = "2d坐标")
    private String position2;
    @ApiModelProperty(value = "3d图标地址")
    private String icon3;
    @ApiModelProperty(value = "3d坐标")
    private String position3;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "显示参数")
    private String displayContext;
    @ApiModelProperty(value = "外部2d坐标")
    private Map<String, String> outerPosition2;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        id = obj.getNodeId();
        outerPosition2 = StringUtils.isEmpty(obj.getOuterPosition2()) ? new HashMap<>()
                : JsonCommon.parse(obj.getOuterPosition2(), new TypeReference<Map<String, String>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ignoreFields);
        obj.setNodeId(id);
        obj.setId(this.getEid());
        if (outerPosition2 != null) {
            obj.setOuterPosition2(JsonCommon.stringify(outerPosition2));
        }
        return obj;
    }
}
