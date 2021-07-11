package cn.cyberpeace.mp.frontbusiness.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
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
@Table("mp_auth_url_ac")
@ApiModel("接口访问控制表")
public class UrlACModel extends BaseModel implements Serializable {

    private String url;

    @DefaultInt(0)
    private Integer isPattern;

    private String methods;

    @DefaultStr
    private String desc;

    @DefaultStr
    private String group;
}
