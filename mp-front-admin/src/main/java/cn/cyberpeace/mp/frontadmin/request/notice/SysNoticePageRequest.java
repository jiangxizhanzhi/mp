 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticePageRequest.java.
 // File size 881.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.request.notice;

import cn.cyberpeace.mp.frontadmin.request.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 公告分页请求信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysNoticePageRequest extends PageRequest {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "组织ids")
    private List<String> orgIds;

    //@ApiModelProperty(value = "用户ids")
    //private List<String> userIds;

    @ApiModelProperty(value = "角色ids")
    private List<String> roleIds;

    @ApiModelProperty(value = "公告id列表")
    private String[] ids;

}
