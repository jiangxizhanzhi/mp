// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name VirtualMachineExecCommandData.java.
// File size 568.
// File create date Fri Jul 12 12:55:26 CST 2024.
// File update date Fri Jul 12 12:55:26 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName VirtualMachineExecCommandData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see VirtualMachinePrivateOperateData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualMachineExecCommandQgaData implements CloudHttpData {
    private String vmId;
    @ApiModelProperty(value = "执行命令")
    private String execCmd;
    @ApiModelProperty(value = "命令参数")
    private List<String> args;
    @ApiModelProperty(value = "")
    private List<String> envs;
}