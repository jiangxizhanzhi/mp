package cn.cyberpeace.mp.auth.bo.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ThirdUser {
    private String id;
    private String username;
    private String realName;
    private String email;
    private String birthday;
    private String phoneNumber;
    private String gender;
    private String avatar;
    private String password;
    private String salt;
    private String status;
    private String homePath;
    private String deleteFlag;
    private String loginIp;
    private String loginDate;
    private String loginNum;
    private String pwdUpdateTime;
    private String remark;
    private String version;
    private Date createTime;
    private Date updateTime;
    private List<ThirdRole> roleList;
    private String perms;
    private String orgId;
    private String orgName;
    private String jobId;
    private String ip;
    private String osName;

    @Getter
    @Setter
    public static class ThirdRole {
        private String id;
        private String roleName;
        private String status;
        private String orderNo;
        private String systemId;
        private String remark;
        private String deleteFlag;
        private String version;
        private Date createTime;
        private Date updateTime;
        private String menuStr;
        private String menu;
    }

}
