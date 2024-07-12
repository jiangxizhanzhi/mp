 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceQueryParams.java.
 // File size 669.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceQueryParams
 * @Description 查询参数定义
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CsceneInstanceQueryParams extends QueryParams {
    @ApiModelProperty(value = "场景访问用户id")
    private String accessUserId;
}
