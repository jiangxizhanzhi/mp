package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
* 载荷同步设置表
* @TableName mp_devops_load_source
*/
@Data
@Table("mp_devops_load_source")
public class DevopsLoadSourceModel extends BaseModel {

    /**
     * 载荷主机
     */
    @ApiModelProperty("载荷主机")
    private String ip;

    /**
    * 同步协议
    */
    @ApiModelProperty("同步协议")
    private String synchronizationProtocol;
    /**
    * 认证方式(1:密码认证 2:cookie)
    */
    @ApiModelProperty("认证方式(1:密码认证 2:cookie)")
    private String authenticationType;
    /**
    * 用户名
    */
    @ApiModelProperty("用户名")
    private String userName;
    /**
    * 密码
    */
    @ApiModelProperty("密码")
    private String password;
    /**
    * cookie
    */
    @ApiModelProperty("cookie")
    private String cookie;
    /**
    * 创建人Id
    */
    @ApiModelProperty("创建人Id")
    private String creatorId;


}
