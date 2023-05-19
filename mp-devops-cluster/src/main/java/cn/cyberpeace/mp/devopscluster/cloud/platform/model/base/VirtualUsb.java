package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 虚拟USB对象
 * @Author: guquan
 * @Date：2023/4/17 9:01
 */
@Data
@ApiModel("虚拟USB对象")
public class VirtualUsb extends BaseModel {

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
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 容量
     */
    @ApiModelProperty("容量")
    private Integer capacity;

    /**
     * 类型
     */
    @ApiModelProperty("类型，storage：存储")
    private String virtualUsbType;

    /**
     * 状态
     */
    @ApiModelProperty("状态，no：待编辑 succ：已保存")
    private String virtualUsbStatus;
}
