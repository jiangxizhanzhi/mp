 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VlanRangeResult.java.
 // File size 513.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName VlanRangeResult
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VlanRangeResult {
    private Boolean status;
    private String message;
    private Boolean success;
    private String requestId;
}
