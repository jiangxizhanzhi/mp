 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePluginSceneElementBO.java.
 // File size 906.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.plugin.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @ClassName BasePluginSceneElementBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ModelBO
 */
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
