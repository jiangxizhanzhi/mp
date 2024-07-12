 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualUsbDownload.java.
 // File size 629.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualUsbDownload
 * @Description 虚拟USB下载对象
 * @Author guquan
 * @Date 2023/4/18 15:48
 * @Version 1.0.0
 * @see BaseModel
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
