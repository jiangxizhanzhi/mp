 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseFlowPluginSceneElementBO.java.
 // File size 3316.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础流程插件场景元素业务对象，是抽象类，用于定义流程插件场景元素的通用行为和属性。
 * 该类继承自BasePluginSceneElementBO，增加了对流程列表的处理。
 *
 * @param <M> 扩展自BaseFlowPluginSceneElementModel的模型类类型参数。
 * @param <T> 扩展自BaseFlowPluginSceneElementBO的业务对象类型参数，形成泛型递归，用于支持自我引用。
 * @param <S> 扩展自BaseSceneElementBO的业务对象类型参数，用于表示场景元素的通用业务操作。
 */
@Data
public abstract class BaseFlowPluginSceneElementBO<M extends BaseFlowPluginSceneElementModel,
        T extends BaseFlowPluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>>
        extends BasePluginSceneElementBO<M, T, S> {

    /**
     * 流量列表，存储了与当前场景元素相关的流量信息。
     */
    @ApiModelProperty(value = "流量列表")
    @Valid
    private List<Flow> flows;

    /**
     * 从模型对象中填充业务对象属性，忽略指定的字段。
     * 该方法重写了父类方法，增加了对flows字段的处理。
     *
     * @param obj 源模型对象。
     * @param ignoreFields 需要忽略的字段名数组。
     */
    @Override
    public void fromModel(M obj, String... ignoreFields) {
        // 调用父类方法填充属性，并添加flows字段到忽略字段列表
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseFlowPluginSceneElementModel::getFlows)));
        // 根据obj中的flows字段值，解析为Flow列表
        flows = StringUtils.isEmpty(obj.getFlows()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getFlows(), new TypeReference<List<Flow>>(){});
    }

    /**
     * 将业务对象转换为模型对象，忽略指定的字段。
     * 该方法重写了父类方法，增加了对flows字段的处理。
     *
     * @param ignoreFields 需要忽略的字段名数组。
     * @return 转换后的模型对象。
     */
    @Override
    public M toModel(String... ignoreFields) {
        // 调用父类方法转换，并添加flows字段到忽略字段列表
        M obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseFlowPluginSceneElementModel::getFlows)));
        // 如果flows不为空，将其序列化为JSON字符串，设置到obj中
        if (flows != null) {
            obj.setFlows(JsonCommon.stringify(flows));
        }
        return obj;
    }

}
