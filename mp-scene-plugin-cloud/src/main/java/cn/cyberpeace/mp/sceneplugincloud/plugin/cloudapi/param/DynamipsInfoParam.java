 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamipsInfoParam.java.
 // File size 386.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

/**
 * @ClassName DynamipsInfoParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpParam
 */
@Data
public class DynamipsInfoParam implements SnakeHttpParam {
    private String type;
}
