package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public abstract class BasePluginSceneElementPortBO<M extends BasePluginSceneElementPortModel,
        T extends BasePluginSceneElementPortBO<M, T, S>, S extends BaseSceneElementPortBO<?, S, ?>> extends ModelBO<M, T> {
    @JsonIgnore
    private S srcPort;

    @Override
    public M toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields, "srcPort"));
    }
}
