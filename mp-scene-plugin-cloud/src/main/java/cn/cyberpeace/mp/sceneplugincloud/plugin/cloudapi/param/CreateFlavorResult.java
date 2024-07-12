 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CreateFlavorResult.java.
 // File size 533.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName CreateFlavorResult
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFlavorResult {
    private String id;
    private String name;
    private Integer ram;
    private Integer cpu;
    private Integer disk;
}


