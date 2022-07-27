package cn.cyberpeace.mp.common.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiModel extends BaseModel {
    private Integer code;
    private String chName;
    private String enName;
}
