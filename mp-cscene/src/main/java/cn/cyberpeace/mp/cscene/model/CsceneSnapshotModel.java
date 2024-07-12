 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneSnapshotModel.java.
 // File size 1022.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneSnapshotModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
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
    @DefaultBool
    private Boolean deleted;
}
