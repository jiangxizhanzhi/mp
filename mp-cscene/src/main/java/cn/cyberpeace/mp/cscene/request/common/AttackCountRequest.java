 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AttackCountRequest.java.
 // File size 1136.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName AttackCountRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/29 19:52
 * @Version 1.0.0
 * @see BaseSceneElementRequest
 */
@Data
public class AttackCountRequest extends BaseSceneElementRequest {

    @ApiModelProperty("ip列表参数中ip的类型 0:源ip,1:目标ip,默认为0")
    private int ipType = 0;

    @ApiModelProperty("用于查询的ip列表")
    private List<String> ips;

    @ApiModelProperty("查询top N")
    private Integer topSize;

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 源IP集合
     */
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

}
