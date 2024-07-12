 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProcessState.java.
 // File size 748.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ProcessState
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/29 20:14
 * @Version 1.0.0
 */
@Data
public class ProcessState {

    @ApiModelProperty("进程号")
    private String pid;

    @ApiModelProperty("父进程号")
    private String ppid;

    @ApiModelProperty("进程用户")
    private String user;

    @ApiModelProperty("内存占用")
    private Double memory;

    @ApiModelProperty("命令")
    private String cmd;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("创建时间")
    private String createTime;

}
