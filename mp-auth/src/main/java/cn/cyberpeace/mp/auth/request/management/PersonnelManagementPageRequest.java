package cn.cyberpeace.mp.auth.request.management;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import lombok.Data;

@Data
public class PersonnelManagementPageRequest extends PageRequest {

    private String teamId;
}
