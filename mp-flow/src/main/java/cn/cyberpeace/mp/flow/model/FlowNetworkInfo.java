package cn.cyberpeace.mp.flow.model;

import lombok.Data;

/**
 * @Description: 流量网络信息
 * @Author qiaokang
 * @Date：2022/4/28 13:48
 */
@Data
public class FlowNetworkInfo {

    /**
     * ip
     */
    private String ip;

    /**
     * 网络id
     */
    private String networkId;

    /**
     * mac
     */
    private String mac;

}
