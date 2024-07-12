 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AssetsStatisticsRequest.java.
 // File size 492.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName AssetsStatisticsRequest
 * @Description 资产统计请求
 * @Author qiaokang
 * @Date 2022/03/14 14:22
 * @Version 1.0.0
 */
@Data
public class AssetsStatisticsRequest {

    @ApiModelProperty("场景id集合")
    @NotNull
    private List<String> sceneIds;

}
