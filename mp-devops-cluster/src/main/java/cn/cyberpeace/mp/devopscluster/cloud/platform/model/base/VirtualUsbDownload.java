package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 虚拟USB下载对象
 * @Author: guquan
 * @Date：2023/4/18 15:48
 */
@Data
@ApiModel("虚拟USB下载对象")
public class VirtualUsbDownload extends BaseModel {

    /**
     * 文件下载地址
     */
    @ApiModelProperty("文件下载地址")
    private String nginxFileUrl;
}
