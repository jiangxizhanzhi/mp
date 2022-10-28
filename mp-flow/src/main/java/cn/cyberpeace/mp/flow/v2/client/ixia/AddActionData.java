package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/28
 */
@Data
public class AddActionData implements HttpData {
    private Integer flowid;
    private String type;
    private Integer actionid;
    private String source;
}
