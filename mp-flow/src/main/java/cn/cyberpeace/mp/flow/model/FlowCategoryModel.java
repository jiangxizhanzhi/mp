package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_flow_category")
public class FlowCategoryModel extends BaseModel {
    private Integer code;
    private String name;
}
