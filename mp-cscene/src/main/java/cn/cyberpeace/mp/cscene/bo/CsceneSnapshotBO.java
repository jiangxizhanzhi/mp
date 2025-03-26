// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name CsceneInstanceBO.java.
// File size 4154.
// File create date Fri Jul 12 12:55:32 CST 2024.
// File update date Fri Jul 12 12:55:32 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName CsceneInstanceBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneBO
 */
@ApiModel(description = "场景快照")
@Data
public class CsceneSnapshotBO {
    @ApiModelProperty(value = "快照id")
    private String snapshotId;
    @ApiModelProperty(value = "场景模板id")
    private String csceneId;
    @ApiModelProperty(value = "场景实例id")
    private String csceneInstanceId;
    @ApiModelProperty(value = "场景快照名称")
    private String snapshotName;
}
