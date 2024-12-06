package cn.cyberpeace.mp.auth.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdSyncUserOrganization {
    private String orgID;
    private String orgCustomUnique;   // 组织唯一标识
    private String orgName;           // 组织名称
    private String orgCode;           // 组织code
    private Integer orgSortNum;       // 用户所属组织排序
    private Integer sortNum;          // 用户兼职部门排序
    private String hrMainkey;         // 组织二期主键
    private String hrOldPrimaykey;    // 组织一期唯一标识
    private String hrOldMainkey;      // 组织一期主键
    private String partTimeJob;       // 所属岗位
    private String unitCode;          // 所属单位编码
    private String unitName;          // 所属单位名称
    private String unitID;            // 所属单位二期主键
    private String unitOldCustom;     // 所属单位一期唯一标识
    private String unitOldID;         // 所属单位一期主键
}
