 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysContext.java.
 // File size 295.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.context;

import lombok.Data;

import java.util.Map;

@Data
public class SysContext {

    /**
     * 调用链路id
     */
    private String callChainId;

    /**
     * 透传内容
     */
    private Map<String, Object> passThroughMap;

}
