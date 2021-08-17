package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.scene.core.bo.base.ModelBO;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SceneElementSnapshotBO extends ModelBO<SceneElementSnapshotModel> {
    private String id;
    private String name;
    private String description;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Override
    public SceneElementSnapshotModel createModel() {
        return new SceneElementSnapshotModel();
    }

}
