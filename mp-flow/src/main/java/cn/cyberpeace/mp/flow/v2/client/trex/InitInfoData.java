package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/24
 */
@Getter
@Setter
public class InitInfoData implements HttpData {
    private String mac;
    private String taskId;
}
