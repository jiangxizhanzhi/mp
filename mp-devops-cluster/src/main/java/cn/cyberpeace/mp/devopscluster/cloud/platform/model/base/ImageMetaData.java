package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 镜像元数据
 *
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

    @ApiModelProperty("环境变量字符串格式")
    private String environmentStr;

    @ApiModelProperty("容器其他参数的字符串格式")
    private String hostConfigExStr;

    @ApiModelProperty("容器interactive参数")
    private Boolean interactive;

    @ApiModelProperty("容器tty参数")
    private Boolean tty;

    @ApiModelProperty("bios启动参数")
    private String hwFirmwareType;

    @ApiModelProperty("界面展示的高级选项开关")
    private Boolean options;
}
