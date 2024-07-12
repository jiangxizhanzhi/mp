 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCollectionPluginSceneElementBO.java.
 // File size 2108.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.bo.BasePluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BaseCollectionPluginSceneElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePluginSceneElementBO
 */
@Data
public abstract class BaseCollectionPluginSceneElementBO<M extends BaseCollectionPluginSceneElementModel,
        T extends BaseCollectionPluginSceneElementBO<M, T, S>, S extends BaseSceneElementBO<?, ?, S>>
        extends BasePluginSceneElementBO<M, T, S> {

    @ApiModelProperty(value = "是否开启探针采集")
    private Boolean enableProbeCollect;
    @ApiModelProperty(value = "探针列表")
    @Valid
    private List<Probe> probes;

    @Override
    public void fromModel(M obj, String... ignoreFields) {
        super.fromModel(obj, ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCollectionPluginSceneElementModel::getProbes)));
        probes = StringUtils.isEmpty(obj.getProbes()) ? new ArrayList<>()
                : JsonCommon.parse(obj.getProbes(), new TypeReference<List<Probe>>(){});
    }

    @Override
    public M toModel(String... ignoreFields) {
        M obj = super.toModel(ArrayUtils.addAll(ignoreFields,
                BeanUtils.getBeanGetterFieldName(BaseCollectionPluginSceneElementModel::getProbes)));
        if (probes != null) {
            obj.setProbes(JsonCommon.stringify(probes));
        }
        return obj;
    }
}
