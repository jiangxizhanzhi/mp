package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_snapshot")
public class CsceneSnapshotModel extends BaseModel {
    @DefaultStr
    private String csceneId;
    @DefaultStr
    private String csceneInstanceId;
    @DefaultStr
    private String nodeId;
    @DefaultStr
    private String snapshotId;
    @DefaultStr
    private String snapshotResourceId;
    @DefaultStr
    private String name;
    @DefaultStr
    private String sourceName;
}
