 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCsceneRoleModel.java.
 // File size 742.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * @ClassName BaseCsceneRoleModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class BaseCsceneRoleModel extends BaseModel {
    @DefaultStr
    private String cscene;
    @DefaultStr
    private String parent;
    @DefaultStr
    private String key;
    @DefaultStr
    private String name;
    @DefaultStr
    private String type;
    @DefaultStr
    private String assetNodes;
    @DefaultStr
    private String visibleNodes;
    @DefaultStr
    private String users;
}
