package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 镜像元数据
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ImageMetaData implements Serializable {

    /**
     * 磁盘控制器类型
     */
    @ApiModelProperty("磁盘控制器类型")
    private String hwDiskBus;

    /**
     * 虚拟网卡型号
     */
    @ApiModelProperty("虚拟网卡型号")
    private String hwVifModel;

    /**
     * 视频图像驱动
     */
    @ApiModelProperty("视频图像驱动")
    private String hwVideoModel;
}
