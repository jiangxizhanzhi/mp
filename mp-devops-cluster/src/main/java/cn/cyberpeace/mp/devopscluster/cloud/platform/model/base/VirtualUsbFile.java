 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualUsbFile.java.
 // File size 825.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualUsbFile
 * @Description 虚拟USB存储文件对象
 * @Author guquan
 * @Date 2023/4/18 10:19
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@ApiModel("虚拟USB存储文件对象")
public class VirtualUsbFile extends BaseModel {

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * 文件大小
     */
    @ApiModelProperty("文件大小")
    private String fileSize;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private String fileDate;
}
