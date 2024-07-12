 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkFlowRateData.java.
 // File size 553.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName NetworkFlowRateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class NetworkFlowRateData implements CloudHttpData {

    /**
     * 端口列表
     */
    private List<String> ports;

    /**
     * 统计时间，单位S
     */
    private Long time;
}
