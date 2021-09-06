package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseCollectionPluginSceneElementBO<M extends BaseCollectionPluginSceneElementModel,
        T extends BaseCollectionPluginSceneElementBO<M, T>> extends BasePluginSceneElementBO<M, T> {

    @ApiModelProperty(value = "探针列表")
    private List<Probe> probes;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCollectionPluginSceneElementModel::getProbes)));
        probes = StringUtils.isEmpty(obj.getProbes()) ? new ArrayList<>()
                : JsonUtils.parse(obj.getProbes(), new TypeReference<List<Probe>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        var obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCollectionPluginSceneElementModel::getProbes)));
        if (probes != null) {
            obj.setProbes(JsonUtils.toJsonString(probes));
        }
        return obj;
    }
}
