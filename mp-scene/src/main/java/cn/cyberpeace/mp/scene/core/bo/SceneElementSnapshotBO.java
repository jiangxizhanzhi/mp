package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.bo.ModelBO;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SceneElementSnapshotBO extends ModelBO<SceneElementSnapshotModel, SceneElementSnapshotBO> {
    @ApiModelProperty(value = "快照id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
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
