package cn.cyberpeace.mp.auth.bo.org;

import lombok.Data;

/**
 * @ClassName UserOrgMapBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/8/26 09:47
 * @Version 1.0.0
 */
@Data
public class UserOrgMapBo {

    private String userId;

    private String orgIds;

    private String orgNames;

    private String orgPaths;

}
