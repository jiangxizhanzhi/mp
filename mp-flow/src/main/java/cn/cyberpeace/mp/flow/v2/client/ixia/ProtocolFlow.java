package cn.cyberpeace.mp.flow.v2.client.ixia;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/30
 */
@Data
public class ProtocolFlow {
    private String name ;
    private Integer weight;
    private List<Flow> flowList;
}
