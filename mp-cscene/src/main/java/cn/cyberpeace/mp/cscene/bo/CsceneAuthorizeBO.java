 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneAuthorizeBO.java.
 // File size 766.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CsceneAuthorizeBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class CsceneAuthorizeBO {
    @ApiModelProperty(value = "场景模板授权类型 0:私有 1:授权所有 2:共享")
    private Integer authType;

    @ApiModelProperty(value = "场景模板授权用户列表")
    private List<SharedUser> sharedUsers;

    @Data
    public static class SharedUser {
        private String id;
        private String name;
        private String organization;
        private String organizationNameAll;
        private String username;
    }
}
