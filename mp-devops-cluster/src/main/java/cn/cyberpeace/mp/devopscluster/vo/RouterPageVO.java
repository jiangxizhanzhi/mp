 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RouterPageVO.java.
 // File size 884.
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
 * @ClassName RouterPageVO
 * @Description 路由分页返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SceneInfoVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "路由分页返回信息")
public class RouterPageVO extends SceneInfoVO {

    /**
     * 网络名称
     */
    @ApiModelProperty("网络名称")
    private String name;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createdAt;

}
