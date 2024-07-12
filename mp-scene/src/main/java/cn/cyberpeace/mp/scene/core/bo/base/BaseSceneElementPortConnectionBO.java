 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneElementPortConnectionBO.java.
 // File size 2750.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BaseSceneElementPortConnectionBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see EidBO
 */
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
    @ApiModelProperty(value = "线型配置")
    private String type;
    @ApiModelProperty(value = "带宽")
    private Integer bandwidth;
    @ApiModelProperty(value = "延迟")
    private Integer delay;
    @ApiModelProperty(value = "丢包率")
    private Integer loss;
    @ApiModelProperty(value = "路径点")
    private List<Map<String, Object>> vertices;
    /**
     * vlan号
     */
    @ApiModelProperty(value = "不同子靶场连接连线的vlanNumber")
    private Integer vlanNumber;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields, BeanUtils.getBeanGetterFieldName(BaseSceneElementPortConnectionModel::getVertices)));
        setId(obj.getConnectionId());
        setSource(obj.getSourcePortId());
        setTarget(obj.getTargetPortId());
        vertices = StringUtils.isEmpty(obj.getVertices()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getVertices(), new TypeReference<List<Map<String,Object>>>() {
        });
    }

    @Override
    public M toModel(String... ignoreFields) {
        M obj = super.toModel(ignoreFields);
        obj.setConnectionId(id);
        obj.setSourcePortId(source);
        obj.setTargetPortId(target);
        obj.setVertices(JsonCommon.stringify(vertices == null ? new ArrayList<>() : vertices));
        return obj;
    }
}
