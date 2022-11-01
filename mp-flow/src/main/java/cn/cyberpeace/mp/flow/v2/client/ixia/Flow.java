package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/30
 */
@Data
public class Flow<T> {
    private String name;
    private String srcIp;
    private String dstIp;
    private Integer dportport;
    private Integer srcPort;
    private List<T> actions;
}
