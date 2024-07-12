 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePluginSceneElementPortBO.java.
 // File size 928.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementPortModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @ClassName BasePluginSceneElementPortBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ModelBO
 */
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
