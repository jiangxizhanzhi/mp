 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ServiceRestartStatusDto.java.
 // File size 465.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName ServiceRestartStatusDto
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/7/29 13:57
 * @Version 1.0.0
 */
@Data
@Builder
public class ServiceRestartStatusDto {

    // 用于判断查询到的采集状态是否是过期状态
    private LocalDateTime restartTime;

    private String status;

}
