package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/7
 */
@Getter
@Setter
public class IxiaCreatePhaseData implements HttpData {
    private String type;
    private String phaseId;
    private Integer duration;
}
