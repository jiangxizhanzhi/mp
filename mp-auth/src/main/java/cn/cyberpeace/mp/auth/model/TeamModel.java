package cn.cyberpeace.mp.auth.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_team")
@Builder
public class TeamModel extends BaseModel {


    //战队名称
    private String name;
    //logo
    @DefaultStr
    private String logo;
    //描述
    @DefaultStr
    private String description;
    @DefaultStr
    private String tag;
    //人员数量
    @DefaultInt
    private Integer userCount = 0;
    //创建者
    private String creator;
}
