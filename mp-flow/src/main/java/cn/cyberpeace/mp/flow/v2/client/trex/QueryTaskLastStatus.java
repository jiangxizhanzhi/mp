package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/27
 */
@Getter
@Setter
public class QueryTaskLastStatus implements HttpData {
    private String taskId;

    @Override
    public String toContent() {
        return JsonUtils.snakeStringify(this);
    }
}
