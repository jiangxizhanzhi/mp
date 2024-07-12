 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationStatus.java.
 // File size 404.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ActivationStatus
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/10/26 14:22
 * @Version 1.0.0
 */
@Data
public class ActivationStatus {

    @ApiModelProperty(value = "是否是激活状态 1:是 0:否")
    private Integer isActived;

}
