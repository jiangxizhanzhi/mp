 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkFlowRateResult.java.
 // File size 623.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName NetworkFlowRateResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class NetworkFlowRateResult implements CloudHttpData {

    /**
     * 速率值列表
     */
    private List<Long> sumRateList;

    /**
     * 时间间隔
     */
    private Integer timeInt;

    /**
     * 单位
     */
    private String unit;

}
