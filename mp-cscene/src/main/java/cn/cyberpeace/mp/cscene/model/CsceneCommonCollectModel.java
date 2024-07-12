 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneCommonCollectModel.java.
 // File size 625.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName CsceneCommonCollectModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@Table("mp_cscene_common_collect")
public class CsceneCommonCollectModel extends BaseModel {

	private String csceneId;

	private String nodeId;

	private String hostName;

	private String agreement;

	private LocalDateTime time;

	private String detail;
}
