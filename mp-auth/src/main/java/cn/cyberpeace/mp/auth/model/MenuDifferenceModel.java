package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_target_difference")
public class MenuDifferenceModel extends BaseModel {

    /**
     * 系统被区分后的ID存放字段
     */
    private String difference;
}
