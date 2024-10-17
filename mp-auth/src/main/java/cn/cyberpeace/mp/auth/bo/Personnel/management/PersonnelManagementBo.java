package cn.cyberpeace.mp.auth.bo.Personnel.management;


import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.role.RoleBo;
import cn.cyberpeace.mp.auth.model.RoleModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonnelManagementBo {
    private String id;
    private String username;
    private String name;

    private String userId;
    private List<OrgBO> orgs;
    private List<RoleModel> roles;



}
