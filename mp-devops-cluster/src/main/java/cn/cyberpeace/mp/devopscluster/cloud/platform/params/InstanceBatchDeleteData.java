package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;

import java.util.List;

@Data
public class InstanceBatchDeleteData implements SnakeHttpData {
    private List<InstanceDeleteData> uuids;
}