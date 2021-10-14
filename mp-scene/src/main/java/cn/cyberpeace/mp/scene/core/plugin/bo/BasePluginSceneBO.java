package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public abstract class BasePluginSceneBO<M extends BasePluginSceneModel, T extends BasePluginSceneBO<M, T, S>,
        S extends BaseSceneBO<?, S>> extends ModelBO<M, T> {
    @JsonIgnore
    private S srcScene;

    @Override
    public M toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields, "srcScene"));
    }
}
