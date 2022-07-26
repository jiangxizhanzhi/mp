package cn.cyberpeace.mp.devops.log.model;

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
@Table("mp_devops_log_type")
public class OpTypeModel extends BaseModel{
    private Integer code;
    private String name;
}
