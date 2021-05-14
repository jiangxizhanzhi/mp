package cn.cyberpeace.mp.scene.core.vo;

import cn.cyberpeace.mp.scene.core.vo.base.ModelVO;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import lombok.Data;

import java.util.Date;

@Data
public class SceneElementSnapshotVO extends ModelVO<SceneElementSnapshotModel> {
    private String id;
    private String name;
    private String description;
    private String address;
    private Date createTime;
    private Date updateTime;

    @Override
    public SceneElementSnapshotModel createModel() {
        return new SceneElementSnapshotModel();
    }

}
