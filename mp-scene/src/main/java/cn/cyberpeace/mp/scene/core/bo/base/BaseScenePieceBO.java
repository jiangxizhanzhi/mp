 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseScenePieceBO.java.
 // File size 555.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseScenePieceModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseScenePieceBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeBO
 */
@Data
public abstract class BaseScenePieceBO<M extends BaseScenePieceModel, T extends BaseScenePieceBO<M, T>>
        extends BaseSceneNodeBO<M, T> {
    @ApiModelProperty(value = "碎片类型")
    private String type;
}
