 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExtGateway.java.
 // File size 930.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ExtGateway
 * @Description 扩展网关信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("网关信息")
public class ExtGateway implements Serializable {

    private List<PortConfig> portConfigs;

    /**
     * console协议配置
     */
    private AccessProtocol consoleConfig;

    public AccessProtocol getConsoleConfig() {
        return Objects.nonNull(consoleConfig) && StringUtils.isBlank(consoleConfig.getProtocol()) ?  null : consoleConfig;
    }
}
