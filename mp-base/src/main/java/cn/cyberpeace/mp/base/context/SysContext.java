 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysContext.java.
 // File size 407.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.context;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName SysContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
