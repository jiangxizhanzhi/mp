package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName ContainerGetResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@NoArgsConstructor
public class ContainerGetResult implements CloudHttpData {
    private String code;
    private Data data;
    private Long timestamp;
    private Double time_elapse;

    @Getter
    @Setter
    public static class Data {
        private String url;
    }
}
