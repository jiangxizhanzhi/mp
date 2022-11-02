package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/2
 */
@Getter
@Setter
public class GetTestStateData implements HttpData {
    private Integer runid;
    private String rtsgroup;
    private Integer numSeconds;
    private Integer numDataPoints;
}
