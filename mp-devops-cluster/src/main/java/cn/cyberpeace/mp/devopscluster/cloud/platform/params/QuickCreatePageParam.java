 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QuickCreatePageParam.java.
 // File size 607.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

/**
 * @ClassName QuickCreatePageParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpParam
 */
@Data
public class QuickCreatePageParam implements SnakeHttpParam {
    private String imageType;
    private String language;
    private String systemArchitecture;
    private String systemType;
    private String flavor;
    private String name;
    private Integer limit;
    private Integer offset;
}
