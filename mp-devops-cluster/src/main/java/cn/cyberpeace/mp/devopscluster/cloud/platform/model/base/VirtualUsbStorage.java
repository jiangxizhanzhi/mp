package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 虚拟USB存储信息
 * @Author: guquan
 * @Date：2023/4/18 10:30
 */
@Data
@ApiModel("虚拟USB存储信息")
public class VirtualUsbStorage extends BaseModel {

    /**
     * 容量
     */
    @ApiModelProperty("容量")
    private Integer capacity;

    /**
     * 已使用
     */
    @ApiModelProperty("已使用")
    private Integer used;

    /**
     * 文件列表
     */
    @ApiModelProperty("文件列表")
    private VirtualUsbFileList files;
}
