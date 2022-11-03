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
public class IpRouterData implements HttpData {
    private String id;
    private String ipAddress;
}
