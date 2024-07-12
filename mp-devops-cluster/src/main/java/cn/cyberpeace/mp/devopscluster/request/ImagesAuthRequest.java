 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImagesAuthRequest.java.
 // File size 1022.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName ImagesAuthRequest
 * @Description 镜像授权请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesAuthRequest extends BaseRequest {

    /**
     * 镜像ID
     */
    @ApiModelProperty("镜像UUID集合")
    @NotEmpty
    private List<String> uuids;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID集合")
    private List<String> userIds;

    /**
     * 是否公开
     */
    @ApiModelProperty("是否公开")
    private PublicEnum isAllPublic;
}
