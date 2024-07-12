 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneElementSnapshotBO.java.
 // File size 1239.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName SceneElementSnapshotBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ModelBO
 */
@Data
public class SceneElementSnapshotBO extends ModelBO<SceneElementSnapshotModel, SceneElementSnapshotBO> {
    @ApiModelProperty(value = "快照id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "源快照名称")
    private String sourceName;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "快照地址")
    private String address;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @Override
    public SceneElementSnapshotModel createModel() {
        return new SceneElementSnapshotModel();
    }

}
