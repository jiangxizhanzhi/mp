 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AccessModeParseResult.java.
 // File size 592.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName AccessModeParseResult
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 * @Version 1.0.0
 */
@Getter
@Setter
public class AccessModeParseResult {
    private String protocol;
    private String port;
    private String path;
    private String username;
    private String password;
    private String privateKey;
    private String privateKeyPath;
    private String desc;
    private String mode;
    private String isproxy;
}
