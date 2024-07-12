 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowClassificationFromJsonModel.java.
 // File size 745.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
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
