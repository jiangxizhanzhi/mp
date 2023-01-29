package cn.cyberpeace.mp.authbase.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;

import java.io.Serializable;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/1/16 14:37
 */
public class BaseRoleResourceModel extends BaseModel implements Serializable {

    @DefaultStr("default")
    private String tenant;

    private String roleId;

    private String resourceId;

}
