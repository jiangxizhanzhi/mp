package cn.cyberpeace.mp.common.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_common_option")
public class OptionModel extends BaseModel {
    private String code;
    private String zhName;
    private String enName;
    private Integer sort;
    private String type;
    private String parentCode;
}

