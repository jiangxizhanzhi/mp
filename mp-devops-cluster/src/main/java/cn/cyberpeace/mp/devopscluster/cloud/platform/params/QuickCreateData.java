package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;
import java.util.List;

@Data
public class QuickCreateData implements SnakeHttpData {
    private String quickName;
    private String deviceUuid;
    private List<String> quickCreateParams;
}
