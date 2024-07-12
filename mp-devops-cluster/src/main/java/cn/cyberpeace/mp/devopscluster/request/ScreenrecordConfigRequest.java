 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenrecordConfigRequest.java.
 // File size 595.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ScreenrecordConfigRequest
 * @Description
 * @Author qiaokang
 * @Date 2021/9/14 10:50
 * @Version 1.0.0
 */
@Data
@ApiModel("录屏空间自动清除配置")
public class ScreenrecordConfigRequest {
    @ApiModelProperty("存储总空间上限(单位G)")
    private Integer fileLimitSpace;

    @ApiModelProperty("单个文件存储时间上限(单位月)")
    private Integer fileLimitTime;
}
