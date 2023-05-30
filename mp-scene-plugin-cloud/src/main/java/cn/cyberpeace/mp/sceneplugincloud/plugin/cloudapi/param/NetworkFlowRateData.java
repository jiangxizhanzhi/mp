package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @Description: 环境总速率同步流量端口数据
 * @Author: guquan
 * @Date：2023/5/30 10:17
 */
@Data
public class NetworkFlowRateData implements CloudHttpData {

    /**
     * 端口列表
     */
    private List<String> ports;

    /**
     * 统计时间，单位S
     */
    private Long time;
}
