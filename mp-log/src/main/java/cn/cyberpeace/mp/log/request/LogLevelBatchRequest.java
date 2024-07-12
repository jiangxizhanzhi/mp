 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogLevelBatchRequest.java.
 // File size 738.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.bo.LogLevelBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName LogLevelBatchRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelBatchRequest implements Serializable {

    private static final long serialVersionUID = -5162943967528676332L;

    @ApiModelProperty(value = "日志")
    private List<LogLevelBO> logLevelList;


}
