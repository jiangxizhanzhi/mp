package cn.cyberpeace.mp.scenepluginflow.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;

/**
 * 基础流程插件场景元素模型类。
 * 该类继承自BasePluginSceneElementModel，用于表示流程插件场景中的元素。
 * 其中，flows属性用于存储流程定义的信息。
 */
@Data
public class BaseFlowPluginSceneElementModel extends BasePluginSceneElementModel {
    /**
     * 流程定义信息。
     * 使用@DefaultStr注解，表示该字段具有默认值。
     * 这里的默认值用于确保即使在flows字段未被明确赋值的情况下，也能保证其不为空。
     */
    @DefaultStr
    private String flows;
}
