package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Data;


@Data
public class StaticRoutesResult {
    private String destination;
    private String gateway;
    private String nexthopNode;
}
