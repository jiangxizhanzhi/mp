 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageAsyncRequest.java.
 // File size 721.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ImageAsyncRequest
 * @Description 镜像同步请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageAsyncRequest extends BaseRequest {

    @ApiModelProperty("镜像ID集合")
    private List<Integer> ids;

    @ApiModelProperty("是否隐藏")
    private Boolean isHide = Boolean.FALSE;
}
