 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualNodePageParam.java.
 // File size 388.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import lombok.Data;

/**
 * @ClassName VirtualNodePageParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageParam
 */
@Data
public class VirtualNodePageParam extends BasePageParam {
    private String name;
    private String nodeType;
    private Boolean isAll;
}

