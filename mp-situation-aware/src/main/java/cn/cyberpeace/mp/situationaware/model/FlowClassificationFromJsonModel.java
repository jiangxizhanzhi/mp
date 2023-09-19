package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.bo.AttackClassificationBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName FlowClassificationfromJsonModel
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/19 13:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowClassificationFromJsonModel {
    private List<FlowType> classifications;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlowType {
        private String type_cn;
        private String type_en;
        private List<AttackClassificationBO.Classification> flow_events;
    }
}
