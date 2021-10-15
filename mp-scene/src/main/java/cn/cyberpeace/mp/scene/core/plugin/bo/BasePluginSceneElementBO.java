package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

@Data
public abstract class BasePluginSceneElementBO<M extends BasePluginSceneElementModel,
        T extends BasePluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>> extends ModelBO<M, T> {
    @JsonIgnore
    private S srcElement;

    @Override
    public M toModel(String... ignoreFields) {
        return super.toModel(ArrayUtils.addAll(ignoreFields, "srcElement"));
    }
}
