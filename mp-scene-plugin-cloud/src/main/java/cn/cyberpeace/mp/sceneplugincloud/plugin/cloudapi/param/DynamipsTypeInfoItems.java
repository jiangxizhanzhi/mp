 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamipsTypeInfoItems.java.
 // File size 437.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName DynamipsTypeInfoItems
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpParam
 */
@Data
public class DynamipsTypeInfoItems implements SnakeHttpParam {

    private Map<String, Object> items;

}
