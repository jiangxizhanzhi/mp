 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPortProtocolModel.java.
 // File size 488.
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
 * 流量端口-协议
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_situation_flow_port_protocol")
public class FlowPortProtocolModel extends BaseModel {

    private String port;

    private String protocol;
}
