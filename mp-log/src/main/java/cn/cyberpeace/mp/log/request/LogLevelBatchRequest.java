 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogLevelBatchRequest.java.
 // File size 704.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
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
 * @description 日志级别批量请求
 * @author wangssh
 * @date 2023/5/30
 * @version 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelBatchRequest implements Serializable {

    private static final long serialVersionUID = -5162943967528676332L;

    @ApiModelProperty(value = "日志")
    private List<LogLevelBO> logLevelList;


}
