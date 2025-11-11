package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 接口管理对象
 * @Author guquan
 * @Date 2025-09-08 16:17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_interface")
public class InterfaceModel extends BaseModel {

    /**
     * ip或域名
     */
    @ApiModelProperty(value = "ip或域名")
    @DefaultStr
    private String ip;

    /**
     * 接口路径
     */
    @ApiModelProperty(value = "接口路径")
    @DefaultStr
    private String path;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @DefaultStr
    private String desc;

}
