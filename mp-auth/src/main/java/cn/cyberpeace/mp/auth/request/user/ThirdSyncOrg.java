package cn.cyberpeace.mp.auth.request.user;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdSyncOrg {

    @JSONField(name = "orgCustomUnique")
    private String orgCustomUnique;

    @JSONField(name = "orgName")
    private String orgName;

    @JSONField(name = "orgCode")
    private String orgCode;

    @JSONField(name = "parentOrgCode")
    private String parentOrgCode;

    @JSONField(name = "parentOrgName")
    private String parentOrgName;

    @JSONField(name = "parentOrgID")
    private String parentOrgID;

    @JSONField(name = "orgName")
    private Integer orderNum;

    @JSONField(name = "deptType")
    private String deptType;

    @JSONField(name = "orgFullName")
    private String orgFullName;

    @JSONField(name = "orgType")
    private String orgType;

    @JSONField(name = "actionFlag")
    private Integer actionFlag;
}
