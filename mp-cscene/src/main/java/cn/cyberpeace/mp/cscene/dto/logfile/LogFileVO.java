 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileVO.java.
 // File size 829.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @ClassName LogFileVO
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/5/6 22:53
 * @Version 1.0.0
 * @see LogFileBasicDto
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LogFileVO extends LogFileBasicDto {

    @ApiModelProperty("大小")
    private Long size;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("修改时间")
    private LocalDateTime modifyTime;

    @ApiModelProperty("接入时间")
    private LocalDateTime accessTime;

}
