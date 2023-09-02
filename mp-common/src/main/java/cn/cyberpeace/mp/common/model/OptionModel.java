package cn.cyberpeace.mp.common.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
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
    @DefaultStr
    private String code;
    @DefaultStr
    private String zhName;
    @DefaultStr
    private String enName;
    @DefaultInt
    private Integer sort;
    @DefaultStr
    private String type;
    @DefaultStr
    private String parentCode;
    @DefaultStr
    private String meta;
}

