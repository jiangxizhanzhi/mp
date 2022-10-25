package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
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
    private String fileName;
    private byte[] file;

    @Override
    public boolean multipart() {
        return true;
    }

    @Override
    public Map<String, Object> toMultipart() {
        Map<String, Object> map = new HashMap<>();
        map.put(flowId, file);
        return map;
    }

    @Override
    public String toContent() {
        return JsonUtils.snakeStringify(this);
    }
}
