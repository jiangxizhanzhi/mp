package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 实体USB对象
 * @Author: guquan
 * @Date：2023/4/26 10:55
 */
@Data
@ApiModel("实体USB对象")
public class EntityUsb {

    /**
     * UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 主机名
     */
    @ApiModelProperty("主机名")
    private String host;
}
