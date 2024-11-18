package cn.cyberpeace.mp.auth.request.team;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import lombok.Data;

@Data
public class LinkManagementRequestPage extends PageRequest {

    private String name;

    private String teamName;

    private String roomId;
}
