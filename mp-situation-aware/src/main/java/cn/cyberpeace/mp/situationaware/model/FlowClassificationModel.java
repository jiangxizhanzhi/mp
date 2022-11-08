package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流量分类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_situation_flow_classification")
public class FlowClassificationModel extends BaseModel {

    private String name;

    private String categoryEn;

    private String categoryZh;

    private Integer severity;
}
