package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/24
 */
@Getter
@Setter
public class FlowResourceData implements HttpData {
    private String flowId;
    private byte[] pcapFile;

    @Override
    public boolean multipart() {
        return true;
    }

    @Override
    public Map<String, Object> toMultipart() {
        Map<String, Object> map = new HashMap<>();
        map.put(flowId, pcapFile);
        return map;
    }
}
