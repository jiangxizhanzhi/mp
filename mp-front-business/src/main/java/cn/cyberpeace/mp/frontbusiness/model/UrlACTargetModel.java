package cn.cyberpeace.mp.frontbusiness.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_url_ac_target")
@ApiModel("接口权限关系表")
public class UrlACTargetModel extends BaseModel implements Serializable {

    private String urlacId;

    private String target;

    @DefaultStr
    private String group;

}
