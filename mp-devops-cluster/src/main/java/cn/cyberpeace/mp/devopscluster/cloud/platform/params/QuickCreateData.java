package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;
import java.util.List;

/**
 * @ClassName QuickCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpData
 */
@Data
public class QuickCreateData implements SnakeHttpData {
    private String quickName;
    private String deviceUuid;
    private List<String> quickCreateParams;
}
