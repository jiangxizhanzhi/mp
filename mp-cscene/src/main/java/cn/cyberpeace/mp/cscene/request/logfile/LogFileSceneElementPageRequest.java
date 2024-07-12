 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogFileSceneElementPageRequest.java.
 // File size 629.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.logfile;

import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName LogFileSceneElementPageRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/5/7 10:04
 * @Version 1.0.0
 * @see BaseSceneElementPageRequest
 */
@Data
public class LogFileSceneElementPageRequest extends BaseSceneElementPageRequest {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
