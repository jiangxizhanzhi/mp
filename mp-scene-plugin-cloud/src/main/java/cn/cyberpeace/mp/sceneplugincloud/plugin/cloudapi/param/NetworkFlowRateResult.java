package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @Description: 环境总速率响应数据
 * @Author: guquan
 * @Date：2023/5/30 10:19
 */
@Data
public class NetworkFlowRateResult implements CloudHttpData {

    /**
     * 速率值列表
     */
    private List<Long> sumRateList;

    /**
     * 时间间隔，单位分钟
     */
    private Integer timeInt;

}
