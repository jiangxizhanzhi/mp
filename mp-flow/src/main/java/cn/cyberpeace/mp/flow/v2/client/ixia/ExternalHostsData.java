package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/2
 */
@Getter
@Setter
public class ExternalHostsData implements HttpData {
    private String ipAddress;
    private String tags;

    public String toContent() {
        return JsonUtils.snakeStringify(this);
    }
}
