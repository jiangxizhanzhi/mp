package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/26
 */
@Getter
@Setter
public class QueryTaskStatusData implements HttpData {
    private List<String> taskIds;

    @Override
    public String toContent() {
        return JsonUtils.snakeStringify(this);
    }
}
