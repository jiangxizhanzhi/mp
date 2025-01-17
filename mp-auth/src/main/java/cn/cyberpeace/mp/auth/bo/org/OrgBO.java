 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgBO.java.
 // File size 359.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.model.OrgModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/9/1 17:20
 */
@Data
public class OrgBO extends OrgModel {

    @ApiModelProperty(value = "路径")
    private String path;

}
