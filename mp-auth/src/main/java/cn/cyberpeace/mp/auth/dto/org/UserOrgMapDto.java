package cn.cyberpeace.mp.auth.dto.org;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/26 09:47
 */
@Data
public class UserOrgMapDto {

    private String userId;

    private String orgIds;

    private String orgNames;

    private String orgPaths;

}
