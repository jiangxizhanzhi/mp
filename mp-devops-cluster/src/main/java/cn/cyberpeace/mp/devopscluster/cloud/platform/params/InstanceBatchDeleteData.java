package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName InstanceBatchDeleteData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see SnakeHttpData
 */
@Data
public class InstanceBatchDeleteData implements SnakeHttpData {
    private List<InstanceDeleteData> uuids;
}