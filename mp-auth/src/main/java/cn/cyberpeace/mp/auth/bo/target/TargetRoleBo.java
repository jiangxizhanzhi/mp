package cn.cyberpeace.mp.auth.bo.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import lombok.Data;


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
