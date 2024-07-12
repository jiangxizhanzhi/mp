 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Scan.java.
 // File size 620.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 14:16
 */
@Data
@ApiModel("扫描")
public class Scan {

    @ApiModelProperty(value = "扫描ID")
    private Long id;

    @ApiModelProperty(value = "扫描时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime time;

}
