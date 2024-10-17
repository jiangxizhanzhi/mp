package cn.cyberpeace.mp.auth.request.team;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import lombok.Data;

@Data
public class TeamPageRequest extends PageRequest {

    private String name;
}
