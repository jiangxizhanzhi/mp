 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SerialPortConvertModel.java.
 // File size 804.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SerialPortConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPortConvertModel {

    @ApiModelProperty("TCP连接生存时间/分钟")
    private Integer comAliveT;

    @ApiModelProperty("最大连接数")
    private Integer comLocalP;

    @ApiModelProperty("本地端口")
    private Integer tcpLinkMax;

    @ApiModelProperty("串口交换机的IP地址")
    private String host;

    @ApiModelProperty("串口ID")
    private String interfaceId;
}
