 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkPageRequest.java.
 // File size 738.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName NetworkPageRequest
 * @Description 网络分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageRequest
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NetworkPageRequest extends BasePageRequest implements Serializable {

    /**
     * 网络名称
     */
    @ApiModelProperty(value = "网络名称")
    private String name;

}
