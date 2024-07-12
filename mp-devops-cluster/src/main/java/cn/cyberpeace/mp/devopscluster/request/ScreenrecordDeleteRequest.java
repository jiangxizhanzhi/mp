 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenrecordDeleteRequest.java.
 // File size 716.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName ScreenrecordDeleteRequest
 * @Description 按时间删除录屏请求
 * @Author qiaokang
 * @Date 2021/9/14 10:50
 * @Version 1.0.0
 */
@Data
@ApiModel("按时间删除录屏请求")
public class ScreenrecordDeleteRequest {

    /**
     * 删除条件：1: 6个月之前，2：3个月之前
     */
    @ApiModelProperty("删除条件：1: 6个月之前，2：3个月之前")
    @NotNull(message = "recordTime can not be empty")
    private Integer recordTime;

}
