// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name VirtualMachineExecuteResult.java.
// File size 599.
// File create date Fri Jul 12 12:55:25 CST 2024.
// File update date Fri Jul 12 12:55:25 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName VirtualMachineExecuteResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVmResult {
    private String id;

    private List<Nic> nics;

    @Getter
    @Setter
    public static class Nic {
        private List<String> ipv4;
    }
}
