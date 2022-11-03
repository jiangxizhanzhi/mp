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
public class InterfaceData implements HttpData {
    //    private String id;
//    private String macAddress;
    private Integer mtu;
//    private Boolean useVnicMacAddress;
//    private String vlanKey;
//    private Boolean duplicateMacAddress;
//    private Boolean ignorePauseFrames;
//    private Integer number;
}
