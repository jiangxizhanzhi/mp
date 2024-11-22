package cn.cyberpeace.mp.auth.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdSyncOrg {
    private Long idtOrgId;           // org__id
    private String orgName;          // orgName
    private Integer orderNum;        // orderNum
    private String orgFullName;      // orgFullName
    private String deptType;         // deptType
    private String parentOrgName;    // parentOrgName
    private String orgCustomUnique;  // orgCustomUnique
    private String orgType;          // orgType
    private String parentOrgId;        // parentOrgID
    private String orgCode;          // orgCode
    private String hrOldMainkey;     // hrOldMainkey
    private String hrMainKey;        // hrMainKey
    private String hrOldPrimaykey;   // hrOldPrimaykey
    private String hrParentMainkey;  // hrParentMainkey
    private String hrOldParentMainkey;  // hrOldParentMainkey
    private String hrOldParentPrimaykey; // hrOldParentPrimaykey
    private Long requestLogId;       // request_log__id
    private String requestLogActionDesc; // request_log__action_desc
    private Integer actionFlag;      // actionFlag
}
