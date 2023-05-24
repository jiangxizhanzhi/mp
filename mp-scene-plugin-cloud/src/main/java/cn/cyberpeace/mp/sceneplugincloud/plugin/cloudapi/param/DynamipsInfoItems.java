package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DynamipsInfoItems implements SnakeHttpParam {

    private Map<String, DynamipsInfoItem> items;

    @Data
    public static class DynamipsInfoType {
        private String type;
    }

    @Data
    public static class DynamipsInfoItem {
        private List<Integer> portSumList;
        private List<String> portList;
    }
}
