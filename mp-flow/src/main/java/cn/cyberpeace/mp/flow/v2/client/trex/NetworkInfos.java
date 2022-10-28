package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/25
 */
@Getter
@Setter
public class NetworkInfos implements HttpData {
    private List<InitInfoData> networkInfos = new ArrayList<>();

    @Getter
    @Setter
    public static class InitInfoData {
        private String mac;
        private String taskId;
    }

    @Override
    public String toContent() {
        return JsonUtils.snakeStringify(this);
    }
}
