 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowMirrorCreateRes.java.
 // File size 375.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName FlowMirrorCreateRes
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@Accessors(chain = true)
public class FlowMirrorCreateRes {
    private Integer optionKey;
    private String flowMirrorId;
}
