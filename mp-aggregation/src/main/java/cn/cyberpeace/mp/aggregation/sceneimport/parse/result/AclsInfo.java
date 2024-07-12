 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AclsInfo.java.
 // File size 482.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName AclsInfo
 * @Description
 * @Author maojun
 * @Date 2022/3/30
 * @Version 1.0.0
 */
@Getter
@Setter
public class AclsInfo {
    private String protocol;
    private String action;
    private String direction;
    private String sourceIP;
    private String sourcePort;
    private String targetIP;
    private String targetPort;
}
