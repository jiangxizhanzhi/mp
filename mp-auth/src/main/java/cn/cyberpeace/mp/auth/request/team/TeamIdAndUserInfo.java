package cn.cyberpeace.mp.auth.request.team;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
public class TeamIdAndUserInfo {
    //队伍id
    private String teamId;
    //对应的人员信息
    private List<UserBo> userBos = Lists.newArrayList();
}
