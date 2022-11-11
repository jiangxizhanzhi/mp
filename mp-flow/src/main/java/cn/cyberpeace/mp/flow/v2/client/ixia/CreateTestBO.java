package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/11
 */
@Getter
@Setter
public class CreateTestBO {
    String neighborhood;
    String profileName;
    String strikeListName;
    Boolean strikeEnable;
    List<PcapFlow.RecreateData> recreateDatas;
    Boolean recreateEnable;
    String loadName;
    String attackProfile;
}
