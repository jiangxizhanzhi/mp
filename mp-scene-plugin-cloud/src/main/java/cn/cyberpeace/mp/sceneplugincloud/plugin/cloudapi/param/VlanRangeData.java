 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VlanRangeData.java.
 // File size 537.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName VlanRangeData
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VlanRangeData implements CloudHttpData {
    private String vlans;
}
