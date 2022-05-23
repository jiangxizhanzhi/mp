package cn.cyberpeace.mp.front.business.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_url_ac_target")
@ApiModel("接口权限关系表")
public class UrlACTargetModel extends BaseModel implements Serializable {

    private String urlacId;

    private String target;

}
