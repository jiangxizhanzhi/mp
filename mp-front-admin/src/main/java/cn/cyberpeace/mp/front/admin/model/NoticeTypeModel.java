package cn.cyberpeace.mp.front.admin.model;

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
@Table("mp_front_admin_notice_type")
public class NoticeTypeModel extends BaseModel {
    private Integer code;
    private String chName;
    private String enName;
}
