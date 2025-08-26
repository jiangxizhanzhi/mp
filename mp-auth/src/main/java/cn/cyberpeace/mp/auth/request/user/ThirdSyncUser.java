package cn.cyberpeace.mp.auth.request.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ThirdSyncUser {
    // 基本信息
    private String app_account__id ;
    private String app_account__account_no ;
    private String app_account__account_name ;
    private String accountNo;         // IT账号
    private String userUid;           // 员工号
    private String userName;          // 姓名
    private Integer gender;           // 性别：0男，1女
    private String mobile;            // 手机号码
    private String email;             // 电子邮箱
    private List<ThirdSyncUserType> userTypes; // 用户类型列表
    private List<ThirdSyncUserOrganization> orgs;  // 用户所属组织列表
    private List<ThirdSyncRole> roles;
    private String wesisCustomUnique; // WeSIS用户唯一标识
    private String newCustomUnique;   // 二期用户唯一标识
    private String identityType;      // 证件类型
    private String identityCode;      // 证件编码 (SM4加密)
    private String staffCountryCode;  // 国籍编码
    private String staffTechpostTitle;// 职称
    private String staffTechLevel;    // 技能等级
    private String staffTypeCode;     // 员工用工类型
    private String staffStatusCode;   // 员工岗位状态
    private String staffJobGrade;     // 职级
    private Integer actionFlag;       //
}
