package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

import java.util.List;

/**
 * @Description: 环境总速率查询条件
 * @Author: guquan
 * @Date：2023/5/30 10:35
 */
@Data
public class NetworkFlowRateParam implements CloudHttpParam {

    /**
     * 端口列表
     */
    private List<String> ports;

    /**
     * 统计时间，单位S
     */
    private Long time;
}
