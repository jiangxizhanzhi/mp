 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventTimeCount.java.
 // File size 559.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ClassName EventTimeCount
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/3/12 17:03
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTimeCount {

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("总数")
    private Long count;
}
