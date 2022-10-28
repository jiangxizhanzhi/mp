package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/28
 */
@Data
public class AddFlowData implements HttpData {

    private FlowParams flowParams;

    @Data
    public static class FlowParams {
        private String name;
        private String to;
        private String from;
    }
}
