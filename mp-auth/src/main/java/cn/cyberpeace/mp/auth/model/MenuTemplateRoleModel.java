 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuTemplateRoleModel.java.
 // File size 683.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这个target以后就当做menu
 *
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_menu_template_role")
@Builder
public class MenuTemplateRoleModel extends BaseModel {
    @DefaultStr
    private String templateId;

    @DefaultStr
    private String roleId;
}
