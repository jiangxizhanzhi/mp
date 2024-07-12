 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenRecordRestoreConfigVO.java.
 // File size 745.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ScreenRecordRestoreConfigVO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "录屏空间自动清除配置")
@Builder
public class ScreenRecordRestoreConfigVO {

    @ApiModelProperty("存储总空间上限(单位G)")
    private Integer fileLimitSpace;

    @ApiModelProperty("单个文件存储时间上限(单位月)")
    private Integer fileLimitTime;

}
