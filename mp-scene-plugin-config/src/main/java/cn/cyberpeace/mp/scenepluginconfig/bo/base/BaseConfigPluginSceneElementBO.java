package cn.cyberpeace.mp.scenepluginconfig.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.scenepluginconfig.bo.Custom;
import cn.cyberpeace.mp.scenepluginconfig.bo.XdConfig;
import cn.cyberpeace.mp.scenepluginconfig.model.base.BaseConfigPluginSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class BaseConfigPluginSceneElementBO<M extends BaseConfigPluginSceneElementModel,
        T extends BaseConfigPluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>>
        extends BasePluginSceneElementBO<M, T, S> {
    private String

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseConfigPluginSceneElementModel::getConfigs),
                BeanUtils.getBeanGetterFieldName(BaseConfigPluginSceneElementModel::getCustom),
                BeanUtils.getBeanGetterFieldName(BaseConfigPluginSceneElementModel::getTimeConfig),
                BeanUtils.getBeanGetterFieldName(BaseConfigPluginSceneElementModel::getXdConfig)));
        config = StringUtils.isEmpty(obj.getConfigs()) ? new HashMap<>() : JsonCommon.parse(obj.getConfigs(), Map.class);
        custom = StringUtils.isEmpty(obj.getCustom()) ? new Custom() : JsonCommon.parse(obj.getCustom(), Custom.class);
        timeConfig = StringUtils.isEmpty(obj.getTimeConfig()) ? new HashMap<>() : JsonCommon.parse(obj.getTimeConfig(), Map.class);
        xdConfig = StringUtils.isEmpty(obj.getXdConfig()) ? new ArrayList<>() : JsonCommon.parse(obj.getXdConfig(),
                new TypeReference<List<XdConfig>>() {
                });
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseConfigPluginSceneElementModel::getConfigs),
                BeanUtils.getBeanGetterFieldName(BaseConfigPluginSceneElementModel::getCustom)));
        if (config != null) {
            obj.setConfigs(JsonCommon.stringify(config));
        }
        if (custom != null) {
            obj.setCustom(JsonCommon.stringify(custom));
        }
        if (timeConfig != null) {
            obj.setTimeConfig(JsonCommon.stringify(timeConfig));
        }
        if (xdConfig != null) {
            obj.setXdConfig(JsonCommon.stringify(xdConfig));
        }
        return obj;
    }
}
