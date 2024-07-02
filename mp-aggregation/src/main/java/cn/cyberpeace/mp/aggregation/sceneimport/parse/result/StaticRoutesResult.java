package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Data;

/**
 * @ClassName StaticRoutesResult
 * @Description
 * @Author maojun
 * @Date 2022/1/21
 * @Version 1.0.0
 */
@Data
public class StaticRoutesResult {
    private String destination;
    private String gateway;
    private String nexthopNode;
}
