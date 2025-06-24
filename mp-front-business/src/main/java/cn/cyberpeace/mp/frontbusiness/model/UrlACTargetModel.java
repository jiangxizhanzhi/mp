 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACTargetModel.java.
 // File size 793.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_url_ac_target")
@ApiModel("接口权限关系表")
public class UrlACTargetModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String urlacId;

    private String target;

    @DefaultStr
    private String group;

}
