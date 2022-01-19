package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
