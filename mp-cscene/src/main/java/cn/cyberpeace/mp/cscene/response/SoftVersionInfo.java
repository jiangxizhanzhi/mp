 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SoftVersionInfo.java.
 // File size 275.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftVersionInfo {
    private String soft;
    private String originVersion;
    private String newVersion;
    private Boolean upgradeResult;
}
