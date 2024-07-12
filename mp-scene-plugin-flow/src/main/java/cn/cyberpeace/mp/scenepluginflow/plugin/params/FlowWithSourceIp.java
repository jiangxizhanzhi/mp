 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowWithSourceIp.java.
 // File size 234.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.plugin.params;

import cn.cyberpeace.mp.scenepluginflow.bo.base.Flow;
import lombok.Data;

@Data
public class FlowWithSourceIp {
    private Flow flow;
    private String sourceIp;
}
