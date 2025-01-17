 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetRoleBo.java.
 // File size 1049.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/26 13:40
 */
@Data
public class TargetRoleBo extends TargetModel {
    public static final String QUERY_FIELD_ROLE_ID = "roleId";
    public static final String QUERY_FIELD_ID = "roleId";
    public static final String QUERY_FIELD_NAME = "name";
    public static final String QUERY_FIELD_PK = "pk";
    public static final String QUERY_FIELD_PARENT = "parent";
    public static final String QUERY_FIELD_PATH = "path";
    public static final String QUERY_FIELD_VISIBLE = "visible";
    public static final String QUERY_FIELD_TENANT = "tenant";
    public static final String QUERY_FIELD_IDENTIFIER = "identifier";
    public static final String QUERY_FIELD_TYPE = "type";
    public static final String QUERY_FIELD_CREATE_TIME = "createTime";
    public static final String QUERY_FIELD_UPDATE_TIME = "updateTime";

    private String roleId;

}
