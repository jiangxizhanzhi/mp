 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowClassificationModel.java.
 // File size 553.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流量分类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_situation_flow_classification")
public class FlowClassificationModel extends BaseModel {

    private String name;

    private String categoryEn;

    private String categoryZh;

    private Integer severity;
}
