package cn.cyberpeace.mp.aggregation.response;

import lombok.Data;


/**
 * @Description
 * @Author zhanglin
 * @Date 2023/5/17
 */
@Data
public class FlowResponse {
    private FlowData data;

    @Data
    public static class FlowData {
        Integer total;
    }
}