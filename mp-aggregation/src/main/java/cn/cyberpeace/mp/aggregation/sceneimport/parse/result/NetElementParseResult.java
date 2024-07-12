 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetElementParseResult.java.
 // File size 402.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName NetElementParseResult
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 * @Version 1.0.0
 */
@Getter
@Setter
public class NetElementParseResult {
    private String id;
    private String ip;
    private List<AclsInfo> acls;
}