 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTrendRequest.java.
 // File size 1243.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FlowTrendRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 17:58
 * @Version 1.0.0
 * @see BaseSceneElementRequest
 */
@Data
public class FlowTrendRequest extends BaseSceneElementRequest {

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("分段数")
    private Integer segmentCount;

    /**
     * 源IP集合
     */
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

    @ApiModelProperty("聚合类型，0：统计时间窗口流量大小，1：统计时间窗口流量次数，默认：0")
    private Integer type = NumberUtils.INTEGER_ZERO;



}
